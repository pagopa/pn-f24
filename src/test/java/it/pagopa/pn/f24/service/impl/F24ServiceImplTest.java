package it.pagopa.pn.f24.service.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnConflictException;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.util.Sha256Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ContextConfiguration(classes = {F24ServiceImpl.class})
@ExtendWith(SpringExtension.class)
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
    private MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;

    @MockBean
    private PnSafeStorageClientImpl pnSafeStorageClientImpl;

    @MockBean
    private JsonService jsonService;

    @MockBean
    private F24Config f24Config;


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

    /**
     * Method under test: {@link F24ServiceImpl#generatePDF(String, String, String)}
     */
    @Test
    void testGeneratePDF() {
        assertNull(f24ServiceImpl.generatePDF("Iun", "Recipient Index", "Attachment Index"));
    }
}

