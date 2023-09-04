/*
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.dto.metadata.*;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.dto.metadata.F24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.F24MetadataDao;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.F24Service;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static it.pagopa.pn.f24.util.Utility.objectToJsonString;

@Service
@Slf4j
public class F24ServiceImpl implements F24Service {
    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";

    private final F24Generator f24Generator;
    private final PnSafeStorageClientImpl pnSafeStorageClient;

    private final MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer;

    private final F24MetadataDao f24MetadataDao;


    public F24ServiceImpl(F24Generator f24Generator, PnSafeStorageClientImpl pnSafeStorageClient, MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer, F24MetadataDao f24MetadataDao) {
        this.f24Generator = f24Generator;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.internalMetadataEventMomProducer = internalMetadataEventMomProducer;
        this.f24MetadataDao = f24MetadataDao;
    }

    @Override
    public Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> monoSaveF24Request) {
        //TODO valutare che validazione sintattica deve essere eseguita
        //validateRequest();
        return this.f24MetadataDao.getItem(setId, xPagopaF24CxId)
                .flatMap(f24Metadata -> handleExistingMetadata(f24Metadata, monoSaveF24Request))
                .switchIfEmpty(handleNewMetadata(monoSaveF24Request, xPagopaF24CxId));
    }

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


    private Mono<RequestAccepted> handleExistingMetadata(F24Metadata f24Metadata, Mono<SaveF24Request> monoSaveF24Request) {
        return monoSaveF24Request.flatMap(saveF24Request -> {
            String checksum = createChecksumFromF24MetadataItems(saveF24Request.getF24Items());
            if (!f24Metadata.getSha256().equalsIgnoreCase(checksum)) {
                return Mono.error(new PnConflictException("setId already processed with different metadata", "Conflict", PnF24ExceptionCodes.ERROR_CODE_F24_SAVE_METADATA_CONFLICT));
            }
            return Mono.just(createRequestAcceptedDto());
        });
    }

    private String createChecksumFromF24MetadataItems(List<F24Item> f24Items) {
        String f24MetadataItemsInJson = objectToJsonString(f24Items);
        return Sha256Handler.computeSha256(f24MetadataItemsInJson);
    }

    private RequestAccepted createRequestAcceptedDto() {
        RequestAccepted requestAccepted = new RequestAccepted();
        requestAccepted.setDescription(REQUEST_ACCEPTED_DESCRIPTION);
        requestAccepted.setStatus(REQUEST_ACCEPTED_STATUS);
        return requestAccepted;
    }

    private Mono<RequestAccepted> handleNewMetadata(Mono<SaveF24Request> monoSaveF24Request, String cxId) {
        Mono<Void> delayedQueueInsert = monoSaveF24Request
                .delayElement(Duration.ofSeconds(SECONDS_TO_DELAY))
                .log()
                .doOnNext(saveF24Request -> internalF24SqsProducer.sendInternalEvent(buildSaveMetadataEvent(saveF24Request, cxId)))
                .doOnError(throwable -> log.error("Error sending f24SaveEvent to Queue", throwable))
                .then();

        Mono<RequestAccepted> dbInsert = monoSaveF24Request
                .flatMap(saveF24Request -> this.f24MetadataDao.putItem(this.createF24Metadata(saveF24Request, cxId)))
                .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
                .log()
                .thenReturn(createRequestAcceptedDto());

        return Mono.zip(delayedQueueInsert, dbInsert)
                .map(Tuple2::getT2);


        return monoSaveF24Request
                .flatMap(saveF24Request -> this.f24MetadataDao.putItem(this.createF24Metadata(saveF24Request, cxId)).thenReturn(saveF24Request))
                .doOnError(throwable -> log.error("Error saving in f24Metadata Table", throwable))
                .flatMap(saveF24Request -> Mono.fromRunnable(() -> {
                    internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildSaveMetadataEvent(saveF24Request.getSetId(), cxId, saveF24Request.getF24Items()));
                }))
                .doOnError(throwable -> log.error("Error sending SaveMetadataEvent to Queue", throwable))
                .thenReturn(createRequestAcceptedDto());

    }

    private F24Metadata createF24Metadata(SaveF24Request saveF24Request, String cxId) {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setSetId(saveF24Request.getSetId());
        f24Metadata.setCxId(cxId);
        f24Metadata.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24Metadata.setHaveToSendValidationEvent(false);
        f24Metadata.setValidationEventSent(false);
        f24Metadata.setCreated(Instant.now());
        String checksum = createChecksumFromF24MetadataItems(saveF24Request.getF24Items());
        f24Metadata.setSha256(checksum);
        return f24Metadata;
    }

    @Override
    public Mono<RequestAccepted> validate(String xPagopaF24CxId, String setId) {
        log.info("Validate request for metadata with setId {} and cxId {}", setId, xPagopaF24CxId);
        return f24MetadataDao.getItem(setId, xPagopaF24CxId)
                .flatMap(f24Metadata -> {
                    if (f24Metadata.getStatus().equals(F24MetadataStatus.TO_VALIDATE)) {
                        return handleMetadataToValidate(f24Metadata);
                    } else {
                        internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(setId, xPagopaF24CxId));
                        return Mono.just(createRequestAcceptedDto());
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PnNotFoundException("Set id not found", "NOT_FOUND", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND))));
    }

    private Mono<RequestAccepted> handleMetadataToValidate(F24Metadata f24Metadata) {
        log.debug("metadata with setId {} and cxId {} is in state TO_VALIDATE", f24Metadata.getSetId(), f24Metadata.getCxId());
        f24Metadata.setHaveToSendValidationEvent(true);
        return f24MetadataDao.updateItem(f24Metadata)
                .flatMap(f24MetadataUpdated -> f24MetadataDao.getItem(f24MetadataUpdated.getSetId(), f24MetadataUpdated.getCxId(), true))
                .map(f24MetadataConsistent -> {
                    if (f24MetadataConsistent.getStatus().equals(F24MetadataStatus.SAVED)) {
                        internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(f24MetadataConsistent.getSetId(), f24MetadataConsistent.getCxId()));
                    }

                    return createRequestAcceptedDto();
                });
    }

    @Override
    public Mono<byte[]> generatePDF(String iun, String recipientIndex, String attachmentIndex) {
        log.info("generating pdf for f24 with iun: {} recipientIndex: {} attachmentIndex: {}", iun, recipientIndex, attachmentIndex);
        //Se necessario includere anche il costo della notifica, allora va rivisto cambiando l'ordine delle chiamate

        return null;
    }

}
*/