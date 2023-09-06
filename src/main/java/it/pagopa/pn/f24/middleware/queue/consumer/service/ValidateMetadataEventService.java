package it.pagopa.pn.f24.middleware.queue.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.f24.business.MetadataValidator;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.MetadataValidationEventProducer;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.service.SafeStorageService;
import lombok.CustomLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CustomLog
public class ValidateMetadataEventService {
    private final F24MetadataSetDao f24MetadataSetDao;
    private final SafeStorageService safeStorageService;
    private final F24Config f24Config;

    private final MetadataValidationEventProducer metadataValidationEventProducer;

    public static final String SAVED = "SAVED";

    public static final Long DOWNLOAD_WHOLE_FILE = -1L;

    public ValidateMetadataEventService(F24MetadataSetDao f24MetadataSetDao, SafeStorageService safeStorageService, F24Config f24Config, MetadataValidationEventProducer metadataValidationEventProducer) {
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.safeStorageService = safeStorageService;
        this.f24Config = f24Config;
        this.metadataValidationEventProducer = metadataValidationEventProducer;
    }

    public Mono<Void> handleSaveMetadata(InternalMetadataEvent.Payload payload) {
        String setId = payload.getSetId();
        String cxId = payload.getCxId();
        log.info("handle save metadata with setId {} and cxId {}", setId, cxId);
        final String processName = "SAVE METADATA HANDLER";
        log.logStartingProcess(processName);

        return f24MetadataSetDao.getItem(setId, cxId)
                .flatMap(f24Metadata -> {
                    if(f24Metadata == null) {
                        return handleMetadataNotFound(payload);
                    }
                    return handleMetadataFound(f24Metadata, payload);
                })
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()))
                .then();
    }

    private Mono<Void> handleMetadataFound(F24MetadataSet f24MetadataSet, InternalMetadataEvent.Payload payload) {
        if(f24MetadataSet.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
            log.warn("Metadata with setId {} and cxId {} already saved and validated", payload.getSetId(), payload.getCxId());
            return Mono.error(new RuntimeException("Metadata already saved"));
        }

        return downloadMetadataSetFromSafeStorage(f24MetadataSet.getFileKeys())
            .map(this::validateMetadataList)
            /*
                .flatMap(f24MetadataValidationIssues -> {
                return this.f24MetadataSetDao.getItem(f24MetadataSet.getPk())
                        .flatMap(refreshedF24MetadataSet -> {
                            if(refreshedF24MetadataSet.getHaveToSendValidationEvent()) {
                                //Invio evento su event bridge di avvenuta validazione
                                //Aggiorna entità settando stato VALIDATION_ENDED ed eventuali validationStatus
                                return updateMetadataRecord(refreshedF24MetadataSet);
                            } else {
                                //Aggiorna entità settando stato VALIDATION_ENDED ed eventuali validationStatus
                                refreshedF24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
                                return updateMetadataRecord(refreshedF24MetadataSet);
                            }
                        })
            })
             */
            .then();
    }

    private Mono<List<MetadataToValidate>> downloadMetadataSetFromSafeStorage(Map<String, F24MetadataRef> fileKeys) {
        return Flux.fromStream(fileKeys.keySet().stream())
                .flatMap(pathTokensString -> {
                    F24MetadataRef f24MetadataRef = fileKeys.get(pathTokensString);
                    String fileKey = fileKeys.get(pathTokensString).getFileKey();
                    return downloadFileFromSafeStorage(fileKey)
                            .map(this::convertBytesToMetadataObject)
                            .doOnError(throwable -> log.warn("Couldn't parse all metadata in set", throwable))
                            .map(f24Metadata -> MetadataToValidate.builder()
                                    .metadata(f24Metadata)
                                    .ref(f24MetadataRef)
                                    .pathTokensKey(pathTokensString)
                                    .build()
                            );
                })
                .collectList();
        /*
        return Flux.fromStream(fileKeys.keySet().stream())
                .flatMap(pathTokensString -> {
                    String fileKey = fileKeys.get(pathTokensString).getFileKey();
                    return downloadFileFromSafeStorage(fileKey);
                })
                .map(this::convertBytesToMetadataObject)
                .doOnError(throwable -> log.warn("Couldn't parse all metadata in set", throwable))
                .map(f24Metadata -> MetadataToValidate.builder().metadata(f24Metadata).ref().build())
                .collectList();

         */
    }

    private Mono<byte[]> downloadFileFromSafeStorage(String fileKey) {
        return safeStorageService.getFile(fileKey, false)
                .flatMap(fileDownloadResponseInt -> safeStorageService.downloadPieceOfContent(fileKey, fileDownloadResponseInt.getDownload().getUrl(), DOWNLOAD_WHOLE_FILE))
                .doOnError(throwable -> log.warn("Error downloading file with fileKey {}", fileKey, throwable));
    }

    private F24Metadata convertBytesToMetadataObject(byte[] metadataFileInBytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(metadataFileInBytes, F24Metadata.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Metadata", e);
        }
    }

    private List<F24MetadataValidationIssue> validateMetadataList(List<MetadataToValidate> metadataToValidateList) {
        List<F24MetadataValidationIssue> validationIssues = new ArrayList<>();

        metadataToValidateList.forEach(
                metadataToValidate -> {
                    MetadataValidator metadataValidator = new MetadataValidator(metadataToValidate);
                    validationIssues.addAll(metadataValidator.validateMetadata());
                }
        );

        return validationIssues;
    }

    private Mono<F24MetadataSet> updateMetadataRecord(F24MetadataSet f24MetadataSet) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        return f24MetadataSetDao.updateItem(f24MetadataSet);
    }

    private Mono<Void> handleMetadataNotFound(InternalMetadataEvent.Payload payload) {
        log.warn("Metadata with setId {} and cxId {} not found on dynamo", payload.getSetId(), payload.getCxId());
        return Mono.empty();
    }
}
