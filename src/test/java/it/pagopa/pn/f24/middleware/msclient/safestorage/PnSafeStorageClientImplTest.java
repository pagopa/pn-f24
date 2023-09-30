package it.pagopa.pn.f24.middleware.msclient.safestorage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileDownloadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileUploadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;

import java.nio.charset.StandardCharsets;

import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PnSafeStorageClientImplTest {

    /**
     * Method under test: {@link PnSafeStorageClientImpl#createFile(FileCreationWithContentRequest, String)}
     */
    @Test
    void testCreateFile() throws WebClientResponseException {

        FileUploadApi fileUploadApi = mock(FileUploadApi.class);
        FileCreationResponse fileCreationResponse = new FileCreationResponse();
        fileCreationResponse.setKey("key");
        fileCreationResponse.setUploadUrl("http://upload-test.it");

        when(fileUploadApi.createFile(any(), any(), any(), any()))
                .thenReturn(Mono.just(fileCreationResponse));
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), mock(RestTemplate.class));

        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        StepVerifier.create(pnSafeStorageClientImpl.createFile(fileCreationWithContentRequest, "Sha256"))
                .expectNext(fileCreationResponse)
                .verifyComplete();
    }

    @Test
    void testGetFile() throws WebClientResponseException {
        FileUploadApi fileUploadApi = mock(FileUploadApi.class);
        FileDownloadApi fileDownloadApi = mock(FileDownloadApi.class);

        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        FileDownloadInfo fileDownloadInfo = new FileDownloadInfo();
        fileDownloadInfo.setUrl("http://download.test.it");
        fileDownloadResponse.setDownload(fileDownloadInfo);

        when(fileDownloadApi.getFile(any(), any(), any()))
                .thenReturn(Mono.just(fileDownloadResponse));
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), mock(RestTemplate.class));

        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        StepVerifier.create(pnSafeStorageClientImpl.getFile("fileKeyTest", true))
                .expectNext(fileDownloadResponse)
                .verifyComplete();
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent() {
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), mock(RestTemplate.class));
        FileCreationWithContentRequest fileCreationRequest = new FileCreationWithContentRequest();
        FileCreationResponse fileCreationResponse = new FileCreationResponse();
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationRequest, fileCreationResponse, "Sha256"));
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent5() throws RestClientException {


        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(), any(), any(), (Class<Object>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), restTemplate);
        FileCreationWithContentRequest fileCreationWithContentRequest = mock(FileCreationWithContentRequest.class);
        when(fileCreationWithContentRequest.getContent()).thenReturn("AAAAAAAA".getBytes(StandardCharsets.UTF_8));
        when(fileCreationWithContentRequest.getContentType()).thenReturn("text/plain");
        FileCreationResponse fileCreationResponse = mock(FileCreationResponse.class);
        when(fileCreationResponse.getUploadMethod()).thenReturn(FileCreationResponse.UploadMethodEnum.PUT);
        when(fileCreationResponse.getKey()).thenReturn("Key");
        when(fileCreationResponse.getSecret()).thenReturn("Secret");
        when(fileCreationResponse.getUploadUrl()).thenReturn("https://example.org/example");
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationWithContentRequest, fileCreationResponse, "Sha256"));
        verify(restTemplate).exchange(any(), any(), any(), (Class<Object>) any());
        verify(fileCreationWithContentRequest).getContent();
        verify(fileCreationWithContentRequest).getContentType();
        verify(fileCreationResponse).getUploadMethod();
        verify(fileCreationResponse).getKey();
        verify(fileCreationResponse).getSecret();
        verify(fileCreationResponse).getUploadUrl();
    }


    /**
     * Method under test: {@link PnSafeStorageClientImpl#downloadPieceOfContent(String, long)}
     */
    @Test
    void testDownloadPieceOfContent() {
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        assertThrows(PnInternalException.class,
                () -> (new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi, new F24Config(), mock(RestTemplate.class)))
                        .downloadPieceOfContent("https://example.org/example", 3L));
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#downloadPieceOfContent(String, long)}
     */
    @Test
    void testDownloadPieceOfContent2() {

        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        assertThrows(PnInternalException.class,
                () -> (new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi, new F24Config(), mock(RestTemplate.class)))
                        .downloadPieceOfContent("foo", 1L));
    }
}

