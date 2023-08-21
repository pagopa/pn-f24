package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.F24MetadataDao;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import lombok.CustomLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@CustomLog
public class SafeStorageEventService {

    private final F24MetadataDao f24MetadataDao;
    private final F24Config f24Config;
    private final MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer;

    public SafeStorageEventService(F24MetadataDao f24MetadataDao, F24Config f24Config, MomProducer<InternalMetadataEvent> internalMetadataEventMomProducer) {
        this.f24MetadataDao = f24MetadataDao;
        this.f24Config = f24Config;
        this.internalMetadataEventMomProducer = internalMetadataEventMomProducer;
    }

    public Mono<Void> handleSafeStorageResponse(FileDownloadResponse response) {
        String fileKey = response.getKey();
        HandleEventUtils.addCorrelationIdToMdc(fileKey);
        log.info("Async response received from service {} for {} with correlationId={}",
                PnSafeStorageClient.CLIENT_NAME, PnSafeStorageClient.UPLOAD_FILE_CONTENT, fileKey);
        final String processName = PnSafeStorageClient.UPLOAD_FILE_CONTENT + " response handler";
        try {
            log.logStartingProcess(processName);

            if(response.getDocumentType().equalsIgnoreCase(f24Config.getSafeStorageMetadataDocType())) {
                return tryToUpdateF24MetadataTable(response)
                        .doOnNext(unused -> log.logEndingProcess(processName));
            } else if(response.getDocumentType().equalsIgnoreCase(f24Config.getSafeStorageF24DocType())) {
                return handleF24FileKey(response)
                        .doOnNext(unused -> log.logEndingProcess(processName));
            } else {
                log.warn("Unsupported document type");
                log.logEndingProcess(processName, false, "Unsupported document type");
                return Mono.empty();
            }
        } catch (Exception ex){
            log.logEndingProcess(processName, false, ex.getMessage());
            throw ex;
        }
    }
    private Mono<Void> tryToUpdateF24MetadataTable(FileDownloadResponse response) {
        return f24MetadataDao.getItemByFileKey(response.getKey())
                .map(this::updateStatusToSaved)
                .flatMap(f24MetadataDao::updateItem)
                .flatMap(this::handleValidationEvent)
                .switchIfEmpty(Mono.defer(() -> rescheduleEvent(response)))
                .then();
    }
    private F24Metadata updateStatusToSaved(F24Metadata f24Metadata) {
        f24Metadata.setStatus(F24MetadataStatus.SAVED);
        return f24Metadata;
    }

    private Mono<F24Metadata> handleValidationEvent(F24Metadata f24Metadata) {
        if(f24Metadata.getHaveToSendValidationEvent()) {
            internalMetadataEventMomProducer.push(InternalMetadataEventBuilder.buildValidateMetadataEvent(f24Metadata.getSetId(), f24Metadata.getCxId()));
        }
        return Mono.just(f24Metadata);
    }

    private Mono<? extends F24Metadata> rescheduleEvent(FileDownloadResponse response) {
        log.warn("The fileKey {} not found on Metadata record, trying to reschedule", response.getKey());
        //TODO
        return Mono.empty();
    }

    private Mono<Void> handleF24FileKey(FileDownloadResponse response) {
        //TODO
        return Mono.empty();
    }
}
