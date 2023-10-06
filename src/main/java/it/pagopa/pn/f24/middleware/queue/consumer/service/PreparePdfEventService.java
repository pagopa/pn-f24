package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.util.GeneratePdfEventBuilder;
import lombok.CustomLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@CustomLog
public class PreparePdfEventService {

    private final F24FileCacheDao f24FileCacheDao;

    private final F24MetadataSetDao f24MetadataSetDao;

    private final F24FileRequestDao f24FileRequestDao;

    private final MomProducer<GeneratePdfEvent> generatePdfEventProducer;

    private final EventBridgeProducer<PnF24PdfSetReadyEvent> pdfSetReadyEventProducer;

    private final F24Config f24Config;

    public PreparePdfEventService(F24FileCacheDao f24FileCacheDao, F24MetadataSetDao f24MetadataSetDao, F24FileRequestDao f24FileRequestDao, MomProducer<GeneratePdfEvent> generatePdfEventProducer, EventBridgeProducer<PnF24PdfSetReadyEvent> pdfSetReadyEventProducer, F24Config f24Config) {
        this.f24FileCacheDao = f24FileCacheDao;
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.f24FileRequestDao = f24FileRequestDao;
        this.generatePdfEventProducer = generatePdfEventProducer;
        this.pdfSetReadyEventProducer = pdfSetReadyEventProducer;
        this.f24Config = f24Config;
    }

    public Mono<Void> preparePdf(PreparePdfEvent.Payload payload) {

        String requestId = payload.getRequestId();
        log.info("prepare pdf for request {}", requestId);
        final String processName = "PREPARE PDF HANDLER";
        log.logStartingProcess(processName);

        return getF24Request(requestId)
                .flatMap(this::handlePreparePdf)
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()));
    }

    private Mono<F24Request> getF24Request(String requestId) {
        return f24FileRequestDao.getItem(requestId)
                .switchIfEmpty(
                    Mono.defer(
                        () -> {
                            log.warn("F24Request with requestId {} not found on dynamo", requestId);
                            return Mono.error(
                                    new PnNotFoundException(
                                            "F24Request not found",
                                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_REQUEST_NOT_FOUND, requestId),
                                            PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                                    )
                            );
                        }
                    )
                );
    }

    private Mono<Void> handlePreparePdf(F24Request f24Request) {
        if(f24Request.getStatus() == F24RequestStatus.DONE) {
            log.debug("F24 Request with requestId {} already in status DONE", f24Request.getRequestId());
            return Mono.empty();
        }

        return searchMetadataToProcess(f24Request)
                .flatMap(fileKeys -> buildF24FilesFromFileKeys(fileKeys, f24Request))
                .flatMap(this::dispatchEventAndSaveFiles);
    }

    private Mono<Map<String, F24MetadataRef>> searchMetadataToProcess(F24Request f24Request) {
        return getMetadataSet(f24Request.getSetId())
                .map(f24MetadataSet -> {
                    Map<String, F24MetadataRef> fileKeys = filterFileKeysByPathTokens(f24MetadataSet.getFileKeys(), f24Request.getPathTokens());
                    if(fileKeys.isEmpty()) {
                        throw new PnNotFoundException(
                                "Metadata not found",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_NOT_FOUND, f24Request.getPathTokens(), f24MetadataSet.getSetId()),
                                PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                        );
                    }

                    return fileKeys;
                });

    }

    private Mono<F24MetadataSet> getMetadataSet(String setId) {
        return f24MetadataSetDao.getItem(setId)
                .doOnError(t -> log.info("Error",t))
                .switchIfEmpty(Mono.error(
                        new PnNotFoundException(
                                "MetadataSet not found",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId),
                                PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                        )
                ));
    }

    private Map<String, F24MetadataRef> filterFileKeysByPathTokens(Map<String, F24MetadataRef> fileKeys, String pathTokensInString) {
        return fileKeys.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(pathTokensInString))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Mono<PreparePdfLists> buildF24FilesFromFileKeys(Map<String, F24MetadataRef> fileKeys, F24Request f24Request) {
        PreparePdfLists preparePdfLists = new PreparePdfLists(f24Request);
        return Flux.fromIterable(fileKeys.entrySet())
                .flatMap(entry -> {
                    F24MetadataRef f24MetadataRef = entry.getValue();

                    Integer cost = f24MetadataRef.isApplyCost() ? f24Request.getCost() : null;
                    String pathTokensInString = entry.getKey();

                    return f24FileCacheDao.getItem(f24Request.getSetId(), cost, pathTokensInString)
                            .map(f24File -> handleFileAlreadyInCache(f24File, f24Request))
                            .switchIfEmpty(Mono.just(createNewF24FileForRequestId(f24Request, cost, pathTokensInString)))
                            .flatMap(f24File -> {
                                switch (f24File.getStatus()) {
                                    case DONE -> preparePdfLists.getFilesReady().add(f24File);
                                    case TO_PROCESS -> {
                                        PreparePdfLists.F24FileToCreate f24FileToCreate = new PreparePdfLists.F24FileToCreate(f24File, f24MetadataRef.getFileKey());
                                        preparePdfLists.getFilesToCreate().add(f24FileToCreate);
                                    }
                                    default -> preparePdfLists.getFilesNotReady().add(f24File);
                                }
                                return Mono.empty();
                            });
                })
                .then()
                .thenReturn(preparePdfLists);
    }

    private F24File handleFileAlreadyInCache(F24File f24File, F24Request f24Request) {
        if (f24File.getStatus() == F24FileStatus.DONE) {
            log.debug("File with pk: {} in status: {}", f24File.getPk(), f24File.getStatus());
            return f24File;
        }
        /*
        Se sono qui, ho trovato il file in uno dei seguenti status: TO_PROCESS / GENERATED
        Li considero tutti come file in elaborazione, quindi gli aggiungo la requestId.
        */
        log.debug("File with pk: {} in status : {}. Adding requestId {} to the file", f24File.getPk(), f24File.getStatus(), f24Request.getRequestId());
        addRequestIdToFile(f24File, f24Request.getRequestId());
        return f24File;
    }

    private void addRequestIdToFile(F24File f24File, String requestId) {
        List<String> actualRequestIds = f24File.getRequestIds();
        if(actualRequestIds == null) {
            f24File.setRequestIds(List.of(requestId));
        } else {
            f24File.getRequestIds().add(requestId);
        }
    }

    private F24File createNewF24FileForRequestId(F24Request f24Request, Integer cost, String pathTokensInString) {
        String cxId = f24Request.getCxId();
        String setId = f24Request.getSetId();
        log.debug("F24File with cxId: {}, setId: {}, cost: {}, pathTokens: {} doesn't exist, creating a new one", cxId, setId, cost, pathTokensInString);

        F24File f24File = new F24File();
        f24File.setPk(new F24FileCacheEntity(setId, cost, pathTokensInString).getPk());
        f24File.setSetId(setId);
        f24File.setCost(cost);
        f24File.setPathTokens(pathTokensInString);
        f24File.setRequestIds(List.of(f24Request.getRequestId()));
        if(f24Config.getRetentionForF24FilesInDays() != null && f24Config.getRetentionForF24FilesInDays() > 0) {
            f24File.setTtl(Instant.now().plus(Duration.ofDays(f24Config.getRetentionForF24FilesInDays())).getEpochSecond());
        }
        f24File.setStatus(F24FileStatus.TO_PROCESS);
        return f24File;
    }

    private Mono<Void> dispatchEventAndSaveFiles(PreparePdfLists preparePdfLists) {
        log.debug("Choosing event to dispatch");
        List<F24File> f24FilesProcessing = preparePdfLists.getFilesNotReady();
        List<PreparePdfLists.F24FileToCreate> f24FilesToCreate = preparePdfLists.getFilesToCreate();
        List<F24File> f24FilesReady = preparePdfLists.getFilesReady();
        F24Request f24Request = preparePdfLists.getF24Request();

        if(!f24FilesProcessing.isEmpty() || !f24FilesToCreate.isEmpty()) {
            log.debug("There are {} files to process and {} files to create, sending GeneratePdf event", f24FilesProcessing.size(), f24FilesToCreate.size());

            return Mono.fromRunnable(() -> sendGeneratePdfEvents(f24FilesToCreate))
                .doOnError(throwable -> log.warn("Error sending preparePdf", throwable))
                .then(updateF24RequestAndF24File(preparePdfLists));
        } else {
            log.debug("All files ({}) are already processed, sending PdfSetReady Event", f24FilesReady.size());

            f24Request.setFiles(buildF24RequestFiles(preparePdfLists));
            return pdfSetReadyEventProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildPdfSetReadyEvent(f24Request))
                    .doOnError(throwable -> log.warn("Error sending PdfSetReady event"))
                    .then(updateF24RequestStatus(preparePdfLists))
                    .then();
        }
    }

    private void sendGeneratePdfEvents(List<PreparePdfLists.F24FileToCreate> files) {
        if(files.isEmpty()) {
            log.debug("There aren't files to generate, won't send any generate pdf event");
        } else {
            List<GeneratePdfEvent> generatePdfEvents = files.stream()
                    .map(GeneratePdfEventBuilder::buildGeneratePdfEvent)
                    .toList();
            log.debug("Sending {} generate pdf events", generatePdfEvents.size());
            generatePdfEventProducer.push(generatePdfEvents);
        }
    }

    private Mono<Void> updateF24RequestAndF24File(PreparePdfLists preparePdfLists) {
        preparePdfLists.getF24Request().setFiles(buildF24RequestFiles(preparePdfLists));
        return f24FileRequestDao.updateRequestAndRelatedFiles(preparePdfLists)
                .doOnError(t -> log.warn("Error updating Request and Files", t));
    }

    private Map<String, F24Request.FileRef> buildF24RequestFiles(PreparePdfLists preparePdfLists) {
        Map<String, F24Request.FileRef> requestFiles = new HashMap<>();

        preparePdfLists.getFilesNotReady().forEach((F24File f24File) -> requestFiles.put(f24File.getPk(), new F24Request.FileRef("")));

        preparePdfLists.getFilesReady().forEach((F24File f24File) -> requestFiles.put(f24File.getPk(), new F24Request.FileRef(f24File.getFileKey())));

        preparePdfLists.getFilesToCreate().forEach((PreparePdfLists.F24FileToCreate f24FileToCreate) -> {
            F24File f24File = f24FileToCreate.getFile();
            requestFiles.put(f24File.getPk(), new F24Request.FileRef(""));
        });

        return requestFiles;
    }

    private Mono<F24Request> updateF24RequestStatus(PreparePdfLists preparePdfLists) {
        F24Request f24Request = preparePdfLists.getF24Request();

        log.debug("setting f24Request with pk: {} status to DONE", f24Request.getPk());
        f24Request.setStatus(F24RequestStatus.DONE);
        f24Request.setRecordVersion(f24Request.getRecordVersion() + 1);
        return f24FileRequestDao.setRequestStatusDone(f24Request)
                .doOnError(throwable -> log.warn("Error updating f24Request status to DONE"))
                .onErrorResume(PnDbConflictException.class, e -> {
                    log.debug("F24 Request with requestId {} already in status DONE", f24Request.getRequestId());
                    return Mono.empty();
                });
    }
}
