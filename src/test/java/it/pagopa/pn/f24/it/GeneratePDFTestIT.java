package it.pagopa.pn.f24.it;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.it.util.TestUtils;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24MetadataValidationEndedEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.AuditLogService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        F24FileCacheDaoMock.class,
        F24ServiceImpl.class,
        F24GeneratorImpl.class,
        PnF24MetadataValidationEndedEventBridgeProducerImpl.class,
        PnF24PdfSetReadyEventBridgeProducerImplMock.class,
        EventBridgeAsyncClientMock.class,
        JsonServiceImpl.class,
        SpringValidatorAdapter.class,
        GeneratePdfSqsProducerMock.class,
        PreparePdfSqsProducerMock.class,
        ValidateMetadataSetSqsProducerMock.class,
        F24MetadataSetDaoMock.class,
        MetadataDownloaderImpl.class,
        F24FileRequestDaoMock.class,
        AuditLogServiceImpl.class,
        SafeStorageServiceImpl.class,
        SafeStorageEventService.class,
        GeneratePDFTestIT.SpringTestConfiguration.class, ValidateMetadataEventService.class,
        MetadataValidatorImpl.class

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
    F24GeneratorImpl f24Generator;

    @Autowired
    private EventBridgeProducer<PnF24MetadataValidationEndEvent> metadataValidationEndedEventProducer;

    @Autowired
    private PnSafeStorageClientMock pnSafeStorageClientMockMock;

    @Autowired
    private MomProducer<ValidateMetadataSetEvent> validateMetadataSetEventProducer;

    @Autowired
    private MomProducer<PreparePdfEvent> preparePdfEventProducer;

    @Autowired
    private F24MetadataSetDaoMock f24MetadataSetDaoMock;

    @Autowired
    private F24FileRequestDao f24FileRequestDao;

    @Autowired
    private JsonServiceImpl jsonServiceImpl;

    @Autowired
    private F24FileCacheDaoMock f24FileCacheDaoMock;

    @Autowired
    private MetadataDownloader metadataDownloader;

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    F24Config f24Config;

    @BeforeEach
    public void setup() {

        TestUtils.initializeAllMockClient(
               f24FileCacheDaoMock,
                f24MetadataSetDaoMock
        );
    }


    //todo:creare cosnt per valori di ritorno dai mock
    @Test
    public void testGeneratePDFNotNull() {

        //GIVEN
        String setId = "setId";
        List<String> pathTokens = List.of("pathTokens");
        //precarico il DB con un file avente lo status DONE
        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileDone(setId, 1, pathTokens.get(0)));
        //verico il ritorno del metodo
        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, 1))
                .expectNextMatches(f24Response -> {
                    return f24Response.getUrl() != null;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testGeneratePDFNotNullRetryAfterWithCost() {

        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileToProcess(setId, 1, pathTokens.get(0)));
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, 1))
                .expectError(PnDbConflictException.class)
                .verify();
    }

    @Test
    public void testGeneratePDFNotNullRetryAfterWithoutCost() {

        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileToProcess(setId, null, pathTokens.get(0)));
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithoutApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, null))
                .expectError(PnDbConflictException.class)
                .verify();
    }

    @Test
    public void testGeneratePDFfileHasNotBeenUpdatedRecently() {

        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24FileCacheDaoMock.putItemIfAbsent(TestUtils.createF24FileToProcessLate(setId, null, pathTokens.get(0)));
        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithoutApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, null))
                .expectError(PnDbConflictException.class)
                .verify();
    }

    @Test
    public void testGeneratePDFNullWithoutCost() {

        String setId = "setIdWithoutApplyCost";
        List<String> pathTokens = List.of("key");

        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithoutApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, null))
                .expectNextMatches(f24Response -> {
                    return f24Response.getUrl() != null;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testGeneratePDFNullWithCost() {

        String setId = "setIdToProcess";
        List<String> pathTokens = List.of("key");

        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithApplyCost(setId));

        StepVerifier.create(f24Service.generatePDF("xPagopaF24CxId", setId, pathTokens, 1))
                .expectNextMatches(f24Response -> {
                    return f24Response.getUrl() != null;
                })
                .expectComplete()
                .verify();

    }
}
