package it.pagopa.pn.f24.middleware.msclient.safestorage;

import static org.junit.jupiter.api.Assertions.assertNull;
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

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

class PnSafeStorageClientImplTest {

    /**
     * Method under test: {@link PnSafeStorageClientImpl#createFile(FileCreationWithContentRequest, String)}
     */
    @Test
    void testCreateFile() throws WebClientResponseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        FileUploadApi fileUploadApi = mock(FileUploadApi.class);
        when(fileUploadApi.createFile(any(), any(), any(), any()))
                .thenReturn((Mono<FileCreationResponse>) mock(Mono.class));
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), mock(RestTemplate.class));
        pnSafeStorageClientImpl.createFile(new FileCreationWithContentRequest(), "Sha256");
        verify(fileUploadApi).createFile(any(), any(), any(), any());
    }


    /**
     * Method under test: {@link PnSafeStorageClientImpl#getFile(String, boolean)}
     */
    @Test
    void testGetFile2() throws WebClientResponseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        FileDownloadApi fileDownloadApi = mock(FileDownloadApi.class);
        when(fileDownloadApi.getFile(any(), any(), any())).thenReturn(null);
        FileUploadApi fileUploadApi = new FileUploadApi();
        assertNull(
                (new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi, new F24Config(), mock(RestTemplate.class)))
                        .getFile("File Key", true));
        verify(fileDownloadApi).getFile(any(), any(), any());
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), mock(RestTemplate.class));
        FileCreationWithContentRequest fileCreationRequest = new FileCreationWithContentRequest();
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationRequest, new FileCreationResponse(), "Sha256"));
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent5() throws UnsupportedEncodingException, RestClientException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(), any(), any(), (Class<Object>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), restTemplate);
        FileCreationWithContentRequest fileCreationWithContentRequest = mock(FileCreationWithContentRequest.class);
        when(fileCreationWithContentRequest.getContent()).thenReturn("AAAAAAAA".getBytes("UTF-8"));
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
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent6() throws UnsupportedEncodingException, RestClientException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(), any(), any(), (Class<Object>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), restTemplate);
        FileCreationWithContentRequest fileCreationWithContentRequest = mock(FileCreationWithContentRequest.class);
        when(fileCreationWithContentRequest.getContent()).thenReturn("AAAAAAAA".getBytes("UTF-8"));
        when(fileCreationWithContentRequest.getContentType()).thenReturn("text/plain");
        FileCreationResponse fileCreationResponse = mock(FileCreationResponse.class);
        when(fileCreationResponse.getUploadMethod()).thenThrow(new PnInternalException("An error occurred"));
        when(fileCreationResponse.getKey()).thenReturn("Key");
        when(fileCreationResponse.getSecret()).thenReturn("Secret");
        when(fileCreationResponse.getUploadUrl()).thenReturn("https://example.org/example");
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationWithContentRequest, fileCreationResponse, "Sha256"));
        verify(fileCreationWithContentRequest).getContent();
        verify(fileCreationWithContentRequest).getContentType();
        verify(fileCreationResponse).getUploadMethod();
        verify(fileCreationResponse).getKey();
        verify(fileCreationResponse).getSecret();
        verify(fileCreationResponse).getUploadUrl();
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent7() throws UnsupportedEncodingException, RestClientException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) mock(ResponseEntity.class);
        when(responseEntity.getStatusCodeValue()).thenThrow(new PnInternalException("An error occurred"));
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(), any(), any(), (Class<Object>) any()))
                .thenReturn(responseEntity);
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), restTemplate);
        FileCreationWithContentRequest fileCreationWithContentRequest = mock(FileCreationWithContentRequest.class);
        when(fileCreationWithContentRequest.getContent()).thenReturn("AAAAAAAA".getBytes("UTF-8"));
        when(fileCreationWithContentRequest.getContentType()).thenReturn("text/plain");
        FileCreationResponse fileCreationResponse = mock(FileCreationResponse.class);
        when(fileCreationResponse.getUploadMethod()).thenReturn(FileCreationResponse.UploadMethodEnum.PUT);
        when(fileCreationResponse.getKey()).thenReturn("Key");
        when(fileCreationResponse.getSecret()).thenReturn("Secret");
        when(fileCreationResponse.getUploadUrl()).thenReturn("https://example.org/example");
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationWithContentRequest, fileCreationResponse, "Sha256"));
        verify(restTemplate).exchange(any(), any(), any(), (Class<Object>) any());
        verify(responseEntity).getStatusCodeValue();
        verify(fileCreationWithContentRequest).getContent();
        verify(fileCreationWithContentRequest).getContentType();
        verify(fileCreationResponse).getUploadMethod();
        verify(fileCreationResponse).getKey();
        verify(fileCreationResponse).getSecret();
        verify(fileCreationResponse).getUploadUrl();
    }

    /**
     * Method under test: {@link PnSafeStorageClientImpl#uploadContent(FileCreationWithContentRequest, FileCreationResponse, String)}
     */
    @Test
    void testUploadContent8() throws UnsupportedEncodingException, RestClientException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        new PnInternalException("An error occurred");
        ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) mock(ResponseEntity.class);
        when(responseEntity.getStatusCodeValue()).thenReturn(42);
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(), any(), any(), (Class<Object>) any()))
                .thenReturn(responseEntity);
        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi,
                new F24Config(), restTemplate);
        FileCreationWithContentRequest fileCreationWithContentRequest = mock(FileCreationWithContentRequest.class);
        when(fileCreationWithContentRequest.getContent()).thenReturn("AAAAAAAA".getBytes("UTF-8"));
        when(fileCreationWithContentRequest.getContentType()).thenReturn("text/plain");
        FileCreationResponse fileCreationResponse = mock(FileCreationResponse.class);
        when(fileCreationResponse.getUploadMethod()).thenReturn(FileCreationResponse.UploadMethodEnum.POST);
        when(fileCreationResponse.getKey()).thenReturn("Key");
        when(fileCreationResponse.getSecret()).thenReturn("Secret");
        when(fileCreationResponse.getUploadUrl()).thenReturn("https://example.org/example");
        assertThrows(PnInternalException.class,
                () -> pnSafeStorageClientImpl.uploadContent(fileCreationWithContentRequest, fileCreationResponse, "Sha256"));
        verify(restTemplate).exchange(any(), any(), any(), (Class<Object>) any());
        verify(responseEntity).getStatusCodeValue();
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
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

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
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       it.pagopa.pn.f24.config.F24Config
        //       it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl
        //   See https://diff.blue/R027 to resolve this issue.

        FileUploadApi fileUploadApi = new FileUploadApi();
        FileDownloadApi fileDownloadApi = new FileDownloadApi();
        assertThrows(PnInternalException.class,
                () -> (new PnSafeStorageClientImpl(fileUploadApi, fileDownloadApi, new F24Config(), mock(RestTemplate.class)))
                        .downloadPieceOfContent("foo", 1L));
    }
}

