package it.pagopa.pn.f24.it;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Item;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.it.util.TestUtils;
import it.pagopa.pn.f24.middleware.eventbus.impl.PnF24MetadataValidationEndedEventBridgeProducerImpl;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.service.impl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static it.pagopa.pn.f24.dto.F24MetadataStatus.VALIDATION_ENDED;
import static it.pagopa.pn.f24.it.util.TestUtils.*;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {F24FileCacheDaoMock.class, F24ServiceImpl.class, F24GeneratorImpl.class, PnF24MetadataValidationEndedEventBridgeProducerImpl.class, PnF24PdfSetReadyEventBridgeProducerImplMock.class, EventBridgeAsyncClientMock.class, JsonServiceImpl.class, LocalValidatorFactoryBean.class, GeneratePdfSqsProducerMock.class, PreparePdfSqsProducerMock.class, ValidateMetadataSetSqsProducerMock.class, F24MetadataSetDaoMock.class, MetadataDownloaderImpl.class, F24FileRequestDaoMock.class, AuditLogServiceImpl.class, SafeStorageServiceImpl.class, SafeStorageEventService.class, GeneratePDFTestIT.SpringTestConfiguration.class, ValidateMetadataEventService.class, MetadataValidatorImpl.class

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

    private static final String REQUEST_ACCEPTED_STATUS = "Success!";
    private static final String REQUEST_ACCEPTED_DESCRIPTION = "Ok";
    @Autowired
    F24ServiceImpl f24Service;
    @Autowired
    F24MetadataSetDaoMock f24MetadataSetDaoMock;

    @Autowired
    F24FileCacheDaoMock f24FileCacheDaoMock;


    @BeforeEach
    public void setup() {

        TestUtils.initializeAllMockClient(f24FileCacheDaoMock, f24MetadataSetDaoMock);
    }

    @Test
    public void saveMetadataTestHandleNewMetadata() {

        String setId = "setId";
        SaveF24Request saveF24Request = new SaveF24Request();
        SaveF24Item saveF24Item = new SaveF24Item();

        String sha256 = computeSha256(getMetadataByFilekey(METADATA_SIMPLIFIED_FILEKEY));

        List<String> pathTokens = List.of("pathTokens");
        saveF24Item.setFileKey(METADATA_SIMPLIFIED_FILEKEY);
        saveF24Item.setApplyCost(true);
        saveF24Item.setSha256(sha256);
        saveF24Item.setPathTokens(pathTokens);
        saveF24Request.setSetId(setId);
        saveF24Request.setF24Items(List.of(saveF24Item));
        Mono<SaveF24Request> monoSaveF24Request = Mono.just(saveF24Request);


        f24Service.saveMetadata("xPagopaF24CxId", setId, monoSaveF24Request).block();
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            F24MetadataSet f24MetadataSet = f24MetadataSetDaoMock.getItem(setId).block();
            Assertions.assertEquals(F24MetadataStatus.VALIDATION_ENDED, f24MetadataSet.getStatus());
        });
        F24MetadataSet f24MetadataSet = f24MetadataSetDaoMock.getItem(setId).block();

    }

    @Test
    public void saveMetadataTestHandleExistingMetadata() {

        String setId = "setId";
        SaveF24Request saveF24Request = new SaveF24Request();
        SaveF24Item saveF24Item = new SaveF24Item();

        List<String> pathTokens = List.of("pathTokens");
        saveF24Item.setFileKey("fileKey");
        saveF24Item.setApplyCost(true);
        saveF24Item.setSha256("sha256");
        saveF24Item.setPathTokens(pathTokens);
        saveF24Request.setSetId(setId);
        saveF24Request.setF24Items(List.of(saveF24Item));
        Mono<SaveF24Request> monoSaveF24Request = Mono.just(saveF24Request);

        f24MetadataSetDaoMock.putItemIfAbsent(TestUtils.createF24MetadataSetWithApplyCost(setId));

        StepVerifier.create(f24Service.saveMetadata("xPagopaF24CxId", setId, monoSaveF24Request)).expectNextMatches(f24Response -> {
            return f24Response.getStatus().equals(REQUEST_ACCEPTED_STATUS);
        }).expectComplete().verify();

    }
}
