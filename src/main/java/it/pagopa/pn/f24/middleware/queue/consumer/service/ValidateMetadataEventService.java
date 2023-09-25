package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.business.MetadataValidator;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
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
    private final EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer;
    public ValidateMetadataEventService(F24MetadataSetDao f24MetadataSetDao, SafeStorageService safeStorageService, EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer) {
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.safeStorageService = safeStorageService;
        this.eventBridgeProducer = eventBridgeProducer;
    }

    public Mono<Void> handleMetadataValidation(ValidateMetadataSetEvent.Payload payload) {
        String setId = payload.getSetId();
        String cxId = payload.getCxId();
        log.info("handle metadata validation with setId {} and cxId {}", setId, cxId);
        final String processName = "VALIDATE METADATA HANDLER";
        log.logStartingProcess(processName);
        return getMetadataSet(setId, cxId)
                .flatMap(this::startMetadataValidation)
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()))
                .then();
    }

    private Mono<F24MetadataSet> getMetadataSet(String setId, String cxId) {
        return f24MetadataSetDao.getItem(setId, cxId)
                .switchIfEmpty(Mono.defer(
                        () -> {
                            log.warn("MetadataSet with setId {} and cxId {} not found on dynamo", setId, cxId);
                            return Mono.error(
                                    new PnNotFoundException(
                                            "MetadataSet not found",
                                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId, cxId),
                                            PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                                    )
                            );
                        }
                    )
                );
    }

    private Mono<Void> startMetadataValidation(F24MetadataSet f24MetadataSet) {
        if(f24MetadataSet.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
            log.debug("Metadata with setId {} and cxId {} already saved and validated", f24MetadataSet.getSetId(), f24MetadataSet.getCxId());
            return Mono.empty();
        }

        if(f24MetadataSet.getStatus().equals(F24MetadataStatus.PROCESSING)) {
            log.debug("Metadata with setId {} and cxId {} already processing", f24MetadataSet.getSetId(), f24MetadataSet.getCxId());
            return Mono.empty();
        }

        return takeChargeOfMetadataValidation(f24MetadataSet)
            .flatMap(updatedF24MetadataSet -> downloadMetadataSetFromSafeStorage(updatedF24MetadataSet.getFileKeys()))
            .map(this::validateMetadataList)
            .flatMap(f24MetadataValidationIssues -> endValidationProcess(f24MetadataValidationIssues, f24MetadataSet));
    }

    private Mono<F24MetadataSet> takeChargeOfMetadataValidation(F24MetadataSet f24MetadataSet) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setStatus(F24MetadataStatus.PROCESSING);

        return f24MetadataSetDao.setF24MetadataSetStatusProcessing(f24MetadataSet)
                .doOnError(throwable -> log.warn("Error setting MetadataSet status with pk {} in PROCESSING", f24MetadataSet.getPk()));
    }

    private Mono<List<MetadataToValidate>> downloadMetadataSetFromSafeStorage(Map<String, F24MetadataRef> fileKeys) {
        return Flux.fromStream(fileKeys.keySet().stream())
                .flatMap(pathTokensString -> {
                    F24MetadataRef f24MetadataRef = fileKeys.get(pathTokensString);
                    String fileKey = fileKeys.get(pathTokensString).getFileKey();
                    return downloadFileFromSafeStorage(fileKey)
                            .map(file -> MetadataToValidate.builder()
                                    .metadataFile(file)
                                    .ref(f24MetadataRef)
                                    .pathTokensKey(pathTokensString)
                                    .build()
                            );
                })
                .collectList();
    }

    private Mono<byte[]> downloadFileFromSafeStorage(String fileKey) {
        final int DOWNLOAD_WHOLE_FILE = -1;
        return safeStorageService.getFile(fileKey, false)
                .flatMap(fileDownloadResponseInt -> safeStorageService.downloadPieceOfContent(fileKey, fileDownloadResponseInt.getDownload().getUrl(), DOWNLOAD_WHOLE_FILE))
                .doOnError(throwable -> log.warn("Error downloading file with fileKey {}", fileKey, throwable));
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

    private Mono<Void> endValidationProcess(List<F24MetadataValidationIssue> f24MetadataValidationIssues, F24MetadataSet f24MetadataSet) {
        return this.f24MetadataSetDao.getItem(f24MetadataSet.getSetId(), f24MetadataSet.getCxId(), true)
                .flatMap(refreshedF24MetadataSet -> {
                    String cxId = f24MetadataSet.getCxId();
                    String setId = f24MetadataSet.getSetId();
                    if(refreshedF24MetadataSet.getHaveToSendValidationEvent()) {
                        log.debug("MetadataSet with setId {} and cxId {} has to send validation end event", setId, cxId);
                        return Mono.fromRunnable(() -> eventBridgeProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(cxId, setId, f24MetadataValidationIssues)))
                                .doOnError(throwable -> log.warn("Error sending validation end event", throwable))
                                .then(updateMetadataSetWithValidation(refreshedF24MetadataSet, f24MetadataValidationIssues, true));
                    } else {
                        log.debug("MetadataSet with setId {} and cxId {} hasn't to send validation end event", setId, cxId);
                        return updateMetadataSetWithValidation(refreshedF24MetadataSet, f24MetadataValidationIssues, false);
                    }
                })
                .then();
    }

    private Mono<F24MetadataSet> updateMetadataSetWithValidation(F24MetadataSet f24MetadataSet, List<F24MetadataValidationIssue> f24MetadataValidationIssues, boolean validationEventSent) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setValidationEventSent(validationEventSent);
        if(f24MetadataValidationIssues != null) {
            f24MetadataSet.setValidationResult(f24MetadataValidationIssues);
        }
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        return f24MetadataSetDao.updateItem(f24MetadataSet);
    }
}
