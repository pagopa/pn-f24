package it.pagopa.pn.f24.it;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.exception.PnF24RuntimeException;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.it.util.TestUtils;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24MetadataValidationEndedEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24PdfSetReadyEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        F24FileCacheDaoMock.class,
        F24ServiceImpl.class,
        F24GeneratorImpl.class,
        PnF24MetadataValidationEndedEventBridgeProducerImpl.class,
        PnF24PdfSetReadyEventBridgeProducerImpl.class,
        EventBridgeAsyncClientMock.class,
        JsonServiceImpl.class,
        SpringValidatorAdapter.class,
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
public class GeneratePDFTestIT {

    @TestConfiguration
    static class SpringTestConfiguration extends AbstractWorkflowTestConfiguration {
        public SpringTestConfiguration() {
            super();
        }
    }

    @Autowired
    F24ServiceImpl f24Service;

    @Autowired
    private F24MetadataSetDaoMock f24MetadataSetDaoMock;
    @Autowired
    private F24FileCacheDaoMock f24FileCacheDaoMock;
    @MockitoSpyBean
    private SafeStorageEventService safeStorageEventService;

    @BeforeEach
    public void setup() {
        TestUtils.initializeAllMockClient(
                List.of(f24FileCacheDaoMock, f24MetadataSetDaoMock)
        );
    }

    @Test
    public void testGeneratePDFWithAFileInCacheReady() {
        /*
            Generazione di un PDF sincrono utilizzando gli estremi di un file già presente in cache in status DONE.
            Il servizio dovrebbe essere in grado di rispondere con la URL di download.
        */

        //GIVEN
        String setId = "setId";
        List<String> pathTokens = List.of("pathTokens");

        // precarico il DB con un file avente lo status DONE
        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileDone(setId, 1, pathTokens.get(0)));

        //WHEN
        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, 1))
                .expectNextMatches(f24Response -> f24Response.getUrl() != null)
                .expectComplete()
                .verify();
    }

    @Test
    public void testGeneratePDFWithARecentlyCreatedFileInCacheNotProcessedYet() {
        /*
            Generazione di un PDF sincrono utilizzando gli estremi di un file da poco caricato in CACHE con stato TO_PROCESS.
            La risposta del servizio dovrebbe essere un retryAfter, poichè si considera il file in elaborazione e possibilmente in poco tempo
            potrebbe essere disponibile per il download.
        */

        //GIVEN
        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileToProcess(setId, 1, pathTokens.get(0)));
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithApplyCost(setId));

        //WHEN
        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, 1))
                .expectNextMatches(f24Response -> f24Response.getRetryAfter() != null)
                .expectComplete()
                .verify();
    }

    @Test
    public void testGeneratePDFWithAFileInCacheNotReadyThatHasNotBeenUpdatedRecently() {
        /*
            Generazione di un PDF sincrono utilizzando gli estremi di un file caricato in CACHE con stato TO_PROCESS ma non aggiornato di recente.
            La risposta del servizio dovrebbe essere un errore, poichè si considera che ci sia stato un problema durante l'elaborazione del file.
        */

        //GIVEN
        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileToProcessLate(setId, null, pathTokens.get(0)));
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithoutApplyCost(setId));

        //WHEN
        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, null))
                .expectError(PnF24RuntimeException.class)
                .verify();
    }

    @Test
    public void testGeneratePDFCreatingNewFileInCacheWithoutCost() {
        /*
            Generazione di un PDF sincrono utilizzando gli estremi di un file non presente in CACHE senza passare un costo.
            La risposta del servizio dovrebbe essere positiva con un URL di download del pdf generato e caricato su safestorage.
        */

        //GIVEN
        String setId = "setIdWithoutApplyCost";
        List<String> pathTokens = List.of("key");
        Integer cost = null;

        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithoutApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, cost))
                .expectNextMatches(f24Response -> f24Response.getUrl() != null)
                .expectComplete()
                .verify();

        //VERIFY
        TestUtils.checkSuccessfulF24Generation(setId, pathTokens, cost, f24FileCacheDaoMock, safeStorageEventService);

    }

    @Test
    public void testGeneratePDFCreatingNewFileInCacheWithCost() {
        /*
            Generazione di un PDF sincrono utilizzando gli estremi di un file non presente in CACHE passando un costo.
            La risposta del servizio dovrebbe essere positiva con un URL di download del pdf generato e caricato su safestorage.
        */

        //GIVEN
        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");
        int cost = 1;

        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithApplyCost(setId));

        //WHEN
        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, cost))
                .expectNextMatches(f24Response -> f24Response.getUrl() != null)
                .expectComplete()
                .verify();

        //VERIFY

        TestUtils.checkSuccessfulF24Generation(setId, pathTokens, cost, f24FileCacheDaoMock, safeStorageEventService);
    }
}
