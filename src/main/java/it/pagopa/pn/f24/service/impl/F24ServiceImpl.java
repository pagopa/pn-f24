
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.PrepareF24Request;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.middleware.queue.producer.util.PreparePdfEventBuilder;
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
import java.util.stream.Collectors;

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
    private final MomProducer<PreparePdfEvent> preparePdfEventProducer;
    private final F24MetadataSetDao f24MetadataSetDao;
    private final F24FileRequestDao f24FileRequestDao;
    private final F24Config f24Config;

    private final JsonService jsonService;


    public F24ServiceImpl(F24Generator f24Generator, PnSafeStorageClientImpl pnSafeStorageClient, EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer, MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer, MomProducer<PreparePdfEvent> preparePdfEventProducer, F24MetadataSetDao f24MetadataSetDao, JsonService jsonService, F24FileRequestDao f24FileRequestDao, F24Config f24Config) {
        this.f24Generator = f24Generator;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.metadataValidationEndedEventProducer = metadataValidationEndedEventProducer;
        this.validateMetadataSetEventProducer = validateMetadataSetEventProducer;
        this.preparePdfEventProducer = preparePdfEventProducer;
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.jsonService = jsonService;
        this.f24FileRequestDao = f24FileRequestDao;
        this.f24Config = f24Config;
    }

    @Override
    public Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> monoSaveF24Request) {
        log.info("Starting saveMetadata with cxId: {} and setId: {}", xPagopaF24CxId, setId);
        return monoSaveF24Request
                .doOnNext(saveF24Request -> validateSaveMetadataRequest(saveF24Request, setId))
                .flatMap(
                    saveF24Request -> this.f24MetadataSetDao.getItem(setId, xPagopaF24CxId)
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
        return Mono.fromRunnable(() -> validateMetadataSetEventProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(saveF24Request.getSetId(), cxId)))
                .doOnError(throwable -> log.warn("Error sending f24SaveEvent to Queue", throwable))
                .then(tryToSaveF24MetadataSet(saveF24Request, cxId));

    }

    private Mono<RequestAccepted> tryToSaveF24MetadataSet(SaveF24Request saveF24Request, String cxId) {
        return this.f24MetadataSetDao.putItemIfAbsent(this.createF24Metadata(saveF24Request, cxId))
            .thenReturn(createRequestAcceptedDto())
            .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
            .onErrorResume(ConditionalCheckFailedException.class, e -> {
                log.debug("MetadataSet already saved with setId: {} cxId: {}", saveF24Request.getSetId(), cxId);
                return this.f24MetadataSetDao.getItem(saveF24Request.getSetId(), cxId)
                        .map(f24MetadataSet -> handleExistingMetadata(f24MetadataSet, saveF24Request));
            });
    }

    private F24MetadataSet createF24Metadata(SaveF24Request saveF24Request, String cxId) {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setCxId(cxId);
        f24MetadataSet.setSetId(saveF24Request.getSetId());
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
        return getMetadataSet(setId, cxId)
                .flatMap(f24Metadata -> {
                    if (f24Metadata.getStatus().equals(F24MetadataStatus.TO_VALIDATE)) {
                        return handleMetadataToValidate(f24Metadata);
                    } else {
                        return sendValidationEndedEventAndUpdateMetadataSet(f24Metadata);
                    }
                })
                .thenReturn(createRequestAcceptedDto())
                .doOnNext(unused -> log.info("Validate request ended with success"));
    }

    private Mono<F24MetadataSet> getMetadataSet(String setId, String cxId) {
        return f24MetadataSetDao.getItem(setId, cxId)
                .switchIfEmpty(Mono.error(
                    new PnNotFoundException(
                            "MetadataSet not found",
                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId, cxId),
                            PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                    )
                ));
    }

    private Mono<Void> handleMetadataToValidate(F24MetadataSet f24MetadataSet) {
        log.debug("metadata with setId {} and cxId {} is in state TO_VALIDATE", f24MetadataSet.getSetId(), f24MetadataSet.getCxId());

        return updateHaveToSendValidationEventAndRemoveTtl(f24MetadataSet)
            .flatMap(this::checkIfValidationOnQueueHasEnded);
    }
    private Mono<F24MetadataSet> updateHaveToSendValidationEventAndRemoveTtl(F24MetadataSet f24MetadataSet) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setTtl(null);
        return f24MetadataSetDao.updateItem(f24MetadataSet);
    }
    private Mono<Void> checkIfValidationOnQueueHasEnded(F24MetadataSet f24MetadataSet) {
        String setId = f24MetadataSet.getSetId();
        String cxId = f24MetadataSet.getCxId();
        log.debug("checking if MetadataSet with setId {} and cxId {} has been validated on queue", setId, cxId);

        return f24MetadataSetDao.getItem(setId, cxId, true)
            .flatMap(f24MetadataSetConsistent -> {
                log.debug("MetadataSet obtained with consistent read has status {}", f24MetadataSetConsistent.getStatus().getValue());
                if(f24MetadataSetConsistent.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
                    return sendValidationEndedEventAndUpdateMetadataSet(f24MetadataSetConsistent);
                }

                return Mono.empty();
            });
    }

    private Mono<Void> sendValidationEndedEventAndUpdateMetadataSet(F24MetadataSet f24MetadataSet) {
        String setId = f24MetadataSet.getSetId();
        String cxId = f24MetadataSet.getCxId();
        log.debug("Sending validation ended event for metadata with setId : {} and cxId : {}", setId, cxId);
        return Mono.fromRunnable(() -> metadataValidationEndedEventProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(cxId, setId, f24MetadataSet.getValidationResult())))
                .doOnError(throwable -> log.warn("Error sending validation ended event", throwable))
                .then(Mono.defer(() -> {
                    //Indifferente a questo punto, lo setto solo per coerenza. (HaveToSendValidationEvent)
                    f24MetadataSet.setHaveToSendValidationEvent(true);
                    f24MetadataSet.setValidationEventSent(true);
                    f24MetadataSet.setUpdated(Instant.now());
                    f24MetadataSet.setTtl(null);
                    return f24MetadataSetDao.updateItem(f24MetadataSet)
                            .then();
                }));
    }

    @Override
    public Mono<byte[]> generatePDF(String iun, String recipientIndex, String attachmentIndex) {
        log.info("generating pdf for f24 with iun: {} recipientIndex: {} attachmentIndex: {}", iun, recipientIndex, attachmentIndex);
        //Se necessario includere anche il costo della notifica, allora va rivisto cambiando l'ordine delle chiamate

        return null;
    }

    @Override
    public Mono<RequestAccepted> preparePDF(String xPagopaF24CxId, String requestId, Mono<PrepareF24Request> monoPrepareF24Request) {
        log.info("starting preparePdf with requestId {} for cxId {}", requestId, xPagopaF24CxId);
        return monoPrepareF24Request
                .flatMap(prepareF24Request -> validatePreparePdfRequest(prepareF24Request, xPagopaF24CxId))
                .flatMap(prepareF24Request -> f24FileRequestDao.getItem(xPagopaF24CxId, requestId)
                        .map(f24Request -> handleExistingRequest(f24Request, prepareF24Request))
                        .switchIfEmpty(Mono.defer(() -> handleNewPreparePdfRequest(xPagopaF24CxId, requestId, prepareF24Request)))
                );
    }

    private Mono<PrepareF24Request> validatePreparePdfRequest(PrepareF24Request prepareF24Request, String cxId) {
        if(!checkPathTokensMaxDimension(prepareF24Request.getPathTokens())) {
            return Mono.error(new PnBadRequestException(
                    "max dimension of pathTokens",
                    String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_PATH_TOKENS_DIMENSION_NOT_ALLOWED, MAX_PATH_TOKENS_DIMENSION),
                    PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED
            ));
        }

        //Cerco se nel set di metadata è presente almeno un metadata con path tokens indicato nel body della richiesta.
        return getMetadataSet(prepareF24Request.getSetId(), cxId)
                .doOnNext(f24MetadataSet -> {
                    String pathTokensString = Utility.convertPathTokensList(prepareF24Request.getPathTokens());
                    Map<String, F24MetadataRef> fileKeysByPathTokens = filterFileKeysByPathTokens(f24MetadataSet.getFileKeys(), pathTokensString);

                    if(fileKeysByPathTokens.isEmpty()) {
                        throw new PnNotFoundException(
                                "MetadataSet not found",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_NOT_FOUND, pathTokensString, prepareF24Request.getSetId(), cxId),
                                PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                        );
                    }

                    boolean metadataRequiresCost = checkIfFileKeysRequireApplyCosts(fileKeysByPathTokens);
                    if(metadataRequiresCost && prepareF24Request.getNotificationCost() == null) {
                        throw new PnBadRequestException("Invalid request payload", "Requested metadata require notificationCost", PnF24ExceptionCodes.ERROR_CODE_F24_PREPARE_PDF_VALIDATION_ERROR);
                    } else if (!metadataRequiresCost && prepareF24Request.getNotificationCost() != null) {
                        throw new PnBadRequestException("Invalid request payload", "Requested metadata don't require notificationCost", PnF24ExceptionCodes.ERROR_CODE_F24_PREPARE_PDF_VALIDATION_ERROR);
                    }
                })
                .thenReturn(prepareF24Request);
    }

    private Map<String, F24MetadataRef> filterFileKeysByPathTokens(Map<String, F24MetadataRef> fileKeys, String pathTokensInString) {
        return fileKeys.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(pathTokensInString))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean checkIfFileKeysRequireApplyCosts(Map<String, F24MetadataRef> fileKeys) {
        return fileKeys.entrySet().stream().anyMatch(entry -> entry.getValue().isApplyCost());
    }

    private RequestAccepted handleExistingRequest(F24Request f24Request, PrepareF24Request prepareF24Request) {
        log.debug("Handling prepare pdf request already existing");
        String pathTokensString = Utility.convertPathTokensList(prepareF24Request.getPathTokens());
        if(
                !f24Request.getSetId().equalsIgnoreCase(prepareF24Request.getSetId()) ||
                        !f24Request.getPathTokens().equalsIgnoreCase(pathTokensString) ||
                        !Objects.equals(f24Request.getCost(), prepareF24Request.getNotificationCost())
        ) {
            throw new PnConflictException("Conflict", "requestId already processed with different payload", PnF24ExceptionCodes.ERROR_CODE_F24_SAVE_REQUEST_CONFLICT);
        }
        return createRequestAcceptedDto();
    }


    private Mono<RequestAccepted> handleNewPreparePdfRequest(String xPagopaF24CxId, String requestId, PrepareF24Request prepareF24Request) {
        log.debug("Handling new prepare pdf request");
        return sendPreparePdfEvent(xPagopaF24CxId, requestId)
                .then(tryToSaveF24Request(xPagopaF24CxId, requestId, prepareF24Request));
    }

    private Mono<Void> sendPreparePdfEvent(String cxId, String requestId) {
        log.debug("Sending prepare pdf event for requestId={}",requestId);

        return Mono.fromRunnable(() -> preparePdfEventProducer.push(
                PreparePdfEventBuilder.buildPreparePdfEvent(cxId ,requestId)
        ))
        .doOnError(t -> log.warn("Error sending preparePdf event"))
        .then();
    }

    private Mono<RequestAccepted> tryToSaveF24Request(String xPagopaF24CxId, String requestId, PrepareF24Request prepareF24Request) {
        log.debug("Try to save F24Request");
        return f24FileRequestDao.putItemIfAbsent(buildF24Request(xPagopaF24CxId, requestId, prepareF24Request))
                .thenReturn(createRequestAcceptedDto())
                .doOnError(throwable -> log.error("Error saving in f24Request Table", throwable))
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.debug("Request already saved with requestId: {}", requestId);
                    return this.f24FileRequestDao.getItem(xPagopaF24CxId, requestId)
                            .map(f24Request -> handleExistingRequest(f24Request, prepareF24Request));
                });
    }

    private F24Request buildF24Request(String xPagopaF24CxId, String requestId, PrepareF24Request prepareF24Request) {
        F24Request f24Request = new F24Request();
        f24Request.setCxId(xPagopaF24CxId);
        f24Request.setRequestId(requestId);
        f24Request.setSetId(prepareF24Request.getSetId());
        f24Request.setCost(prepareF24Request.getNotificationCost());
        String pathTokens = Utility.convertPathTokensList(prepareF24Request.getPathTokens());
        f24Request.setPathTokens(pathTokens);
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setCreated(Instant.now());
        f24Request.setRecordVersion(0);
        f24Request.setFiles(new HashMap<>());
        if(f24Config.getRetentionForF24RequestsInDays() != null && f24Config.getRetentionForF24RequestsInDays() > 0) {
            f24Request.setTtl(Instant.now().plus(Duration.ofDays(f24Config.getRetentionForF24RequestsInDays())).getEpochSecond());
        }
        return f24Request;
    }

}