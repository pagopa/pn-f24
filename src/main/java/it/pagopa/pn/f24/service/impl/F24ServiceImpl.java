
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.F24Service;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.util.Sha256Handler;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class F24ServiceImpl implements F24Service {
    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";
    private static final long SECONDS_TO_DELAY = 4;
    private static final int MAX_PATH_TOKENS_DIMENSION = 4;

    private final F24Generator f24Generator;
    private final PnSafeStorageClientImpl pnSafeStorageClient;
    private final EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer;
    private final MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;
    private final F24MetadataSetDao f24MetadataSetDao;
    private final JsonService jsonService;
    private final F24Config f24Config;


    public F24ServiceImpl(F24Generator f24Generator, PnSafeStorageClientImpl pnSafeStorageClient, EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer, MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer, F24MetadataSetDao f24MetadataSetDao, JsonService jsonService, F24Config f24Config) {
        this.f24Generator = f24Generator;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.metadataValidationEndedEventProducer = metadataValidationEndedEventProducer;
        this.validateMetadataSetEventProducer = validateMetadataSetEventProducer;
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.jsonService = jsonService;
        this.f24Config = f24Config;
    }

    @Override
    public Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> monoSaveF24Request) {
        log.info("Starting saveMetadata with cxId: {} and setId: {}", xPagopaF24CxId, setId);
        return monoSaveF24Request
                .doOnNext(saveF24Request -> validateSaveMetadataRequest(saveF24Request, setId))
                .flatMap(
                    saveF24Request -> this.f24MetadataSetDao.getItem(setId)
                        .map(f24Metadata -> handleExistingMetadata(f24Metadata, saveF24Request))
                        .switchIfEmpty(Mono.defer(() -> handleNewMetadata(saveF24Request, xPagopaF24CxId)))
                )
                .doOnNext(unused -> log.info("Ended saveMetadata with success"));
    }

    private void validateSaveMetadataRequest(SaveF24Request saveF24Request, String pathSetId) {
        if(!pathSetId.equalsIgnoreCase(saveF24Request.getSetId())) {
            throw new PnBadRequestException(
                    "incongruent setId",
                    PnF24ExceptionCodes.ERROR_MESSAGE_F24_SET_ID_INCONGRUENT,
                    PnF24ExceptionCodes.ERROR_CODE_F24_SET_ID_INCONGRUENT
            );
        }

        saveF24Request.getF24Items().forEach(
                saveF24Item -> {
                    if(!checkPathTokensMaxDimension(saveF24Item.getPathTokens())) {
                        throw new PnBadRequestException(
                            "max dimension of pathTokens",
                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_PATH_TOKENS_DIMENSION_NOT_ALLOWED, MAX_PATH_TOKENS_DIMENSION),
                            PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED
                        );
                    }
                }
        );

        if(!checkPathTokensHaveSameDimension(saveF24Request.getF24Items())) {
            throw new PnBadRequestException("pathTokens with different lengths are not allowed", "pathTokens with different lengths are not allowed", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED);
        }

        if(!checkPathTokensUniqueness(saveF24Request.getF24Items())) {
            throw new PnBadRequestException("pathTokens are not unique", "pathTokens are not unique", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED);
        }
    }

    private boolean checkPathTokensMaxDimension(List<String> pathTokens) {
        return pathTokens.size() < MAX_PATH_TOKENS_DIMENSION;
    }

    private boolean checkPathTokensHaveSameDimension(List<SaveF24Item> f24Items) {
        int firstPathTokensSize = f24Items.get(0).getPathTokens().size();
        return f24Items.stream().noneMatch(f24Item -> f24Item.getPathTokens().size() != firstPathTokensSize);
    }

    private boolean checkPathTokensUniqueness(List<SaveF24Item> f24Items) {
        Set<String> pathTokensSet = new HashSet<>();
        for(SaveF24Item f24Item : f24Items) {
            String pathTokensInString = Utility.convertPathTokensList(f24Item.getPathTokens());
            pathTokensSet.add(pathTokensInString);
        }

        return pathTokensSet.size() == f24Items.size();
    }

/*
    //TODO check tipo di dato in ingresso
    public Mono<FileDownloadInfo> checkF24File(F24File f24File) {
        //TODO chiamata f24filedao
        return f24MetadataDao.getItem(f24File.getPk(), f24File.getSk())
                .map(f -> {
                    //chiamata a safestorage per scaricare il file?
                    return pnSafeStorageClient.getFile(f.getFileKey(), false);
                })
                //record non trovato sul safestorage, quindi ->
                .onErrorResume()
        // if record non found, respond with HTTP NOT FOUND
    }

    //TODO cost value?
    //todo: check object type
    private F24Item addCost(F24Metadata f24Metadata, Integer cost) {
        F24Standard f24Item = new F24Standard();
        if (cost != null) {
            if (f24Metadata.getApplyCost() != null) {
                if (f24Metadata.getApplyCost()) {
                    if (F24Standard != null) {
                        //todo: sum of cost and debit(String)
                        f24Item.getInps().getRecords().get(0).setDebit(cost.toString());

                    } else if (F24Simplified != null) {

                    } else if (F24Elid != null) {

                    } else if)F24Excise != null){

                    } //todo:error otherwise
                    // else error

                    return f24Item;
                }
            }
        } else {
            //todo: error no cost to apply

        }
        return f24Metadata;
    }
 */

    private RequestAccepted handleExistingMetadata(F24MetadataSet f24MetadataSet, SaveF24Request saveF24Request) {
        String checksum = createChecksumFromSaveF24Items(saveF24Request.getF24Items());
        if (!f24MetadataSet.getSha256().equalsIgnoreCase(checksum)) {
            throw new PnConflictException("Conflict", "setId already processed with different metadata", PnF24ExceptionCodes.ERROR_CODE_F24_SAVE_METADATA_CONFLICT);
        }
        return createRequestAcceptedDto();
    }

    private String createChecksumFromSaveF24Items(List<SaveF24Item> saveF24Items) {
        String saveF24ItemsInJson = jsonService.stringifyObject(saveF24Items);
        return Sha256Handler.computeSha256(saveF24ItemsInJson);
    }

    private RequestAccepted createRequestAcceptedDto() {
        RequestAccepted requestAccepted = new RequestAccepted();
        requestAccepted.setDescription(REQUEST_ACCEPTED_DESCRIPTION);
        requestAccepted.setStatus(REQUEST_ACCEPTED_STATUS);
        return requestAccepted;
    }

    private Mono<RequestAccepted> handleNewMetadata(SaveF24Request saveF24Request, String cxId) {
        return Mono.fromRunnable(() -> validateMetadataSetEventProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(saveF24Request.getSetId())))
                .doOnError(throwable -> log.warn("Error sending f24SaveEvent to Queue", throwable))
                .then(tryToSaveF24MetadataSet(saveF24Request, cxId));

    }

    private Mono<RequestAccepted> tryToSaveF24MetadataSet(SaveF24Request saveF24Request, String cxId) {
        return this.f24MetadataSetDao.putItemIfAbsent(this.createF24Metadata(saveF24Request, cxId))
            .thenReturn(createRequestAcceptedDto())
            .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
            .onErrorResume(ConditionalCheckFailedException.class, e -> {
                log.debug("MetadataSet already saved with setId: {}", saveF24Request.getSetId());
                return this.f24MetadataSetDao.getItem(saveF24Request.getSetId())
                        .map(f24MetadataSet -> handleExistingMetadata(f24MetadataSet, saveF24Request));
            });
    }

    private F24MetadataSet createF24Metadata(SaveF24Request saveF24Request, String cxId) {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId(saveF24Request.getSetId());
        f24MetadataSet.setCreatorCxId(cxId);
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setHaveToSendValidationEvent(false);
        f24MetadataSet.setValidationEventSent(false);
        f24MetadataSet.setCreated(Instant.now());
        String checksum = createChecksumFromSaveF24Items(saveF24Request.getF24Items());
        f24MetadataSet.setSha256(checksum);
        f24MetadataSet.setFileKeys(createF24MetadataItems(saveF24Request));
        f24MetadataSet.setTtl(Instant.now().plus(Duration.ofDays(3L)).getEpochSecond());
        return f24MetadataSet;
    }

    private Map<String, F24MetadataRef> createF24MetadataItems(SaveF24Request saveF24Request) {
        Map<String, F24MetadataRef> f24MetadataItemMap = new HashMap<>();
        saveF24Request.getF24Items().forEach(
                saveF24Item -> {
                    F24MetadataRef temp = new F24MetadataRef();
                    temp.setApplyCost(saveF24Item.getApplyCost());
                    temp.setSha256(saveF24Item.getSha256());
                    temp.setFileKey(saveF24Item.getFileKey());
                    String pathTokensKey = Utility.convertPathTokensList(saveF24Item.getPathTokens());
                    f24MetadataItemMap.put(pathTokensKey, temp);
                }
        );

        return f24MetadataItemMap;
    }

    @Override
    public Mono<RequestAccepted> validate(String cxId, String setId) {
        log.info("Validate request for metadata with setId {} and cxId {}", setId, cxId);
        return getMetadataSet(setId)
                .flatMap(f24Metadata -> {
                    if (f24Metadata.getStatus().equals(F24MetadataStatus.TO_VALIDATE)) {
                        return handleMetadataToValidate(f24Metadata, cxId);
                    } else {
                        return sendValidationEndedEventAndUpdateMetadataSet(f24Metadata, cxId);
                    }
                })
                .thenReturn(createRequestAcceptedDto())
                .doOnNext(unused -> log.info("Validate request ended with success"));
    }

    private Mono<F24MetadataSet> getMetadataSet(String setId) {
        return f24MetadataSetDao.getItem(setId)
                .switchIfEmpty(Mono.error(
                    new PnNotFoundException(
                            "MetadataSet not found",
                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId),
                            PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                    )
                ));
    }

    private Mono<Void> handleMetadataToValidate(F24MetadataSet f24MetadataSet, String cxId) {
        log.debug("MetadataSet with setId {} is in state TO_VALIDATE", f24MetadataSet.getSetId());

        return updateHaveToSendValidationEventAndRemoveTtl(f24MetadataSet, cxId)
            .flatMap(this::checkIfValidationOnQueueHasEnded);
    }
    private Mono<F24MetadataSet> updateHaveToSendValidationEventAndRemoveTtl(F24MetadataSet f24MetadataSet, String cxId) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setTtl(null);
        f24MetadataSet.setValidatorCxId(cxId);
        return f24MetadataSetDao.updateItem(f24MetadataSet);
    }
    private Mono<Void> checkIfValidationOnQueueHasEnded(F24MetadataSet f24MetadataSet) {
        String setId = f24MetadataSet.getSetId();
        log.debug("Checking if validation on queue of MetadataSet with setId {} has ended", setId);

        return f24MetadataSetDao.getItem(setId, true)
            .flatMap(f24MetadataSetConsistent -> {
                log.debug("MetadataSet obtained with consistent read has status {}", f24MetadataSetConsistent.getStatus().getValue());
                if(f24MetadataSetConsistent.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
                    return sendValidationEndedEventAndUpdateMetadataSet(f24MetadataSetConsistent, f24MetadataSet.getValidatorCxId());
                }

                return Mono.empty();
            });
    }

    private Mono<Void> sendValidationEndedEventAndUpdateMetadataSet(F24MetadataSet f24MetadataSet, String validatorCxId) {
        String setId = f24MetadataSet.getSetId();
        log.debug("Sending validation ended event for metadata with setId : {} and validatorCxId : {}", setId, validatorCxId);

        PnF24MetadataValidationEndEvent event = PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(validatorCxId, setId, f24MetadataSet.getValidationResult());
        return Mono.fromRunnable(() -> metadataValidationEndedEventProducer.sendEvent(event))
                .doOnError(throwable -> log.warn("Error sending validation ended event", throwable))
                .then(Mono.defer(() -> setValidationEventSentOnMetadataSet(f24MetadataSet, validatorCxId)));
    }

    private Mono<Void> setValidationEventSentOnMetadataSet(F24MetadataSet f24MetadataSet, String validatorCxId) {
        //Indifferente a questo punto, lo setto solo per coerenza. (HaveToSendValidationEvent)
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setValidationEventSent(true);
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setTtl(null);
        f24MetadataSet.setValidatorCxId(validatorCxId);
        return f24MetadataSetDao.updateItem(f24MetadataSet)
                .then();
    }

    @Override
    public Mono<byte[]> generatePDF(String iun, String recipientIndex, String attachmentIndex) {
        log.info("generating pdf for f24 with iun: {} recipientIndex: {} attachmentIndex: {}", iun, recipientIndex, attachmentIndex);
        //Se necessario includere anche il costo della notifica, allora va rivisto cambiando l'ordine delle chiamate

        return null;
    }

}