package it.pagopa.pn.f24.service.impl;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyBoolean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnF24RuntimeException;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Sha256Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.Duration;

@ContextConfiguration(classes = {F24ServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24ServiceImplTest {
    private static final String DEFAULT_SUCCESS_STATUS = "Success!";
    @MockBean
    private EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer;

    @MockBean
    private F24Generator f24Generator;
    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;

    @Autowired
    private F24ServiceImpl f24ServiceImpl;

    @MockBean
    private PnSafeStorageClientImpl pnSafeStorageClientImpl;
    @MockBean
    private F24FileCacheDao f24FileCacheDao;
    @MockBean
    private SafeStorageService safeStorageService;
    @MockBean
    private MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;
    @MockBean
    private MomProducer<PreparePdfEvent> preparePdfEventProducer;
    @MockBean
    private JsonService jsonService;
    @MockBean
    private MetadataDownloader metadataDownloader;
    @MockBean
    private F24FileRequestDao f24FileRequestDao;

    @Test
    public void generatePDFFromCache() {

        //Mock for f24FileDao.getItem
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
        f24File.setStatus(F24FileStatus.TO_PROCESS);
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

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataErrorValidationRequestDifferentsSetId() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setIdDifferent");

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataErrorValidationPathTokensNotAllowed() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0","0","0","0","1"));
        saveF24Request.setF24Items(List.of(saveF24Item));

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataErrorValidationPathTokensWithDifferentSize() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0", "0"));
        SaveF24Item saveF24Item2 = new SaveF24Item();
        saveF24Item2.setPathTokens(List.of("0", "0", "1"));
        saveF24Request.setF24Items(List.of(saveF24Item, saveF24Item2));

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataErrorValidationPathTokensNotUnique() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0", "0"));
        SaveF24Item saveF24Item2 = new SaveF24Item();
        saveF24Item2.setPathTokens(List.of("0", "0"));
        saveF24Request.setF24Items(List.of(saveF24Item, saveF24Item2));

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataErrorConflictWithExistingMetadataSet() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0", "0"));
        saveF24Item.setApplyCost(true);
        saveF24Item.setFileKey("metadataFileKey");
        saveF24Request.setF24Items(List.of(saveF24Item));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        f24MetadataSet.setSha256("differentSha");
        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.just(f24MetadataSet));
        when(jsonService.stringifyObject(any())).thenReturn("SaveF24Item as JSON string");

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectError(PnConflictException.class)
                .verify();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataOkWithExistingMetadataSet() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0", "0"));
        saveF24Item.setApplyCost(true);
        saveF24Item.setFileKey("metadataFileKey");
        saveF24Request.setF24Items(List.of(saveF24Item));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        String shaSource = "SaveF24Item as JSON string";
        f24MetadataSet.setSha256(Sha256Handler.computeSha256(shaSource));
        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.just(f24MetadataSet));
        when(jsonService.stringifyObject(any())).thenReturn("SaveF24Item as JSON string");

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectNextMatches(requestAccepted -> requestAccepted.getStatus().equalsIgnoreCase(DEFAULT_SUCCESS_STATUS))
                .verifyComplete();
    }

    /**
     * Method under test: {@link F24ServiceImpl#saveMetadata(String, String, Mono)}
     */
    @Test
    void testSaveMetadataOkWithNewMetadataSet() {
        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setId("setId");
        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setPathTokens(List.of("0", "0"));
        saveF24Item.setApplyCost(true);
        saveF24Item.setFileKey("metadataFileKey");
        saveF24Request.setF24Items(List.of(saveF24Item));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        String shaSource = "SaveF24Item as JSON string";
        f24MetadataSet.setSha256(Sha256Handler.computeSha256(shaSource));
        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.empty());
        doNothing().when(validateMetadataSetEventProducer).push((ValidateMetadataSetEvent) any());

        when(jsonService.stringifyObject(any())).thenReturn("String");
        when(f24MetadataSetDao.putItemIfAbsent(any())).thenReturn(Mono.empty());

        StepVerifier.create(f24ServiceImpl.saveMetadata("cxId", "setId", Mono.just(saveF24Request)))
                .expectNextMatches(requestAccepted -> requestAccepted.getStatus().equalsIgnoreCase(DEFAULT_SUCCESS_STATUS))
                .verifyComplete();
    }

    /**
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     */
    @Test
    void testValidateErrorWhenMetadataSetIsNotFound() {
        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.empty());
        StepVerifier.create(f24ServiceImpl.validate("42", "42")).
                expectError(PnNotFoundException.class)
                .verify();
    }

    /**
     * Non invia evento su coda.
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     */
    @Test
    void testValidateOkWhenMetadataIsInStatusToValidate() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("test");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        F24MetadataSet f24MetadataSetUpdated = new F24MetadataSet();
        f24MetadataSetUpdated.setSetId("test");
        f24MetadataSetUpdated.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSetUpdated.setHaveToSendValidationEvent(true);
        f24MetadataSetUpdated.setValidatorCxId("validatiorCxIdTest");
        when(f24MetadataSetDao.updateItem(any()))
                .thenReturn(Mono.just(f24MetadataSetUpdated));

        when(f24MetadataSetDao.getItem(any(), anyBoolean()))
                .thenReturn(Mono.just(f24MetadataSetUpdated));


        StepVerifier.create(f24ServiceImpl.validate("42", "42"))
                .expectNextMatches(requestAccepted -> requestAccepted.getStatus().equalsIgnoreCase(DEFAULT_SUCCESS_STATUS))
                .verifyComplete();
    }

    /**
     * Non invia evento su coda.
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     */
    @Test
    void testValidateOkWhenQueueValidationEndsAndSendEvent() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("test");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        F24MetadataSet f24MetadataSetUpdated = new F24MetadataSet();
        f24MetadataSetUpdated.setSetId("test");
        f24MetadataSetUpdated.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSetUpdated.setHaveToSendValidationEvent(true);
        f24MetadataSetUpdated.setValidatorCxId("validatiorCxIdTest");
        when(f24MetadataSetDao.updateItem(any()))
                .thenReturn(Mono.just(f24MetadataSetUpdated));

        when(f24MetadataSetDao.getItem(any(), anyBoolean()))
                .thenReturn(Mono.just(f24MetadataSetUpdated));

        doNothing().when(metadataValidationEndedEventProducer).sendEvent(any());

        //Update finale con eventSent = true
        F24MetadataSet f24MetadataSetUpdated2 = new F24MetadataSet();
        f24MetadataSetUpdated2.setSetId("test");
        f24MetadataSetUpdated2.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSetUpdated2.setHaveToSendValidationEvent(true);
        f24MetadataSetUpdated2.setValidationEventSent(true);
        f24MetadataSetUpdated2.setValidatorCxId("validatiorCxIdTest");
        when(f24MetadataSetDao.updateItem(any()))
                .thenReturn(Mono.just(f24MetadataSetUpdated));

        StepVerifier.create(f24ServiceImpl.validate("42", "42"))
                .expectNextMatches(requestAccepted -> requestAccepted.getStatus().equalsIgnoreCase(DEFAULT_SUCCESS_STATUS))
                .verifyComplete();
    }

    @Test
    public void generatePdfFromCacheSuccessWithRetryAfter() {

        //Mock for f24FileDao.getItem
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setPk("fileMetadata");
        f24File.setCreated(Instant.now());
        f24File.setStatus(F24FileStatus.TO_PROCESS);
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
    public void generatePdfSuccessWhenFileIsNotInCache() {

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
        when(safeStorageService.getFilePolling(anyString(), any(), any(), any()))
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
    public void generatePdfErrorWhenGivenPathTokensDoesNotExistInMetadataSet() {

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
    public void generatePdfFailOnMetadataWithoutApplyCostWhenRequestCostIsNotNull() {

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

    @Test
    void preparePdfFailsWhenPathTokensExceedAllowedDimension() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(200);
        prepareF24Request.setPathTokens(List.of("0","0","0","0","0"));

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    @Test
    void preparePdfFailsWhenMetadataSetIsNotFound() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(200);
        prepareF24Request.setPathTokens(List.of("notExisting"));

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void preparePdfFailsWhenMetadataSetHasNotPathTokensGivenInRequestBody() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(200);
        prepareF24Request.setPathTokens(List.of("notExisting"));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        f24MetadataSet.setFileKeys(Map.of("0_0", new F24MetadataRef()));
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void preparePdfFailsWhenMetadataHasApplyCostAndIsNotGivenInRequestBody() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(null);
        prepareF24Request.setPathTokens(List.of("0"));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setApplyCost(true);
        f24MetadataSet.setFileKeys(Map.of("0_0", f24MetadataRef));
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    @Test
    void preparePdfFailsWhenMetadataHasNotApplyCostButCostIsGivenInRequestBody() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(200);
        prepareF24Request.setPathTokens(List.of("0"));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setApplyCost(false);
        f24MetadataSet.setFileKeys(Map.of("0_0", f24MetadataRef));
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnBadRequestException.class)
                .verify();
    }

    @Test
    void preparePdfFailsWhenRequestExistsAndHasDifferentRequestBody() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(null);
        prepareF24Request.setPathTokens(List.of("0"));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setApplyCost(false);
        f24MetadataSet.setFileKeys(Map.of("0_0", f24MetadataRef));
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setSetId("setId");
        f24Request.setCxId("cxId");
        f24Request.setPathTokens("0");
        f24Request.setCost(200);
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectError(PnConflictException.class)
                .verify();
    }

    @Test
    void preparePdfSuccessWhenRequestDoesNotExists() {
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId("requestId");
        prepareF24Request.setId("setId");
        prepareF24Request.setNotificationCost(null);
        prepareF24Request.setPathTokens(List.of("0"));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setApplyCost(false);
        f24MetadataSet.setFileKeys(Map.of("0_0", f24MetadataRef));
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.empty());

        doNothing().when(preparePdfEventProducer).push((PreparePdfEvent) any());

        when(f24FileRequestDao.putItemIfAbsent(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(f24ServiceImpl.preparePDF("cxId", "requestId", Mono.just(prepareF24Request)))
                .expectNextMatches(requestAccepted -> requestAccepted.getStatus().equalsIgnoreCase(DEFAULT_SUCCESS_STATUS))
                .expectComplete()
                .verify();
    }

}