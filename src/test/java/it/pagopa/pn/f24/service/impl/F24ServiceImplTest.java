

package it.pagopa.pn.f24.service.impl;

import static org.mockito.Mockito.*;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnF24RuntimeException;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@ContextConfiguration(classes = {F24ServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24ServiceImplTest {
    @Autowired
    private F24ServiceImpl f24ServiceImpl;
    @MockBean
    private F24Generator f24Generator;
    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;
    @MockBean
    private PnSafeStorageClientImpl pnSafeStorageClientImpl;
    @MockBean
    private F24FileCacheDao f24FileCacheDao;
    @MockBean
    private SafeStorageService safeStorageService;
    @MockBean
    private EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer;
    @MockBean
    private MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;
    @MockBean
    private JsonService jsonService;
    @MockBean
    private MetadataDownloader metadataDownloader;

    @Test
    public void generatePDFFromCache() {

        //Mock for f24FileDao.getItem
        //todo check
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        // f24File.setSk("fileMetadata");
        f24File.setPk("fileMetadata");
        f24File.setCreated(Instant.now());
        // f24File.setRequestId("fileKey");
        f24File.setStatus(F24FileStatus.DONE);

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
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
    public void generatePdfFromCacheFailWithRuntimeException() {

        //Mock for f24FileDao.getItem
        //todo check
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        // f24File.setSk("fileMetadata");
        f24File.setPk("fileMetadata");
        f24File.setCreated(Instant.now());
        // f24File.setRequestId("fileKey");
        f24File.setStatus(F24FileStatus.PROCESSING);
        f24File.setUpdated(Instant.now().minus(Duration.ofMinutes(10)));

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.just(f24File));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        //assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", new ArrayList<>(), 100))
                .expectError(PnF24RuntimeException.class)
                .verify();
    }

    @Test
    public void generatePdfFromCacheWithRetryAfter() {

        //Mock for f24FileDao.getItem
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setPk("fileMetadata");
        f24File.setCreated(Instant.now());
        f24File.setStatus(F24FileStatus.PROCESSING);
        f24File.setUpdated(Instant.now());

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.just(f24File));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        //assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", new ArrayList<>(), 100))
                .expectNextMatches(f24Response -> f24Response.getRetryAfter().compareTo(BigDecimal.valueOf(0)) == 1)
                .expectComplete()
                .verify();
    }

    @Test
    public void generatePdfWhenFileIsNotInCache() throws JsonProcessingException {

        List<String> pathTokens = List.of("key");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("key");
        f24MetadataRef.setApplyCost(true);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));
        f24MetadataSet.setSetId("pk");

        //mock for SafeStorageService.createAndUploadContent
        FileCreationResponseInt fileCreationResponseInt = new FileCreationResponseInt();
        fileCreationResponseInt.setKey("key");

        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        //mock for F24Generator.generate
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());

        F24File f24File = new F24File();
        f24File.setFileKey("key");

        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(metadataDownloader.downloadMetadata(any()))
                .thenReturn(Mono.just(f24Metadata));
        when(f24Generator.generate(any(F24Metadata.class)))
                .thenReturn(new byte[0]);
        when(safeStorageService.createAndUploadContent(any()))
                .thenReturn(Mono.just(fileCreationResponseInt));
        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(f24FileCacheDao.putItemIfAbsent(any()))
                .thenReturn(Mono.just(f24File));
        when(f24FileCacheDao.updateItem(any()))
                .thenReturn(Mono.empty());

        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, 10))
                .expectNextMatches(f24Response -> Objects.equals(f24Response.getUrl(), "url"))
                .expectComplete()
                .verify();
    }

    @Test
    public void generatePdfErrorWhenGivenPathTokensDoesntExistInMetadataSet() {

        List<String> pathTokens = List.of("Notkey");

        //Mock for MetadataSetDao.getItem
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("key");
        f24MetadataRef.setApplyCost(true);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));
        f24MetadataSet.setSetId("pk");

        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        // Assert
        StepVerifier.create(f24ServiceImpl.generatePDF("xPagopaF24CxId", "setId", pathTokens, 100))
                .expectNextCount(0)
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    public void generatePdfFailOnMetadataWithoutApplyCostWhenRequestCostIsNotNull() throws JsonProcessingException {

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

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());

        when(f24FileCacheDao.getItem(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(metadataDownloader.downloadMetadata(any()))
                .thenReturn(Mono.just(f24Metadata));
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
    public void generatePdfFailOnMetadataWithApplyCostWhenRequestCostIsNull() {

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

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());

        when(f24FileCacheDao.getItem(anyString(), any(), anyString()))
                .thenReturn(Mono.empty());
        when(f24MetadataSetDao.getItem(anyString()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(metadataDownloader.downloadMetadata(any()))
                .thenReturn(Mono.just(f24Metadata));
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