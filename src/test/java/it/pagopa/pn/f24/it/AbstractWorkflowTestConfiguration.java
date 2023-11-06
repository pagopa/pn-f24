package it.pagopa.pn.f24.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import org.springframework.context.annotation.Bean;

public class AbstractWorkflowTestConfiguration {



    public AbstractWorkflowTestConfiguration() {
    }

    @Bean
    public ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder().configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public PnSafeStorageClient pnSafeStorageClient(SafeStorageEventService safeStorageEventService, F24FileCacheDaoMock f24FileCacheDaoMock) {
        return new PnSafeStorageClientMock(safeStorageEventService, f24FileCacheDaoMock);
    }

    @Bean
    public MomProducer<ValidateMetadataSetEvent> validateMetadataSetSqsProducerMock(F24MetadataSetDaoMock f24MetadataSetDaoMock, ValidateMetadataEventService validateMetadataEventService) {
        return new ValidateMetadataSetSqsProducerMock(f24MetadataSetDaoMock, validateMetadataEventService);
    }

    @Bean
    public MomProducer<PreparePdfEvent> preparePdfEventMomProducer(F24FileRequestDaoMock f24FileRequestDaoMock, PreparePdfEventService preparePdfEventService) {
        return new PreparePdfSqsProducerMock(f24FileRequestDaoMock, preparePdfEventService);
    }

    @Bean
    public MomProducer<GeneratePdfEvent> generatePdfEventMomProducer(GeneratePdfEventService generatePdfEventService) {
        return new GeneratePdfSqsProducerMock(generatePdfEventService);
    }

    @Bean
    public F24FileRequestDao f24FileRequestDao(F24FileCacheDaoMock f24FileCacheDaoMock) {
        return new F24FileRequestDaoMock(f24FileCacheDaoMock);
    }

}
