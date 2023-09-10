
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
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
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.F24Service;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static it.pagopa.pn.f24.util.Utility.*;

@Service
@Slf4j
public class F24ServiceImpl implements F24Service {
    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";
    private static final long SECONDS_TO_DELAY = 4;

    private final F24Generator f24Generator;
    private final PnSafeStorageClientImpl pnSafeStorageClient;
    private final EventBridgeProducer<PnF24AsyncEvent> metadataValidationEventProducer;

    private final MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer;

    private final F24MetadataSetDao f24MetadataSetDao;


    public F24ServiceImpl(F24Generator f24Generator, PnSafeStorageClientImpl pnSafeStorageClient, EventBridgeProducer<PnF24AsyncEvent> metadataValidationEventProducer, MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer, F24MetadataSetDao f24MetadataSetDao) {
        this.f24Generator = f24Generator;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.metadataValidationEventProducer = metadataValidationEventProducer;
        this.internalMetadataEventMomProducer = internalMetadataEventMomProducer;
        this.f24MetadataSetDao = f24MetadataSetDao;
    }

    @Override
    public Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> monoSaveF24Request) {
        log.info("Starting saveMetadata with cxId: {} and setId: {}", xPagopaF24CxId, setId);
        return monoSaveF24Request
                .doOnNext(this::validateRequest)
                .flatMap(
                    saveF24Request -> this.f24MetadataSetDao.getItem(setId, xPagopaF24CxId)
                        .map(f24Metadata -> handleExistingMetadata(f24Metadata, saveF24Request))
                        .switchIfEmpty(Mono.defer(() -> handleNewMetadata(saveF24Request, xPagopaF24CxId)))
                )
                .doOnNext(unused -> log.info("Ended saveMetadata with success"));
    }

    private void validateRequest(SaveF24Request saveF24Request) {
        if(!checkPathTokensHaveSameDimension(saveF24Request.getF24Items())) {
            throw new PnBadRequestException("pathTokens with different lengths are not allowed", "pathTokens with different lengths are not allowed", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED);
        }

        if(!checkPathTokensUniqueness(saveF24Request.getF24Items())) {
            throw new PnBadRequestException("pathTokens are not unique", "pathTokens are not unique", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED);
        }

    }

    private boolean checkPathTokensHaveSameDimension(List<SaveF24Item> f24Items) {
        int firstPathTokensSize = f24Items.get(0).getPathTokens().size();
        return f24Items.stream().noneMatch(f24Item -> f24Item.getPathTokens().size() != firstPathTokensSize);
    }

    private boolean checkPathTokensUniqueness(List<SaveF24Item> f24Items) {
        Set<String> pathTokensSet = new HashSet<>();
        for(SaveF24Item f24Item : f24Items) {
            String pathTokensInString = String.join("#", f24Item.getPathTokens());
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
        String saveF24ItemsInJson = objectToJsonString(saveF24Items);
        return Sha256Handler.computeSha256(saveF24ItemsInJson);
    }

    private RequestAccepted createRequestAcceptedDto() {
        RequestAccepted requestAccepted = new RequestAccepted();
        requestAccepted.setDescription(REQUEST_ACCEPTED_DESCRIPTION);
        requestAccepted.setStatus(REQUEST_ACCEPTED_STATUS);
        return requestAccepted;
    }

    private Mono<RequestAccepted> handleNewMetadata(SaveF24Request saveF24Request, String cxId) {
/*
        Mono<Void> delayedQueueInsert = Mono.delay(Duration.ofSeconds(SECONDS_TO_DELAY))
                .doOnNext(signal -> internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(saveF24Request.getSetId(), cxId)))
                .doOnError(throwable -> log.warn("Error sending f24SaveEvent to Queue", throwable))
                .then();

        Mono<Void> dbInsert = this.f24MetadataSetDao.putItem(this.createF24Metadata(saveF24Request, cxId))
                .doOnNext(unused -> log.debug("Saved record in pn-f24Metadata Table"))
                .doOnError(throwable -> log.warn("Error saving in f24Metadata Table", throwable))
                .then();

        return Mono.zip(dbInsert, delayedQueueInsert)
                .thenReturn(createRequestAcceptedDto());
*/
        return Mono.fromRunnable(() -> internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(saveF24Request.getSetId(), cxId)))
                .doOnError(throwable -> log.warn("Error sending f24SaveEvent to Queue", throwable))
                .then(this.f24MetadataSetDao.putItem(this.createF24Metadata(saveF24Request, cxId)))
                .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
                .thenReturn(createRequestAcceptedDto());

/*
        return this.f24MetadataDao.putItem(this.createF24Metadata(saveF24Request, cxId))
                .thenReturn(saveF24Request)
                .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
                .flatMap(saveF24RequestSaved -> Mono.fromRunnable(() -> internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildSaveMetadataEvent(saveF24RequestSaved.getSetId(), cxId, saveF24RequestSaved.getF24Items()))))
                .doOnError(throwable -> log.error("Error sending SaveMetadataEvent to Queue", throwable))
                .thenReturn(createRequestAcceptedDto());
*/
    }

    private F24MetadataSet createF24Metadata(SaveF24Request saveF24Request, String cxId) {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setPk(cxId + "#" + saveF24Request.getSetId());
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
                    String pathTokensKey = String.join("#", saveF24Item.getPathTokens());
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
                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_NOT_FOUND, setId, cxId),
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
        metadataValidationEventProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(cxId, setId, f24MetadataSet.getValidationResult()));

        //Indifferente a questo punto, lo setto solo per coerenza.
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setValidationEventSent(true);
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setTtl(null);
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