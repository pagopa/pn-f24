package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
@CustomLog
@AllArgsConstructor
public class SafeStorageEventService {

    private final F24FileCacheDao f24FileCacheDao;
    private final F24FileRequestDao f24RequestDao;
    private final F24Config f24Config;
    private final EventBridgeProducer<PnF24AsyncEvent> pdfSetReadyProducer;


    public Mono<Void> handleSafeStorageResponse(FileDownloadResponse response) {
        String fileKey = response.getKey();
        HandleEventUtils.addCorrelationIdToMdc(fileKey);
        log.info("Async response received from service {} for {} with correlationId={}",
                PnSafeStorageClient.CLIENT_NAME, PnSafeStorageClient.UPLOAD_FILE_CONTENT, fileKey);
        final String processName = PnSafeStorageClient.UPLOAD_FILE_CONTENT + " response handler";
        try {
            log.logStartingProcess(processName);

            if(response.getDocumentType().equalsIgnoreCase(f24Config.getSafeStorageF24DocType())) {
                return handleF24FileKey(response)
                        .doOnNext(unused -> log.logEndingProcess(processName))
                        .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()));
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

    private Mono<Void> handleF24FileKey(FileDownloadResponse response) {
        return f24FileCacheDao.getItemByFileKey(response.getKey())
                .doOnError(throwable -> log.warn("Error querying f24File table by fileKey", throwable))
                .flatMap(this::updateF24FileStatusInDone)
                .flatMap(this::updateStatusOfRelatedRequests);
    }

    private Mono<F24File> updateF24FileStatusInDone(F24File f24File) {
        log.debug("Updating F24File with pk: {} setting status DONE", f24File.getPk());
        f24File.setStatus(F24FileStatus.DONE);
        f24File.setUpdated(Instant.now());
        return f24FileCacheDao.setF24FileStatusDone(f24File)
                .doOnError(throwable -> log.warn("Error setting status DONE to F24File record with pk: {}", f24File.getPk()));
    }

    private Mono<Void> updateStatusOfRelatedRequests(F24File f24File) {
        log.debug("Updating F24Requests: {} associated with F24File with pk: {}", f24File.getRequestIds(), f24File.getPk());
        return Flux.fromIterable(f24File.getRequestIds())
                .flatMap(this::getF24Request)
                .flatMap(f24Request -> tryToUpdateF24RequestFile(f24Request, f24File))
                .flatMap(this::checkIfRequestIsCompleted)
                .then();
    }

    private Mono<F24Request> getF24Request(String pk) {
        return f24RequestDao.getItem(pk)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("F24Request with pk: {} not found", pk);
                    return Mono.empty();
                }));
    }

    private Mono<F24Request> tryToUpdateF24RequestFile(F24Request f24Request, F24File f24File) {
        f24Request.setFiles(setFileKeyInRequestFileMap(f24Request, f24File));
        return f24RequestDao.updateRequestFile(f24Request)
                .doOnError(t -> log.warn("Error updating Request file", t));
    }

    private Map<String, F24Request.FileRef> setFileKeyInRequestFileMap(F24Request f24Request, F24File f24File) {
        Map<String, F24Request.FileRef> mapFiles = f24Request.getFiles();

        F24Request.FileRef fileRefToUpdate = mapFiles.get(f24File.getPk());
        fileRefToUpdate.setFileKey(f24File.getFileKey());

        mapFiles.put(f24File.getPk(), fileRefToUpdate);

        return mapFiles;
    }

    private Mono<Void> checkIfRequestIsCompleted(F24Request f24Request) {
        return f24RequestDao.getItem(f24Request.getPk(), true)
            .flatMap(consistentF24Request -> {
                if(allFilesHaveFileKey(f24Request.getFiles())) {
                    log.debug("F24Request with pk: {} has all file with status DONE. Sending PdfSetReady event", f24Request.getPk());
                    return Mono.fromRunnable(() -> pdfSetReadyProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildPdfSetReadyEvent(consistentF24Request)))
                            .doOnError(throwable -> log.warn("Error sending PdfSetReady event"))
                            .then(updateF24RequestStatusInDone(f24Request))
                            .then();
                }

                log.debug("F24Request with pk: {} has other files to process.", f24Request.getPk());
                return Mono.empty();
            });
    }

    private boolean allFilesHaveFileKey(Map<String, F24Request.FileRef> files) {
        return files.values()
                .stream()
                .allMatch(fileRef -> fileRef.getFileKey() != null);
    }

    private Mono<F24Request> updateF24RequestStatusInDone(F24Request f24Request) {
        log.debug("setting f24Request with pk: {} status to DONE", f24Request.getPk());

        f24Request.setStatus(F24RequestStatus.DONE);
        f24Request.setUpdated(Instant.now());
        return f24RequestDao.updateItem(f24Request)
                .doOnError(throwable -> log.warn("Error updating f24Request status to DONE"));
    }
}
