package it.pagopa.pn.f24.it;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.it.util.EventBridgeEventType;
import it.pagopa.pn.f24.it.util.TestUtils;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24MetadataValidationEndedEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24PdfSetReadyEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.impl.*;
import it.pagopa.pn.f24.util.Sha256Handler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Mono;

import java.util.List;

import static it.pagopa.pn.f24.it.util.TestUtils.*;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        F24FileCacheDaoMock.class,
        F24ServiceImpl.class,
        F24GeneratorImpl.class,
        PnF24MetadataValidationEndedEventBridgeProducerImpl.class,
        PnF24PdfSetReadyEventBridgeProducerImpl.class,
        EventBridgeAsyncClientMock.class,
        JsonServiceImpl.class,
        LocalValidatorFactoryBean.class,
        ValidateMetadataSetSqsProducerMock.class,
        F24MetadataSetDaoMock.class,
        MetadataDownloaderImpl.class,
        AuditLogServiceImpl.class,
        SafeStorageServiceImpl.class,
        SafeStorageEventService.class,
        GeneratePDFTestIT.SpringTestConfiguration.class,
        ValidateMetadataEventService.class,
        MetadataValidatorImpl.class,
        PreparePdfEventService.class,
        GeneratePdfEventService.class
})
@DirtiesContext
@EnableConfigurationProperties(value = F24Config.class)
@TestPropertySource("classpath:/application-test.properties")
public class SaveMetadataTestIT {

    @TestConfiguration
    static class SpringTestConfiguration extends AbstractWorkflowTestConfiguration {

        public SpringTestConfiguration() {
            super();
        }
    }
    @Autowired
    F24ServiceImpl f24Service;
    @Autowired
    F24MetadataSetDaoMock f24MetadataSetDaoMock;
    @Autowired
    F24FileCacheDaoMock f24FileCacheDaoMock;
    @MockitoSpyBean
    ValidateMetadataSetSqsProducerMock validateMetadataSetSqsProducerMock;
    @MockitoSpyBean
    PnF24MetadataValidationEndedEventBridgeProducerImpl validationEndedEventBridgeProducer;
    @MockitoSpyBean
    ValidateMetadataEventService validateMetadataEventService;
    @MockitoSpyBean
    EventBridgeAsyncClientMock eventBridgeAsyncClientMock;

    @BeforeEach
    public void setup() {
        TestUtils.initializeAllMockClient(
                List.of(f24FileCacheDaoMock, f24MetadataSetDaoMock, validateMetadataSetSqsProducerMock)
        );
    }

    @Test
    public void saveNewMetadataSetProcessSuccessfullyWhenValidationRequestOccursAfterQueueTaskEnded() {
        /*
            Salvataggio di un set di metadati validi con richiesta di validazione pervenuta dopo aver terminato il processo di validazione su coda.
        */

        //GIVEN
        String setId = "setId";

        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setFileKey(METADATA_SIMPLIFIED_WITH_COST_FILEKEY);
        saveF24Item.setApplyCost(true);
        saveF24Item.setSha256(Sha256Handler.computeSha256(getMetadataByFilekey(METADATA_SIMPLIFIED_WITH_COST_FILEKEY)));
        List<String> pathTokens = List.of("pathTokens");
        saveF24Item.setPathTokens(pathTokens);

        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setSetId(setId);
        saveF24Request.setF24Items(List.of(saveF24Item));

        //WHEN
        f24Service.saveMetadata("pn-delivery", setId, Mono.just(saveF24Request)).block();

        //Attendiamo che la coda esegua la validazione del set e aggiorni lo status.
        await().untilAsserted(() -> {
            Assertions.assertTrue(TestUtils.checkMetadataSetValidationEnded(setId, f24MetadataSetDaoMock));
        });

        Assertions.assertTrue(TestUtils.checkMetadataSetIsValid(setId, f24MetadataSetDaoMock));
        Mockito.verify(validationEndedEventBridgeProducer, Mockito.times(0)).sendEvent(Mockito.any(PnF24MetadataValidationEndEvent.class));

        f24Service.validate("pn-delivery-push", setId).block();

        //VERIFY
        Mockito.verify(validateMetadataSetSqsProducerMock).push((List<ValidateMetadataSetEvent>) Mockito.any());
        Mockito.verify(validationEndedEventBridgeProducer, Mockito.times(1)).sendEvent(Mockito.any(PnF24MetadataValidationEndEvent.class));
        Assertions.assertTrue(TestUtils.checkMetadataSetValidationEventIsSent(setId, f24MetadataSetDaoMock));
        TestUtils.checkEventSentOnEventBridge(true, EventBridgeEventType.METADATA_VALIDATION, eventBridgeAsyncClientMock);
    }

    @Test
    public void saveNewMetadataSetProcessSuccessfullyWhenValidationRequestOccursBeforeQueueTaskEnded() {
        /*
            Salvataggio di un set di metadati validi con richiesta di validazione pervenuta prima di aver terminato il processo di validazione su coda.
        */

        //GIVEN
        String setId = "setId";

        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setFileKey(METADATA_SIMPLIFIED_WITH_COST_FILEKEY);
        saveF24Item.setApplyCost(true);
        saveF24Item.setSha256(Sha256Handler.computeSha256(getMetadataByFilekey(METADATA_SIMPLIFIED_WITH_COST_FILEKEY)));
        List<String> pathTokens = List.of("pathTokens");
        saveF24Item.setPathTokens(pathTokens);

        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setSetId(setId);
        saveF24Request.setF24Items(List.of(saveF24Item));

        //Chiediamo alla coda di attendere una chiamata all'API di validazione prima di eseguire il flusso di validazione
        validateMetadataSetSqsProducerMock.setWaitValidationApiCall(true);

        //WHEN
        f24Service.saveMetadata("pn-delivery", setId, Mono.just(saveF24Request)).block();

        //Verifichiamo che il processo su coda non sia partito
        Mockito.verify(validateMetadataEventService, Mockito.times(0)).handleMetadataValidation(Mockito.any());

        f24Service.validate("pn-delivery-push", setId).block();

        //VERIFY
        Mockito.verify(validateMetadataSetSqsProducerMock).push((List<ValidateMetadataSetEvent>) Mockito.any());

        // Attendo che il processo di validazione su coda termini
        await().untilAsserted(() -> {
            Assertions.assertTrue(TestUtils.checkMetadataSetValidationEnded(setId, f24MetadataSetDaoMock));
        });

        Mockito.verify(validationEndedEventBridgeProducer, Mockito.times(1)).sendEvent(Mockito.any(PnF24MetadataValidationEndEvent.class));
        Assertions.assertTrue(TestUtils.checkMetadataSetValidationEventIsSent(setId, f24MetadataSetDaoMock));
        Assertions.assertTrue(TestUtils.checkMetadataSetIsValid(setId, f24MetadataSetDaoMock));
        TestUtils.checkEventSentOnEventBridge(true, EventBridgeEventType.METADATA_VALIDATION, eventBridgeAsyncClientMock);
    }

    @Test
    public void saveNewMetadataProcessSetFailsWhenMetadataAreInvalid() {
        /*
            Salvataggio di un set di metadati validi con richiesta di validazione pervenuta prima di aver terminato il processo di validazione su coda.
        */

        //GIVEN
        String setId = "setId";

        SaveF24Item saveF24Item = new SaveF24Item();
        saveF24Item.setFileKey(INVALID_METADATA_SIMPLIFIED_WITH_COST_FILEKEY);
        saveF24Item.setApplyCost(true);
        saveF24Item.setSha256(Sha256Handler.computeSha256(getMetadataByFilekey(INVALID_METADATA_SIMPLIFIED_WITH_COST_FILEKEY)));
        List<String> pathTokens = List.of("pathTokens");
        saveF24Item.setPathTokens(pathTokens);

        SaveF24Request saveF24Request = new SaveF24Request();
        saveF24Request.setSetId(setId);
        saveF24Request.setF24Items(List.of(saveF24Item));

        //WHEN
        f24Service.saveMetadata("pn-delivery", setId, Mono.just(saveF24Request)).block();

        // Attendo che il processo di validazione su coda termini
        await().untilAsserted(() -> {
            Assertions.assertTrue(TestUtils.checkMetadataSetValidationEnded(setId, f24MetadataSetDaoMock));
        });

        f24Service.validate("pn-delivery-push", setId).block();

        //VERIFY
        Mockito.verify(validateMetadataSetSqsProducerMock).push((List<ValidateMetadataSetEvent>) Mockito.any());
        Mockito.verify(validationEndedEventBridgeProducer, Mockito.times(1)).sendEvent(Mockito.any(PnF24MetadataValidationEndEvent.class));
        Assertions.assertTrue(TestUtils.checkMetadataSetValidationEventIsSent(setId, f24MetadataSetDaoMock));
        Assertions.assertFalse(TestUtils.checkMetadataSetIsValid(setId, f24MetadataSetDaoMock));
        TestUtils.checkEventSentOnEventBridge(false, EventBridgeEventType.METADATA_VALIDATION, eventBridgeAsyncClientMock);
    }
}
