
package it.pagopa.pn.f24.service.impl;
import static org.mockito.Mockito.*;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.SafeStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.primitives.Bytes;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@ContextConfiguration(classes = {F24ServiceImpl.class})
@ExtendWith(SpringExtension.class)
class F24ServiceImplTest {
    @MockBean
    private EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer;
    @MockBean
    private F24Generator f24Generator;
    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;
    @Autowired
    private F24ServiceImpl f24ServiceImpl;
    @MockBean
    private MomProducer<InternalMetadataEvent> momProducer;
    @MockBean
    private PnSafeStorageClientImpl pnSafeStorageClientImpl;
    @MockBean
    private F24FileDao f24FileDao;
    @MockBean
    private SafeStorageService safeStorageService;

    /**
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     *//*

/*
    @Test
    void testValidate() {
        when(f24MetadataSetDao.getItem(any(), any())).thenThrow(new PnBadRequestException(
                "An error occurred", "The characteristics of someone or something", "An error occurred"));
        assertThrows(PnBadRequestException.class, () -> f24ServiceImpl.validate("42", "42"));
        verify(f24MetadataSetDao).getItem(any(), any());
    }


/**
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     *//*

    @Test
    void testValidate2() {
        when(f24MetadataSetDao.getItem(any(), any()))
                .thenReturn((Mono<F24MetadataSet>) mock(Mono.class));
        f24ServiceImpl.validate("42", "42");
        verify(f24MetadataSetDao).getItem(any(), any());
    }

*/
    @Test
    public void generatePDFFromCache() {

        //Mock for f24FileDao.getItem
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setSk("fileMetadata");
        f24File.setPk("fileMetadata");
        f24File.setCreated("fileKey");
        f24File.setRequestId("fileKey");
        f24File.setStatus(F24FileStatus.GENERATED);

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        when(f24FileDao.getItemByPathTokens(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(Mono.just(f24File));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        //assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", new ArrayList<>(), 100))
                .expectNextMatches(f24Response -> Objects.equals(f24Response.getUrl(), "url"))
                .expectComplete()
                .verify();
    }

    @Test
    public void generatePDFFromMetadata() {

        List<String> pathTokens = List.of("key");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("key");
        f24MetadataRef.setApplyCost(true);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));
        f24MetadataSet.setPk("pk");

        //mock for SafeStorageService.createAndUploadContent
        FileCreationResponseInt fileCreationResponseInt = new FileCreationResponseInt();
        fileCreationResponseInt.setKey("key");

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        //mock for F24Generator.generate
        F24Standard f24Standard = new F24Standard();
        ObjectMapper objectMapper = new ObjectMapper();
        //todo: ottenere un json realistico da usare come input
      //  String util = objectMapper.writeValueAsString(f24Standard);

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(f24Standard);

        when(f24FileDao.getItemByPathTokens(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString(), anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.downloadPieceOfContent(anyString(), anyString(), anyLong()))
                .thenReturn(Mono.just(any()));
        when(f24Generator.generate(any(F24Metadata.class)))
                .thenReturn(new byte[0]);
        when(safeStorageService.createAndUploadContent(any()))
                .thenReturn(Mono.just(fileCreationResponseInt));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, 100))
                .expectNextCount(0)
                .expectNextMatches(f24Response -> Objects.equals(f24Response.getUrl(), "url"))
                .expectComplete()
                .verify();
    }

    @Test
    public void generatePDFFromMetadataError() {

        List<String> pathTokens = List.of("key");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("Notakey");
        f24MetadataSet.setFileKeys(Map.of("Ehy", f24MetadataRef));

        when(f24FileDao.getItemByPathTokens(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString(), anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.downloadPieceOfContent(anyString(), anyString(), anyLong()))
                .thenReturn(Mono.just(Bytes.concat("test".getBytes())));
        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, 100))
                .expectNextCount(0)
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    public void ApplyCostFalseWithCost() {

        List<String> pathTokens = List.of("key");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("key");
        f24MetadataRef.setApplyCost(false);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));

        //mock for SafeStorageService.createAndUploadContent
        FileCreationResponseInt fileCreationResponseInt = new FileCreationResponseInt();
        fileCreationResponseInt.setKey("key");

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        when(f24FileDao.getItemByPathTokens(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString(), anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.downloadPieceOfContent(anyString(), anyString(), anyLong()))
                .thenReturn(Mono.just(Bytes.concat("test".getBytes())));
        when(f24Generator.generate(any(F24Metadata.class)))
                .thenReturn(new byte[0]);
        when(safeStorageService.createAndUploadContent(any()))
                .thenReturn(Mono.just(fileCreationResponseInt));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, 100))
                .expectNextCount(0)
                .expectError(PnBadRequestException.class)
                .verify();
    }

    @Test
    public void ApplyCostTrueWithCostNull() {

        List<String> pathTokens = List.of("key");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("key");
        f24MetadataRef.setApplyCost(true);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        when(f24FileDao.getItemByPathTokens(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString(), anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.downloadPieceOfContent(anyString(), anyString(), anyLong()))
                .thenReturn(Mono.just(Bytes.concat("test".getBytes())));
        when(f24Generator.generate(any(F24Metadata.class)))
                .thenReturn(new byte[0]);
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, null))
                .expectNextCount(0)
                .expectError(PnBadRequestException.class)
                .verify();
    }

}


