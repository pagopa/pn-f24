package it.pagopa.pn.f24.it;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.PrepareF24Request;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.it.util.EventBridgeEventType;
import it.pagopa.pn.f24.it.util.TestCase;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

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
        SpringValidatorAdapter.class,
        ValidateMetadataSetSqsProducerMock.class,
        F24MetadataSetDaoMock.class,
        MetadataDownloaderImpl.class,
        AuditLogServiceImpl.class,
        SafeStorageServiceImpl.class,
        SafeStorageEventService.class,
        PreparePDFTestIT.SpringTestConfiguration.class,
        ValidateMetadataEventService.class,
        MetadataValidatorImpl.class,
        PreparePdfEventService.class,
        GeneratePdfEventService.class
})
@DirtiesContext
@EnableConfigurationProperties(value = F24Config.class)
@TestPropertySource("classpath:/application-test.properties")
public class PreparePDFTestIT {

    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";

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
    @Autowired
    private F24FileRequestDaoMock f24FileRequestDaoMock;
    @SpyBean
    private SafeStorageEventService safeStorageEventService;
    @SpyBean
    private PreparePdfEventService preparePdfEventService;
    @SpyBean
    private GeneratePdfEventService generatePdfEventService;
    @SpyBean
    private EventBridgeAsyncClientMock eventBridgeAsyncClientMock;

    @BeforeEach
    public void setup() {
        TestUtils.initializeAllMockClient(
                List.of(f24FileCacheDaoMock, f24MetadataSetDaoMock, f24FileRequestDaoMock)
        );
    }

    @Test
    public void testPreparePDFSuccessWithCost() {
        /*
            Generazione di un insieme di PDF in modalità asincrona (nessuno presente in CACHE).
        */

        //GIVEN
        String requestId = "PREPARE_PDF_0001";
        String setId = "setId";
        List<String> pathTokens = List.of("0");
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId(requestId);
        prepareF24Request.setSetId(setId);
        prepareF24Request.setNotificationCost(100);
        prepareF24Request.setPathTokens(pathTokens);

        // Precarico il DB con un set di due metadati (con costo e senza).
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetByTestCases(
                setId, List.of(TestCase.METADATA_SIMPLIFIED_WITH_COST, TestCase.METADATA_SIMPLIFIED_WITHOUT_COST)
        ));

        //WHEN
        StepVerifier.create(f24Service.preparePDF("xPagopaF24CxId", requestId, Mono.just(prepareF24Request)))
                .expectNextMatches(f24Response -> f24Response.getStatus().equalsIgnoreCase(REQUEST_ACCEPTED_STATUS) && f24Response.getDescription().equalsIgnoreCase(REQUEST_ACCEPTED_DESCRIPTION))
                .expectComplete()
                .verify();

        //VERIFY

        //Attendo che sia inviato un evento di preparazione PDF
        await().untilAsserted(() -> {
            Mockito.verify(preparePdfEventService).preparePdf(Mockito.any());
        });

        //Attendo che siano inviati due evento di generazione PDF (1 per ogni file incluso nel metadato precaricato)
        await().untilAsserted(() -> {
            Mockito.verify(generatePdfEventService, Mockito.times(2)).generatePdf(Mockito.any());
        });

        //Attendo che siano inviati due eventi di file caricato su safestorage.
        await().untilAsserted(() -> {
            Mockito.verify(safeStorageEventService, Mockito.times(2)).handleSafeStorageResponse(Mockito.any());
        });

        //Attendo e controllo che la richiesta sia passata a status DONE su DB
        await().untilAsserted(() -> {
            TestUtils.checkPrepareRequestEndedSuccessfully(requestId, f24FileRequestDaoMock);
        });

        //Verifico sia stato inviato un evento su EventBridge di pdfReady con status OK.
        TestUtils.checkEventSentOnEventBridge(true, EventBridgeEventType.PDF_READY, eventBridgeAsyncClientMock);
    }

    @Test
    public void testPreparePDFSuccessWithoutCost() {
        /*
            Generazione di un insieme di PDF in modalità asincrona (nessuno presente in CACHE).
        */

        //GIVEN
        String requestId = "PREPARE_PDF_0001";
        String setId = "setId";
        List<String> pathTokens = List.of("0");
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId(requestId);
        prepareF24Request.setSetId(setId);
        prepareF24Request.setNotificationCost(null);
        prepareF24Request.setPathTokens(pathTokens);

        // Precarico il DB con un set di due metadati (con costo e senza).
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetByTestCases(
                setId, List.of(TestCase.METADATA_SIMPLIFIED_WITHOUT_COST)
        ));

        //WHEN
        StepVerifier.create(f24Service.preparePDF("xPagopaF24CxId", requestId, Mono.just(prepareF24Request)))
                .expectNextMatches(f24Response -> f24Response.getStatus().equalsIgnoreCase(REQUEST_ACCEPTED_STATUS) && f24Response.getDescription().equalsIgnoreCase(REQUEST_ACCEPTED_DESCRIPTION))
                .expectComplete()
                .verify();

        //VERIFY

        //Attendo che sia inviato un evento di preparazione PDF
        await().untilAsserted(() -> {
            Mockito.verify(preparePdfEventService).preparePdf(Mockito.any());
        });

        //Attendo che siano inviati due evento di generazione PDF (1 per ogni file incluso nel metadato precaricato)
        await().untilAsserted(() -> {
            Mockito.verify(generatePdfEventService, Mockito.times(1)).generatePdf(Mockito.any());
        });

        //Attendo che siano inviati due eventi di file caricato su safestorage.
        await().untilAsserted(() -> {
            Mockito.verify(safeStorageEventService, Mockito.times(1)).handleSafeStorageResponse(Mockito.any());
        });

        //Attendo e controllo che la richiesta sia passata a status DONE su DB
        await().untilAsserted(() -> {
            TestUtils.checkPrepareRequestEndedSuccessfully(requestId, f24FileRequestDaoMock);
        });

        //Verifico sia stato inviato un evento su EventBridge di pdfReady con status OK.
        TestUtils.checkEventSentOnEventBridge(true, EventBridgeEventType.PDF_READY, eventBridgeAsyncClientMock);
    }

    @Test
    public void testPreparePDFSuccessWithCostWhenFileIsAlreadyInCache() {
        /*
            Generazione di un insieme di PDF in modalità asincrona (con i file già presenti in CACHE).
        */

        //GIVEN
        String requestId = "PREPARE_PDF_0002";
        String setId = "setId";
        Integer cost = 100;
        List<String> pathTokens = List.of("0");
        PrepareF24Request prepareF24Request = new PrepareF24Request();
        prepareF24Request.setRequestId(requestId);
        prepareF24Request.setSetId(setId);
        prepareF24Request.setNotificationCost(100);
        prepareF24Request.setPathTokens(pathTokens);

        // Precarico il DB con un set di un metadato (con costo).
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetByTestCases(
                setId, List.of(TestCase.METADATA_SIMPLIFIED_WITH_COST)
        ));
        // Precarico il DB con il file generato a partire dal metadato creato prima.
        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileDone(setId, cost, "0_0"));

        //WHEN
        StepVerifier.create(f24Service.preparePDF("xPagopaF24CxId", requestId, Mono.just(prepareF24Request)))
                .expectNextMatches(f24Response -> f24Response.getStatus().equalsIgnoreCase(REQUEST_ACCEPTED_STATUS) && f24Response.getDescription().equalsIgnoreCase(REQUEST_ACCEPTED_DESCRIPTION))
                .expectComplete()
                .verify();

        //VERIFY

        //Attendo che sia inviato un evento di preparazione PDF
        await().untilAsserted(() -> {
            Mockito.verify(preparePdfEventService).preparePdf(Mockito.any());
        });

        //Controllo che non sia inviato un evento di generazione del PDF (Il file era già in cache)
        Mockito.verify(generatePdfEventService, Mockito.times(0)).generatePdf(Mockito.any());


        //Controllo che non sia inviato un evento di file caricato su safestorage.
        Mockito.verify(safeStorageEventService, Mockito.times(0)).handleSafeStorageResponse(Mockito.any());


        //Attendo e controllo che la richiesta sia passata a status DONE su DB
        await().untilAsserted(() -> {
            TestUtils.checkPrepareRequestEndedSuccessfully(requestId, f24FileRequestDaoMock);
        });

        //Verifico sia stato inviato un evento su EventBridge di pdfReady con status OK.
        TestUtils.checkEventSentOnEventBridge(true, EventBridgeEventType.PDF_READY, eventBridgeAsyncClientMock);
    }
}
