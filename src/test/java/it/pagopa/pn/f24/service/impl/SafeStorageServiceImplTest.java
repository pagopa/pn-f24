package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnFileNotFoundException;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileDownloadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileUploadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SafeStorageServiceImplTest {
    @Mock
    private PnSafeStorageClient safeStorageClient;

    private SafeStorageServiceImpl safeStorageService;

    @BeforeEach
    public void init() {
        safeStorageService = new SafeStorageServiceImpl(safeStorageClient);
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getFile() {
        //GIVEN
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        fileDownloadResponse.setKey("key");
        fileDownloadResponse.setChecksum("checkSum");
        fileDownloadResponse.setContentType("content");
        fileDownloadResponse.setDocumentStatus("status");
        fileDownloadResponse.setDocumentType("type");
        fileDownloadResponse.setDownload(new FileDownloadInfo());

        when(safeStorageClient.getFile(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.just(fileDownloadResponse));

        //WHEN
        Mono<FileDownloadResponseInt> responseMono = safeStorageService.getFile("test", true);

        //THEN
        Assertions.assertNotNull(responseMono);
        FileDownloadResponseInt response = responseMono.block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(fileDownloadResponse.getKey(), response.getKey());
        Assertions.assertEquals(fileDownloadResponse.getChecksum(), response.getChecksum());
        Assertions.assertEquals(fileDownloadResponse.getContentType(), response.getContentType());
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getFileError() {
        //GIVEN
        when(safeStorageClient.getFile(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.error(new PnInternalException("test", "test")));

        Mono<FileDownloadResponseInt> mono = safeStorageService.getFile("test", true);

        //WHEN
        Assertions.assertThrows(PnInternalException.class, mono::block);
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void createAndUploadContent() {
        //GIVEN
        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        fileCreationWithContentRequest.setContent("content".getBytes());

        FileCreationResponse expectedResponse = new FileCreationResponse();
        expectedResponse.setKey("key");
        expectedResponse.setSecret("secret");

        when(safeStorageClient.createFile(any(FileCreationWithContentRequest.class), Mockito.anyString()))
                .thenReturn(Mono.just(expectedResponse));

        //WHEN
        Mono<FileCreationResponseInt> responseMono = safeStorageService.createAndUploadContent(fileCreationWithContentRequest);

        //THEN
        FileCreationResponseInt response = responseMono.block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getKey(), expectedResponse.getKey());
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void createAndUploadContentError() {
        //GIVEN
        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        fileCreationWithContentRequest.setContent("content".getBytes());

        when(safeStorageClient.createFile(any(FileCreationWithContentRequest.class), Mockito.anyString()))
                .thenReturn(Mono.error(new PnInternalException("test", "test")));

        //WHEN
        Mono<FileCreationResponseInt> mono = safeStorageService.createAndUploadContent(fileCreationWithContentRequest);

        Assertions.assertThrows(PnInternalException.class, mono::block);
    }


    @Test
    @ExtendWith(SpringExtension.class)
    void downloadPieceOfContent() {
        //GIVEN


        when(safeStorageClient.downloadPieceOfContent(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(new byte[0]);

        //WHEN
        Mono<byte[]> responseMono = safeStorageService.downloadPieceOfContent("test", "https://someurl", 128);

        //THEN
        Assertions.assertNotNull(responseMono);
        byte[] response = responseMono.block();
        Assertions.assertNotNull(response);

    }

    @Test
    void downloadPieceOfContentShouldGenerateErrorMono() {

        // Chiamata al metodo downloadPieceOfContent con parametri appropriati che genereranno un errore
        String fileKey = "yourFileKey";
        String url = "yourInvalidUrl";
        long maxSize = 1000L;

        Mono<byte[]> resultMono = safeStorageService.downloadPieceOfContent(fileKey, url, maxSize);

        // Verifica che il resultMono generi un'errore di tipo PnInternalException
        StepVerifier.create(resultMono)
                .expectError(PnInternalException.class)
                .verify();
    }

    @Test
    void testGetFileNotFound() throws WebClientResponseException {
        FileUploadApi fileUploadApi = mock(FileUploadApi.class);
        FileDownloadApi fileDownloadApi = mock(FileDownloadApi.class);

        // Set up mocks
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        FileDownloadInfo fileDownloadInfo = new FileDownloadInfo();
        fileDownloadInfo.setUrl("http://download.test.it");
        fileDownloadResponse.setDownload(fileDownloadInfo);

        when(fileDownloadApi.getFile(any(), any(), any()))
                .thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null)));

        // Create the instance of the class under test
        PnSafeStorageClientImpl pnSafeStorageClientImpl = new PnSafeStorageClientImpl(
                fileUploadApi, fileDownloadApi, new F24Config(), mock(RestTemplate.class));

        // Trigger the method and verify the error
        StepVerifier.create(pnSafeStorageClientImpl.getFile("fileKeyTest", true))
                .expectError(PnFileNotFoundException.class)
                .verify();
    }

}