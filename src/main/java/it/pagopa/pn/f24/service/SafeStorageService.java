package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import reactor.core.publisher.Mono;

public interface SafeStorageService {
    Mono<FileDownloadResponseInt> getFile(String fileKey, Boolean metadataOnly) ;
    Mono<FileCreationResponseInt> createAndUploadContent(FileCreationWithContentRequest fileCreationRequest);
    Mono<byte[]> downloadPieceOfContent(String fileKey, String url, long maxSize);
}
