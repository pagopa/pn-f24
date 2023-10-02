package it.pagopa.pn.f24.middleware.queue.consumer.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.MetadataValidator;
import it.pagopa.pn.f24.service.SafeStorageService;
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
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Map;

@ContextConfiguration(classes = {ValidateMetadataEventService.class, F24Config.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
public class ValidateMetadataEventServiceTest {
    @MockBean
    private EventBridgeProducer<PnF24MetadataValidationEndEvent> eventBridgeProducer;

    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;

    @MockBean
    private MetadataValidator metadataValidator;

    @MockBean
    private SafeStorageService safeStorageService;

    @Autowired
    private ValidateMetadataEventService validateMetadataEventService;

    @Test
    public void testHandleMetadataValidation() {

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);


        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        StepVerifier.create(validateMetadataEventService.handleMetadataValidation(new ValidateMetadataSetEvent.Payload("42")))
                .verifyComplete();
    }

    @Test
    public void testStartMetadataValidationValidateMetadataList() {

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        F24Metadata f24Metadata = new F24Metadata();
        MetadataToValidate metadataToValidate = new MetadataToValidate();

        metadataToValidate.setMetadataFile("f24MetadataRef".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("testPathTokensKey");
        metadataToValidate.setF24Metadata(f24Metadata);

        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys(Map.of("test", f24MetadataRef));
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setSetId("testSetId");
        f24MetadataSet.setValidatorCxId("testValidatorCxId");

        f24MetadataRef.setFileKey("test");
        Long maxSize = -1L;

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();

        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("testUrl");

        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(f24MetadataSetDao.getItem(any(),anyBoolean()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.getFile(f24MetadataRef.getFileKey(), false))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(safeStorageService.downloadPieceOfContent(f24MetadataRef.getFileKey(), fileDownloadInfoInt.getUrl(), maxSize))
                .thenReturn(Mono.just("test".getBytes()));
        doNothing().when(eventBridgeProducer).sendEvent(any());
        when(f24MetadataSetDao.setF24MetadataSetStatusValidationEnded(f24MetadataSet))
                .thenReturn(Mono.just(f24MetadataSet));


        StepVerifier.create(validateMetadataEventService.handleMetadataValidation(new ValidateMetadataSetEvent.Payload("42")))
                .verifyComplete();
    }

    @Test
    public void testStartMetadataValidationgetHaveToSendValidationEventFalse() {

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        F24Metadata f24Metadata = new F24Metadata();
        MetadataToValidate metadataToValidate = new MetadataToValidate();

        metadataToValidate.setMetadataFile("f24MetadataRef".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("testPathTokensKey");
        metadataToValidate.setF24Metadata(f24Metadata);

        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys(Map.of("test", f24MetadataRef));
        f24MetadataSet.setHaveToSendValidationEvent(false);
        f24MetadataSet.setSetId("testSetId");
        f24MetadataSet.setValidatorCxId("testValidatorCxId");

        f24MetadataRef.setFileKey("test");
        Long maxSize = -1L;

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();

        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("testUrl");

        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(f24MetadataSetDao.getItem(any(),anyBoolean()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.getFile(f24MetadataRef.getFileKey(), false))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(safeStorageService.downloadPieceOfContent(f24MetadataRef.getFileKey(), fileDownloadInfoInt.getUrl(), maxSize))
                .thenReturn(Mono.just("test".getBytes()));
        doNothing().when(eventBridgeProducer).sendEvent(any());
        when(f24MetadataSetDao.setF24MetadataSetStatusValidationEnded(f24MetadataSet))
                .thenReturn(Mono.just(f24MetadataSet));


        StepVerifier.create(validateMetadataEventService.handleMetadataValidation(new ValidateMetadataSetEvent.Payload("42")))
                .verifyComplete();
    }

    @Test
    public void testStartMetadataValidationConditionalCheckFailedException() {

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        F24Metadata f24Metadata = new F24Metadata();
        MetadataToValidate metadataToValidate = new MetadataToValidate();

        metadataToValidate.setMetadataFile("f24MetadataRef".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("testPathTokensKey");
        metadataToValidate.setF24Metadata(f24Metadata);

        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys(Map.of("test", f24MetadataRef));
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setSetId("testSetId");
        f24MetadataSet.setValidatorCxId("testValidatorCxId");

        f24MetadataRef.setFileKey("test");
        Long maxSize = -1L;

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();

        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("testUrl");

        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(f24MetadataSetDao.getItem(any(),anyBoolean()))
                .thenReturn(Mono.just(f24MetadataSet));
        when(safeStorageService.getFile(f24MetadataRef.getFileKey(), false))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(safeStorageService.downloadPieceOfContent(f24MetadataRef.getFileKey(), fileDownloadInfoInt.getUrl(), maxSize))
                .thenReturn(Mono.just("test".getBytes()));
        doNothing().when(eventBridgeProducer).sendEvent(any());
        when(f24MetadataSetDao.setF24MetadataSetStatusValidationEnded(any()))
                .thenReturn(Mono.error(ConditionalCheckFailedException.builder().build()));


        StepVerifier.create(validateMetadataEventService.handleMetadataValidation(new ValidateMetadataSetEvent.Payload("42")))
                .verifyComplete();
    }

    @Test
    public void getMetadataSet() {

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(validateMetadataEventService.handleMetadataValidation(new ValidateMetadataSetEvent.Payload("42")))
                .expectError(PnNotFoundException.class)
                .verify();
    }


}

