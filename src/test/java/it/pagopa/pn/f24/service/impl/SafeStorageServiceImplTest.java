package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;

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

        Mockito.when(safeStorageClient.getFile(Mockito.anyString(), Mockito.anyBoolean()))
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
        Mockito.when(safeStorageClient.getFile(Mockito.anyString(), Mockito.anyBoolean()))
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

        Mockito.when(safeStorageClient.createFile(Mockito.any(FileCreationWithContentRequest.class), Mockito.anyString()))
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

        Mockito.when(safeStorageClient.createFile(Mockito.any(FileCreationWithContentRequest.class), Mockito.anyString()))
                .thenReturn(Mono.error(new PnInternalException("test", "test")));

        //WHEN
        Mono<FileCreationResponseInt> mono = safeStorageService.createAndUploadContent(fileCreationWithContentRequest);

        Assertions.assertThrows(PnInternalException.class, mono::block);
    }


    @Test
    @ExtendWith(SpringExtension.class)
    void downloadPieceOfContent() {
        //GIVEN


        Mockito.when(safeStorageClient.downloadPieceOfContent(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(new byte[0]);

        //WHEN
        Mono<byte[]> responseMono = safeStorageService.downloadPieceOfContent("test", "https://someurl", 128);

        //THEN
        Assertions.assertNotNull(responseMono);
        byte[] response = responseMono.block();
        Assertions.assertNotNull(response);

    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getFilePollingSafeStorage() {

        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        FileDownloadInfo fileDownloadInfo = new FileDownloadInfo();

        fileDownloadInfo.setUrl("url");
        fileDownloadResponse.setDownload(fileDownloadInfo);

        Mockito.when(safeStorageClient.getFile(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.just(fileDownloadResponse));

        StepVerifier.create(safeStorageService.getFilePolling("filekey", false, 3, 1))
                .expectNextMatches(f -> Objects.equals(f.getDownload().getUrl(), "url"))
                .expectComplete()
                .verify(Duration.ofSeconds(10 + 1));
    }
}