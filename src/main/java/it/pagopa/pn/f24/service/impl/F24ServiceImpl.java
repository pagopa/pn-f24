
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.business.F24ResponseConverter;
import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.*;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
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
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.F24Service;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.F24Utils;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Sha256Handler;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;

@Service
@Slf4j
public class F24ServiceImpl implements F24Service {
    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final int MAX_PATH_TOKENS_DIMENSION = 4;

    private final F24Generator f24Generator;
    private final EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer;
    private final SafeStorageService safeStorageService;
    private final MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;
    private final F24MetadataSetDao f24MetadataSetDao;
    private final JsonService jsonService;
    private final F24Config f24Config;
    private final F24FileCacheDao f24FileCacheDao;


    public F24ServiceImpl(F24Generator f24Generator, SafeStorageService safeStorageService, EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer, MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer, JsonService jsonService, F24MetadataSetDao f24MetadataSetDao, F24FileCacheDao f24FileCacheDao, F24Config f24Config) {
        this.f24Generator = f24Generator;
        this.safeStorageService = safeStorageService;
        this.metadataValidationEndedEventProducer = metadataValidationEndedEventProducer;
        this.validateMetadataSetEventProducer = validateMetadataSetEventProducer;
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.jsonService = jsonService;
        this.f24Config = f24Config;
        this.f24FileCacheDao = f24FileCacheDao;
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
        if (!pathSetId.equalsIgnoreCase(saveF24Request.getSetId())) {
            throw new PnBadRequestException(
                    "incongruent setId",
                    PnF24ExceptionCodes.ERROR_MESSAGE_F24_SET_ID_INCONGRUENT,
                    PnF24ExceptionCodes.ERROR_CODE_F24_SET_ID_INCONGRUENT
            );
        }

        saveF24Request.getF24Items().forEach(
                saveF24Item -> {
                    if (!checkPathTokensMaxDimension(saveF24Item.getPathTokens())) {
                        throw new PnBadRequestException(
                                "max dimension of pathTokens",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_PATH_TOKENS_DIMENSION_NOT_ALLOWED, MAX_PATH_TOKENS_DIMENSION),
                                PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED
                        );
                    }
                }
        );

        if (!checkPathTokensHaveSameDimension(saveF24Request.getF24Items())) {
            throw new PnBadRequestException("pathTokens with different lengths are not allowed", "pathTokens with different lengths are not allowed", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED);
        }

        if (!checkPathTokensUniqueness(saveF24Request.getF24Items())) {
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
        for (SaveF24Item f24Item : f24Items) {
            String pathTokensInString = Utility.convertPathTokensList(f24Item.getPathTokens());
            pathTokensSet.add(pathTokensInString);
        }

        return pathTokensSet.size() == f24Items.size();
    }


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
                    if (f24MetadataSetConsistent.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
                        return sendValidationEndedEventAndUpdateMetadataSet(f24MetadataSetConsistent);
                    }

                    return Mono.empty();
                });
    }

    private Mono<Void> sendValidationEndedEventAndUpdateMetadataSet(F24MetadataSet f24MetadataSet) {
        String setId = f24MetadataSet.getSetId();
        String cxId = f24MetadataSet.getCxId();
        log.debug("Sending validation ended event for metadata with setId : {} and cxId : {}", setId, cxId);

        PnF24MetadataValidationEndEvent event = PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(cxId, setId, f24MetadataSet.getValidationResult(), f24Config.getDeliveryPushCxId());
        return Mono.fromRunnable(() -> metadataValidationEndedEventProducer.sendEvent(event))
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
    public Mono<F24Response> generatePDF(String xPagopaF24CxId, String setId, List<String> pathTokens, Integer cost) {
        String pathTokensInString = Utility.convertPathTokensList(pathTokens);
        return f24FileCacheDao.getItem(xPagopaF24CxId, setId, cost, pathTokensInString).flatMap(f ->
        {
            if (f.getStatus().equals(F24FileStatus.DONE)) {
                return safeStorageService.getFile(f.getFileKey(), false).map(FileDownloadResponseInt::getDownload)
                        .map(fileDownloadResponseInt -> {
                            log.info("pdf found for setId: {} pathTokens: {}", setId, pathTokensInString);
                            return F24ResponseConverter.fileDownloadInfoToF24Response(fileDownloadResponseInt);
                        });
            }
            if (f.getUpdated().isBefore(Instant.now().minus(Duration.ofMinutes(f24Config.getSafeStorageExecutionLimitMin()))))
                throw new PnF24RuntimeException("File generation exceeded time expectation", "", PnF24ExceptionCodes.ERROR_CODE_F24_FILE_GENERATION_IN_PROGRESS);

            return Mono.just(F24ResponseConverter.fileDownloadInfoToF24Response(buildRetryAfterResponse().getDownload()));
        }).switchIfEmpty(Mono.defer(() -> generateFromMetadata(setId, xPagopaF24CxId, pathTokensInString, cost)));
    }

    private Mono<F24Response> generateFromMetadata(String setId, String xPagopaF24CxId, String pathTokensInString, Integer cost) {
        log.info("pdf not found, starting generation for: setId: {} pathTokens: {}", setId, pathTokensInString);
        return getMetadataSet(setId, xPagopaF24CxId).flatMap(f24MetadataSet -> {
            F24MetadataRef f24MetadataRef = f24MetadataSet.getFileKeys().get(pathTokensInString);
            if (f24MetadataRef == null) {
                throw new PnNotFoundException("Metadata not found", "", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND);
            }
            return downloadPOC(f24MetadataRef).flatMap(f24Metadata -> {
                if (f24MetadataRef.isApplyCost() && cost != null && cost != 0) {

                    MetadataInspector inspector = MetadataInspectorFactory.getInspector(getF24TypeFromMetadata(f24Metadata));
                    inspector.addCostToDebit(f24Metadata, cost);

                } else if (!f24MetadataRef.isApplyCost() && cost != null) {
                    return Mono.error(new PnBadRequestException("apply cost inconsistent", "applyCost is required when cost is populated", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
                } else if (f24MetadataRef.isApplyCost() && cost == null) {
                    return Mono.error(new PnBadRequestException("apply cost inconsistent", "cost is required when applyCost is true", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
                }
                FileCreationWithContentRequest fileCreationRequest = generaPdf(f24Metadata);
                return uploadF24FileAndPut(fileCreationRequest, cost, pathTokensInString, f24MetadataSet)
                        .flatMap(f24File -> pollingSafeStorage(f24File.getFileKey())
                                .flatMap(fileDownloadResponseInt -> {
                                    if (fileDownloadResponseInt.getDownload() != null && fileDownloadResponseInt.getDownload().getUrl() != null) {
                                        log.info("Received download url :{}for file with pk :{}, setting status DONE", fileDownloadResponseInt.getDownload().getUrl(), f24File.getPk());
                                        f24File.setStatus(F24FileStatus.DONE);
                                        f24File.setUpdated(Instant.now());
                                        return f24FileCacheDao.updateItem(f24File).thenReturn(F24ResponseConverter.fileDownloadInfoToF24Response(fileDownloadResponseInt.getDownload()));
                                    }
                                    log.info("Received retry after of :{}  for file with pk :{}", fileDownloadResponseInt.getDownload().getRetryAfter(), f24File.getPk());
                                    return Mono.just(F24ResponseConverter.fileDownloadInfoToF24Response(fileDownloadResponseInt.getDownload()));
                                }));
            });
        });
    }

    private Mono<F24Metadata> downloadPOC(F24MetadataRef f24MetadataRef) {
        return safeStorageService.getFile(f24MetadataRef.getFileKey(), false).flatMap(fileDownloadResponse -> {
            assert fileDownloadResponse.getDownload() != null;
            return safeStorageService.downloadPieceOfContent(f24MetadataRef.getFileKey(), fileDownloadResponse.getDownload().getUrl(), -1)
                    .map(bytes -> {
                        String fileContent = new String(bytes, StandardCharsets.UTF_8);
                        return F24Utils.validateF24Metadata(fileContent);
                    });
        }).doOnError(throwable -> log.error("error downloading poc for filekey: {}", f24MetadataRef.getFileKey(), throwable));
    }

    private FileCreationWithContentRequest generaPdf(F24Metadata f24Meta) {
        byte[] generatedPdf = f24Generator.generate(f24Meta);
        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        fileCreationWithContentRequest.setContent(generatedPdf);
        fileCreationWithContentRequest.setContentType(CONTENT_TYPE);
        log.info("generated pdf for f24 with metadata: {} ", f24Meta);
        return fileCreationWithContentRequest;
    }

    private Mono<F24File> uploadF24FileAndPut(FileCreationWithContentRequest fileCreationWithContentRequest, Integer cost, String pathTokensInString, F24MetadataSet f24Metadataset) {

        return safeStorageService.createAndUploadContent(fileCreationWithContentRequest).map(fileCreationResponse -> {
            F24File f24File = new F24File();
            f24File.setCxId(f24Metadataset.getCxId());
            f24File.setSetId(f24Metadataset.getSetId());
            f24File.setCost(cost);
            f24File.setPathTokens(pathTokensInString);
            f24File.setStatus(F24FileStatus.GENERATED);
            f24File.setCreated(Instant.now());
            if (f24Config.getRetentionForF24FilesInDays() != null && f24Config.getRetentionForF24FilesInDays() > 0) {
                f24File.setTtl(Instant.now().plus(Duration.ofDays(f24Config.getRetentionForF24FilesInDays())).getEpochSecond());
            }
            f24File.setFileKey(fileCreationResponse.getKey());
            return f24File;
        }).flatMap(f24FileCacheDao::putItemIfAbsent);
    }

    public Mono<FileDownloadResponseInt> pollingSafeStorage(String fileKey) {

        return Flux.interval(Duration.ofSeconds(f24Config.getSecIntervalForSafeStoragePolling()))
                .flatMap(i -> safeStorageService.getFile(fileKey, false)
                        .onErrorResume(WebClientException.class, e -> Mono.empty()))
                .doOnNext(response -> {
                    if (response.getDownload() == null)
                        log.info("response has download null");
                    if (response.getDownload() != null)
                        log.info("response has download not null");
                })
                .doOnError(e -> log.warn("error polling safeStorage: " + e))
                .takeUntil(response -> response.getDownload() != null)
                .take(Duration.ofSeconds(f24Config.getSecToPollingTimeout()))
                .last()
                .onErrorResume(NoSuchElementException.class, e -> Mono.just((buildRetryAfterResponse())))
                .publishOn(Schedulers.boundedElastic());

    }

    private FileDownloadResponseInt buildRetryAfterResponse() {
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setRetryAfter(BigDecimal.valueOf(f24Config.getRetryAfterWhenErrorSafeStorage()));
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);
        return fileDownloadResponseInt;
    }
}