
package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.business.F24ResponseConverter;
import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.InternalMetadataEventBuilder;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.F24Service;
import it.pagopa.pn.f24.service.F24Utils;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Sha256Handler;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.*;

import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;
import static it.pagopa.pn.f24.util.Utility.objectToJsonString;

@Service
@Slf4j
public class F24ServiceImpl implements F24Service {
    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";
    private static final String DEFAULT_SEPARATOR = "#";

    private static final String CONTENT_TYPE = "application/pdf";
    private static final String COST = "NO_FEE";
    private static final long SECONDS_TO_DELAY = 4;
    private static final int MAX_PATH_TOKENS_DIMENSION = 4;

    private final F24Generator f24Generator;
    //todo service
    private final PnSafeStorageClientImpl pnSafeStorageClient;
    private final EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer;
    private final MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;
    private final F24MetadataSetDao f24MetadataSetDao;

    private final F24FileDao f24FileDao;


    public F24ServiceImpl(F24Generator f24Generator, PnSafeStorageClientImpl pnSafeStorageClient, EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer, MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer, F24MetadataSetDao f24MetadataSetDao) {
        this.f24Generator = f24Generator;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.eventBridgeProducer = eventBridgeProducer;
        this.validateMetadataSetEventProducer = validateMetadataSetEventProducer;
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.f24FileDao = f24FileRepository;
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
                    String pathTokensKey = String.join(".", saveF24Item.getPathTokens());
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
        return Mono.fromRunnable(() -> eventBridgeProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(cxId, setId, f24MetadataSet.getValidationResult())))
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
        String pathTokensInString = String.join(DEFAULT_SEPARATOR, pathTokens);

        return f24FileDao.getItemByPathTokens(setId, xPagopaF24CxId, pathTokens, String.valueOf(cost)).flatMap(f ->
                safeStorageService.getFile(f.getFileKey(), false).map(FileDownloadResponseInt::getDownload).map(fileDownloadResponseInt -> {
                    log.info("pdf found for setId: {} pathTokens: {}", setId, pathTokensInString);
                    return F24ResponseConverter.fileDownloadInfoToF24Response(fileDownloadResponseInt);
                })).switchIfEmpty(Mono.defer(() -> generateFromMetadata(setId, xPagopaF24CxId, pathTokensInString, cost)));
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
                        .flatMap(f24File -> Mono.delay(Duration.ofSeconds(10))
                                .flatMap(unused -> safeStorageService.getFile(f24File.getFileKey(), false))
                                //     .map(FileDownloadResponse::getDownload)
                                .map(fileDownloadInfo -> {
                                    log.info("pdf generated for setId: {} pathTokens: {}", setId, pathTokensInString);
                                    return F24ResponseConverter.fileDownloadInfoToF24Response(fileDownloadInfo.getDownload());
                                }).publishOn(Schedulers.boundedElastic()));

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

        String finalCost = cost == null ? COST : cost.toString();

        return safeStorageService.createAndUploadContent(fileCreationWithContentRequest).flatMap(fileCreationResponse ->

                Mono.just(F24File.builder()
                        .pk(f24Metadataset.getPk())
                        .created(String.valueOf(Instant.now()))
                        .sk(finalCost + "#" + pathTokensInString)
                        .fileKey(fileCreationResponse.getKey())
                        .status(F24FileStatus.GENERATED).requestId(UUID.randomUUID() + "_SYNC").build()));
    }
}