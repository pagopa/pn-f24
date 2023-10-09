package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@CustomLog
@AllArgsConstructor
public class SafeStorageEventService {

    private final F24FileCacheDao f24FileCacheDao;
    private final F24FileRequestDao f24RequestDao;
    private final F24Config f24Config;
    private final EventBridgeProducer<PnF24PdfSetReadyEvent> pdfSetReadyProducer;


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
                .flatMap(this::updateFileStatusAndRelatedRequests);
    }

    private Mono<Void> updateFileStatusAndRelatedRequests(F24File f24File) {
        if(f24File.getRequestIds() == null || f24File.getRequestIds().isEmpty()) {
            log.debug("F24File with pk: {}, has not related requests", f24File.getPk());
            return updateF24FileStatusInDone(f24File);
        }

        log.debug("F24File with pk: {}, has related requests with id: {}", f24File.getPk(), f24File.getRequestIds());
        return updateFileAndRequests(f24File);
    }

    private Mono<Void> updateF24FileStatusInDone(F24File f24File) {
        log.debug("Updating F24File with pk: {} setting status DONE", f24File.getPk());
        return f24FileCacheDao.setStatusDone(f24File)
                .doOnError(throwable -> log.warn("Error setting status DONE to F24File record with pk: {}", f24File.getPk()))
                .then();
    }

    private Mono<Void> updateFileAndRequests(F24File f24FileToUpdate) {
        return this.buildF24RequestsToUpdate(f24FileToUpdate)
                .flatMap(f24RequestsToUpdate -> updateTransactionalFileAndRequests(f24RequestsToUpdate, f24FileToUpdate))
                .flatMap(this::checkIfRequestsAreCompleted);
    }

    private Mono<List<F24Request>> buildF24RequestsToUpdate(F24File f24File) {
        return Flux.fromIterable(f24File.getRequestIds())
                .flatMap(this::getF24Request)
                .map(f24Request -> setFileKeyInRequestFileMap(f24Request, f24File))
                .collectList();
    }


    private Mono<F24Request> getF24Request(String requestId) {
        return f24RequestDao.getItem(requestId)
                .switchIfEmpty(Mono.defer(
                        () -> {
                            log.warn("F24Request with requestId {} not found", requestId);
                            return Mono.error(
                                    new PnNotFoundException(
                                            "F24Request not found",
                                            String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_REQUEST_NOT_FOUND, requestId),
                                            PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                                    )
                            );
                        }
                ));
    }

    private F24Request setFileKeyInRequestFileMap(F24Request f24Request, F24File f24File) {
        f24Request.setFiles(updateFileMap(f24Request, f24File));
        return f24Request;
    }

    private Map<String, F24Request.FileRef> updateFileMap(F24Request f24Request, F24File f24File) {
        Map<String, F24Request.FileRef> requestMapFiles = f24Request.getFiles();

        F24Request.FileRef fileRefToUpdate = requestMapFiles.get(f24File.getPk());
        fileRefToUpdate.setFileKey(f24File.getFileKey());

        requestMapFiles.put(f24File.getPk(), fileRefToUpdate);

        return requestMapFiles;
    }

    private Mono<List<F24Request>> updateTransactionalFileAndRequests(List<F24Request> f24Requests, F24File f24File) {
        removeRequestIdsFromF24FileAndSetStatusDone(f24Requests, f24File);
        return f24RequestDao.updateTransactionalFileAndRequests(f24Requests, f24File)
                .doOnError(throwable -> log.warn("Error updating F24File and related F24Requests", throwable))
                .thenReturn(f24Requests);
    }

    private void removeRequestIdsFromF24FileAndSetStatusDone(List<F24Request> f24Requests, F24File f24File) {
        //Rimuovo dal file i requestId che sono associati.
        List<String> requestIdsToRemove = f24Requests.stream().map(F24Request::getRequestId).toList();
        f24File.getRequestIds().removeAll(requestIdsToRemove);

        f24File.setStatus(F24FileStatus.DONE);
    }

    private Mono<Void> checkIfRequestsAreCompleted(List<F24Request> f24Requests) {
        return Flux.fromIterable(f24Requests)
                .flatMap(f24Request -> f24RequestDao.getItem(f24Request.getRequestId(), true))
                .flatMap(consistentF24Request -> {
                    if(allRequestFilesHaveFileKey(consistentF24Request.getFiles())) {
                        return sendPdfSetReadyEvent(consistentF24Request);
                    }

                    log.debug("F24Request with pk: {} has other files to process.", consistentF24Request.getPk());
                    return Mono.empty();
                })
                .then();
    }

    private boolean allRequestFilesHaveFileKey(Map<String, F24Request.FileRef> files) {
        return files.values()
                .stream()
                .noneMatch(fileRef -> StringUtils.isEmpty(fileRef.getFileKey()));
    }

    private Mono<Void> sendPdfSetReadyEvent(F24Request consistentF24Request) {
        log.debug("F24Request with pk: {} has all file with status DONE. Sending PdfSetReady event", consistentF24Request.getPk());
        return pdfSetReadyProducer.sendEvent(PnF24AsyncEventBuilderHelper.buildPdfSetReadyEvent(consistentF24Request))
                .doOnError(throwable -> log.warn("Error sending PdfSetReady event"))
                .then(updateF24RequestStatusInDone(consistentF24Request))
                .then();
    }

    private Mono<F24Request> updateF24RequestStatusInDone(F24Request f24Request) {
        log.debug("setting f24Request with pk: {} status to DONE", f24Request.getPk());

        f24Request.setStatus(F24RequestStatus.DONE);
        f24Request.setRecordVersion(f24Request.getRecordVersion() + 1);
        return f24RequestDao.setRequestStatusDone(f24Request)
                .doOnError(throwable -> log.warn("Error updating f24Request status to DONE"))
                .onErrorResume(PnDbConflictException.class, e -> {
                    log.debug("F24 Request with requestId {} already in status DONE", f24Request.getRequestId());
                    return Mono.empty();
                });
    }
}
