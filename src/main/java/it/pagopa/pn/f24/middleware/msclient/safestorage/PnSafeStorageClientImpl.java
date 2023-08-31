package it.pagopa.pn.f24.middleware.msclient.safestorage;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileDownloadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileUploadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import lombok.CustomLog;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;

@Component
@CustomLog
public class PnSafeStorageClientImpl extends CommonBaseClient implements PnSafeStorageClient {

    private final FileUploadApi fileUploadApi;
    private final FileDownloadApi fileDownloadApi;
    private final F24Config f24Config;
    private final RestTemplate restTemplate;

    public PnSafeStorageClientImpl(FileUploadApi fileUploadApi, FileDownloadApi fileDownloadApi, F24Config f24Config, RestTemplate restTemplate) {
        this.fileUploadApi = fileUploadApi;
        this.fileDownloadApi = fileDownloadApi;
        this.f24Config = f24Config;
        this.restTemplate = restTemplate;
    }

    @Override
    public Mono<FileCreationResponse> createFile(FileCreationWithContentRequest fileCreationRequest, String sha256) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_SAFE_STORAGE, "createFile");

        return fileUploadApi.createFile( this.f24Config.getSafeStorageCxId(),"SHA-256", sha256,  fileCreationRequest )
                .doOnError( res -> log.error("File creation error - documentType={} filesize={} sha256={}", fileCreationRequest.getDocumentType(), fileCreationRequest.getContent().length, sha256));
    }

    @Override
    public Mono<FileDownloadResponse> getFile(String fileKey, boolean metadataOnly) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_SAFE_STORAGE, "getFile");
        return fileDownloadApi.getFile(fileKey, f24Config.getSafeStorageCxId(), metadataOnly);
    }

    @Override
    public void uploadContent(FileCreationWithContentRequest fileCreationRequest, FileCreationResponse fileCreationResponse, String sha256) {
        try {
            log.logInvokingAsyncExternalService(PnLogger.EXTERNAL_SERVICES.PN_SAFE_STORAGE, "uploadContent", fileCreationResponse.getKey());

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-type", fileCreationRequest.getContentType());
            headers.add("x-amz-checksum-sha256", sha256);
            headers.add("x-amz-meta-secret", fileCreationResponse.getSecret());

            HttpEntity<Resource> req = new HttpEntity<>(new ByteArrayResource(fileCreationRequest.getContent()), headers);

            URI url = URI.create(fileCreationResponse.getUploadUrl());
            HttpMethod method = fileCreationResponse.getUploadMethod() == FileCreationResponse.UploadMethodEnum.POST ? HttpMethod.POST : HttpMethod.PUT;

            ResponseEntity<String> res = restTemplate.exchange(url, method, req, String.class);

            if (res.getStatusCodeValue() != org.springframework.http.HttpStatus.OK.value())
            {
                throw new PnInternalException("File upload failed", PnF24ExceptionCodes.ERROR_CODE_F24_UPLOADFILEERROR);
            }

        } catch (PnInternalException ee)
        {
            log.error("uploadContent PnInternalException uploading file", ee);
            throw ee;
        }
        catch (Exception ee)
        {
            log.error("uploadContent Exception uploading file", ee);
            throw new PnInternalException("Exception uploading file", PnF24ExceptionCodes.ERROR_CODE_F24_UPLOADFILEERROR, ee);
        }
    }

    @Override
    public byte[] downloadPieceOfContent(String url, long maxSize) {
        long readSize = 0;
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                readSize += bytesRead;
                if (maxSize > 0 && readSize > maxSize)
                    break;
            }
            return fileOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Cannot read file content", e);
            throw new PnInternalException("cannot download content", PnF24ExceptionCodes.ERROR_CODE_F24_READ_FILE_ERROR, e);
        }
    }

}