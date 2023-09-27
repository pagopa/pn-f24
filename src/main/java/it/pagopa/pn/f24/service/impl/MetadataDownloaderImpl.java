package it.pagopa.pn.f24.service.impl;


import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MetadataDownloaderImpl implements MetadataDownloader {
    private final SafeStorageService safeStorageService;
    private final JsonService jsonService;
    public static final Long DOWNLOAD_WHOLE_FILE = -1L;

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
                .flatMap(fileDownloadResponseInt -> safeStorageService.downloadPieceOfContent(fileKey, fileDownloadResponseInt.getDownload().getUrl(), DOWNLOAD_WHOLE_FILE))
                .doOnError(throwable -> log.warn("Error downloading file with fileKey {}", fileKey, throwable));
    }
}