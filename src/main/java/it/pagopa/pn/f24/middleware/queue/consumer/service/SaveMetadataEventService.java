package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Item;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.F24MetadataDao;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.CustomLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static it.pagopa.pn.f24.util.Utility.objectToJsonString;

@Service
@CustomLog
public class SaveMetadataEventService {
    private final F24MetadataDao f24MetadataDao;
    private final SafeStorageService safeStorageService;
    private final F24Config f24Config;

    public static final String SAVED = "SAVED";

    public SaveMetadataEventService(F24MetadataDao f24MetadataDao, SafeStorageService safeStorageService, F24Config f24Config) {
        this.f24MetadataDao = f24MetadataDao;
        this.safeStorageService = safeStorageService;
        this.f24Config = f24Config;
    }

    public Mono<Void> handleSaveMetadata(InternalMetadataEvent.Payload payload) {
        String setId = payload.getSetId();
        String cxId = payload.getCxId();
        log.info("handle save metadata with setId {} and cxId {}", setId, cxId);
        final String processName = "SAVE METADATA HANDLER";
        log.logStartingProcess(processName);

        return f24MetadataDao.getItem(setId, cxId)
                .flatMap(f24Metadata -> {
                    if(f24Metadata == null) {
                        return handleMetadataNotFound(payload);
                    }
                    return handleUploadMetadata(f24Metadata, payload);
                })
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()))
                .then();
    }

    private Mono<Void> handleUploadMetadata(F24Metadata f24Metadata, InternalMetadataEvent.Payload payload) {
        return validateMetadata(f24Metadata, payload)
                .doOnError(throwable -> log.warn("TODO Cancellare il Messaggio"))
                .then(uploadOnSafeStorage(payload))
                .flatMap(uploadResponse -> updateMetadataRecord(f24Metadata, uploadResponse.getKey()))
                .then();
    }

    private Mono<Void> validateMetadata(F24Metadata f24Metadata, InternalMetadataEvent.Payload payload) {
        if(f24Metadata.getStatus().equals(F24MetadataStatus.SAVED)) {
            log.warn("Metadata with setId {} and cxId {} already saved", payload.getSetId(), payload.getCxId());
            return Mono.error(new RuntimeException("Metadata already saved"));
        }

        return checkSha256(payload.getF24Item(), f24Metadata.getSha256());
    }

    private Mono<Void> checkSha256(List<F24Item> f24Items, String sha256) {
        String f24ItemsToJson = objectToJsonString(f24Items);
        String checksum = Sha256Handler.computeSha256(f24ItemsToJson);
        if(!sha256.equalsIgnoreCase(checksum)) {
            log.warn("Event payload has different sha from persisted Metadata record");
            return Mono.error(new RuntimeException("SaveMetadataException"));
        }

        return Mono.empty();
    }

    private Mono<FileCreationResponseInt> uploadOnSafeStorage(InternalMetadataEvent.Payload payload) {
        return safeStorageService.createAndUploadContent(buildFileCreationRequest(payload));
    }

    private FileCreationWithContentRequest buildFileCreationRequest(InternalMetadataEvent.Payload payload) {
        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        fileCreationWithContentRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
        fileCreationWithContentRequest.setDocumentType(f24Config.getSafeStorageMetadataDocType());
        fileCreationWithContentRequest.setStatus(SAVED);
        fileCreationWithContentRequest.setContent(objectToJsonString(payload.getF24Item()).getBytes(StandardCharsets.UTF_8));
        return fileCreationWithContentRequest;
    }

    private Mono<F24Metadata> updateMetadataRecord(F24Metadata f24Metadata, String fileKey) {
        f24Metadata.setUpdated(Instant.now());
        f24Metadata.setFileKey(fileKey);
        return f24MetadataDao.updateItem(f24Metadata);
    }

    private Mono<Void> handleMetadataNotFound(InternalMetadataEvent.Payload payload) {
        log.warn("Metadata with setId {} and cxId {} not found on dynamo", payload.getSetId(), payload.getCxId());
        return Mono.empty();
    }
}
