package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import reactor.core.publisher.Mono;

public interface SafeStorageService {
    Mono<FileDownloadResponseInt> getFile(String fileKey, Boolean metadataOnly) ;
    Mono<FileCreationResponseInt> createAndUploadContent(FileCreationWithContentRequest fileCreationRequest);
    Mono<byte[]> downloadPieceOfContent(String fileKey, String url, long maxSize);

    /**
     * Polling per il download di un file da SafeStorage:
     * se il tempo di timeout viene consumato senza restituire una risposta positiva, viene lanciato un NoSuchElementException
     * @param pollingTimeoutSec secondi di timeout del polling obbligatori (!= null)
     * @param pollingIntervalSec secondi di intervallo tra un polling e l'altro obbligatori (!= null)
     */
    Mono<FileDownloadResponseInt> getFilePolling(String fileKey, Boolean metadataOnly, Integer pollingTimeoutSec, Integer pollingIntervalSec);
}
