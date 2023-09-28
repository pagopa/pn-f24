package it.pagopa.pn.f24.service.impl;


import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_CODE_F24_READ_FILE_ERROR;

@Service
@Slf4j
public class MetadataDownloaderImpl implements MetadataDownloader {
    private final SafeStorageService safeStorageService;
    private final JsonService jsonService;
    public static final Long DOWNLOAD_WHOLE_FILE = -1L;
    public static final BigDecimal RETRY_AFTER_TIME_LIMIT = BigDecimal.valueOf(30000);
    public static final int TIME_LIMIT = 0;

    public MetadataDownloaderImpl(SafeStorageService safeStorageService, JsonService jsonService) {
        this.safeStorageService = safeStorageService;
        this.jsonService = jsonService;
    }

    @Override
    public Mono<F24Metadata> downloadMetadata(String fileKey) {
        log.debug("Starting download metadata f24 with fileKey {}", fileKey);
        return downloadFileFromSafeStorage(fileKey)
                .map(jsonService::parseMetadataFile);
    }

    private Mono<byte[]> downloadFileFromSafeStorage(String fileKey) {
        return safeStorageService.getFile(fileKey, false)
                .flatMap(fileDownloadResponseInt -> {

                    if (fileDownloadResponseInt.getDownload().getUrl() == null && fileDownloadResponseInt.getDownload().getRetryAfter() != null) {
                        log.info("Download of filekey {} not ready, Retry after {} ms", fileKey, fileDownloadResponseInt.getDownload().getRetryAfter());
                        if (fileDownloadResponseInt.getDownload().getRetryAfter().compareTo(RETRY_AFTER_TIME_LIMIT) < TIME_LIMIT) {
                            return Mono.delay(Duration.ofMillis(fileDownloadResponseInt.getDownload().getRetryAfter().longValue()))
                                    .flatMap(aLong -> retryDownload(fileKey));
                        } else {
                            log.info("Download requesting too much time for fileKey {}", fileKey);
                            return Mono.error(new PnInternalException(ERROR_CODE_F24_READ_FILE_ERROR, "Error downloading file with fileKey " + fileKey));
                        }
                    }
                    return safeStorageService.downloadPieceOfContent(fileKey, fileDownloadResponseInt.getDownload().getUrl(), DOWNLOAD_WHOLE_FILE);

                })
                .doOnError(throwable -> log.warn("Error downloading file with fileKey {}", fileKey, throwable));
    }

    private Mono<byte[]> retryDownload(String fileKey) {
        log.info("Retry download for fileKey {}", fileKey);
        return safeStorageService.getFile(fileKey, false).flatMap(fileDownloadResponseInt -> {
            if (fileDownloadResponseInt.getDownload().getUrl() == null && fileDownloadResponseInt.getDownload().getRetryAfter() != null) {
                log.info("Download of filekey {} still not available", fileKey);
                throw new PnInternalException(ERROR_CODE_F24_READ_FILE_ERROR, "Error downloading file with fileKey " + fileKey);
            }
            return safeStorageService.downloadPieceOfContent(fileKey, fileDownloadResponseInt.getDownload().getUrl(), DOWNLOAD_WHOLE_FILE);
        });
    }
}