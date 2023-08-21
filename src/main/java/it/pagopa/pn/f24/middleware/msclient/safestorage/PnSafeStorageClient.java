package it.pagopa.pn.f24.middleware.msclient.safestorage;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import reactor.core.publisher.Mono;

public interface PnSafeStorageClient {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_SAFE_STORAGE;
    String GET_FILE = "GET FILE";
    String CREATE_FILE = "FILE CREATION";
    String UPLOAD_FILE_CONTENT = "UPLOAD FILE CONTENT";
    Mono<FileDownloadResponse> getFile(String fileKey, boolean metadataOnly);

    Mono<FileCreationResponse> createFile(FileCreationWithContentRequest fileCreationRequest, String sha256);

    void uploadContent(FileCreationWithContentRequest fileCreationRequest, FileCreationResponse fileCreationResponse, String sha256);

    /**
     * Scarica una parte (o tutto) il contenuto di un file
     * E' obbligatorio passare la dimensione richiesta, per rendere evidente che il metodo può essere usato per scaricare solo una parte di file
     *
     * @param url da scaricare
     * @param maxSize dimensione massima richiesta, -1 per scaricare tutto il file
     * @return array di dati
     */
    byte[] downloadPieceOfContent(String url, long maxSize);
}
