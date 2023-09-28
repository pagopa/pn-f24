package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.NoSuchElementException;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_CODE_F24_READ_FILE_ERROR;
import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_CODE_F24_UPLOADFILEERROR;


@Slf4j
@Service
public class SafeStorageServiceImpl implements SafeStorageService {
    private final PnSafeStorageClient safeStorageClient;

    public SafeStorageServiceImpl(PnSafeStorageClient safeStorageClient) {
        this.safeStorageClient = safeStorageClient;
    }

    @Override
    public Mono<FileDownloadResponseInt> getFile(String fileKey, Boolean metadataOnly) {
        MDC.put(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY, fileKey);

        return safeStorageClient.getFile(fileKey, metadataOnly)
                .doOnSuccess(fileDownloadResponse -> {
                    log.debug("Response getFile from SafeStorage: {}", fileDownloadResponse);
                    MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY);
                })
                .doOnError(err -> MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY))
                .map(this::getFileDownloadResponseInt);
    }

    private FileDownloadResponseInt getFileDownloadResponseInt(FileDownloadResponse fileDownloadResponse) {
        FileDownloadResponseInt.FileDownloadResponseIntBuilder responseIntBuilder = FileDownloadResponseInt.builder()
                .contentLength(fileDownloadResponse.getContentLength())
                .checksum(fileDownloadResponse.getChecksum())
                .contentType(fileDownloadResponse.getContentType())
                .key(fileDownloadResponse.getKey());

        if(fileDownloadResponse.getDownload() != null){
            responseIntBuilder.download(
                    FileDownloadInfoInt.builder()
                            .retryAfter(fileDownloadResponse.getDownload().getRetryAfter())
                            .url(fileDownloadResponse.getDownload().getUrl())
                            .build()
            );
        }

        return responseIntBuilder.build();
    }
    
    @Override
    public Mono<FileCreationResponseInt> createAndUploadContent(FileCreationWithContentRequest fileCreationRequest) {
        log.info("Start createAndUploadFile - documentType={} filesize={}", fileCreationRequest.getDocumentType(), fileCreationRequest.getContent().length);

        String sha256 = Sha256Handler.computeSha256(fileCreationRequest.getContent());

        return safeStorageClient.createFile(fileCreationRequest, sha256)
                .onErrorResume( Exception.class, exception ->{
                    log.error("Cannot create file ", exception);
                    return Mono.error(new PnInternalException("Cannot create file", ERROR_CODE_F24_UPLOADFILEERROR, exception));
                })
                .flatMap(fileCreationResponse -> 
                    Mono.fromRunnable(() -> safeStorageClient.uploadContent(fileCreationRequest, fileCreationResponse, sha256))
                            .thenReturn(fileCreationResponse)
                            .map(fileCreationResponse2 ->{
                                FileCreationResponseInt fileCreationResponseInt = FileCreationResponseInt.builder()
                                        .key(fileCreationResponse2.getKey())
                                        .build();

                                log.info("createAndUploadContent file uploaded successfully key={} sha256={}", fileCreationResponseInt.getKey(), sha256);

                                return fileCreationResponseInt;
                            })
                );
    }

    @Override
    public Mono<byte[]> downloadPieceOfContent(String fileKey, String url, long maxSize) {
        MDC.put(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY, fileKey);
        log.debug("Start call downloadPieceOfContent - fileKey={} url={} maxSize={}", fileKey, url, maxSize);

        return Mono.fromSupplier(() ->  safeStorageClient.downloadPieceOfContent(url, maxSize))
                .doOnSuccess( res -> {
                    MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY);
                    log.debug("downloadPieceOfContent file ok key={} url={} read size={}", fileKey, url, res==null?null:res.length);
                })
                .onErrorResume( err ->{
                    log.error("Cannot download content ", err);
                    MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY);
                    return Mono.error(new PnInternalException("Cannot update metadata", ERROR_CODE_F24_READ_FILE_ERROR, err));
                });
    }

    /**
     * Polling per il download di un file da SafeStorage:
     * se il tempo di timeout viene consumato senza restituire una risposta positiva, viene lanciato un NoSuchElementException
     * @param pollingTimeoutSec secondi di timeout del polling obbligatori (!= null)
     * @param pollingIntervalSec secondi di intervallo tra un polling e l'altro obbligatori (!= null)
     */
    public Mono<FileDownloadResponseInt> getFilePollingSafeStorage(String fileKey, Boolean metadataOnly, Integer pollingTimeoutSec, Integer pollingIntervalSec) {

        return Flux.interval(Duration.ofSeconds(pollingIntervalSec))
                .flatMap(i -> getFile(fileKey, false)
                        .onErrorResume(WebClientException.class, e -> Mono.empty()))
                .doOnNext(response -> {
                    if (response.getDownload() == null)
                        log.info("response has download null");
                    if (response.getDownload() != null)
                        log.info("response has download not null");
                })
                .doOnError(e -> log.warn("error polling safeStorage: " + e))
                .takeUntil(response -> response.getDownload() != null)
                .take(Duration.ofSeconds(pollingTimeoutSec))
                .last()
                .publishOn(Schedulers.boundedElastic());

    }
}
