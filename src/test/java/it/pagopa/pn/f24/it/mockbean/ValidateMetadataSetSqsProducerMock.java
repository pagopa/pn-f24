package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.it.util.ThreadPool;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;

@Slf4j
public class ValidateMetadataSetSqsProducerMock implements MomProducer<ValidateMetadataSetEvent> {

    private final F24MetadataSetDaoMock f24MetadataSetDaoMock;
    private final ValidateMetadataEventService validateMetadataEventService;

    public ValidateMetadataSetSqsProducerMock(F24MetadataSetDaoMock f24MetadataSetDaoMock, ValidateMetadataEventService validateMetadataEventService) {
        this.f24MetadataSetDaoMock = f24MetadataSetDaoMock;
        this.validateMetadataEventService = validateMetadataEventService;
    }

    @Override
    public void push(List<ValidateMetadataSetEvent> list) {
        String setId = list.get(0).getPayload().getSetId();
        log.info("[TEST] pushing setId={} to queue", setId);

        ThreadPool.start(new Thread(() -> {
            Assertions.assertDoesNotThrow(() -> {

                try {
                    await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
                        F24MetadataSet f24MetadataSet = f24MetadataSetDaoMock.getItem(setId).block();
                        log.info("[TEST] Start assertion for queue f24MetadataSet = {}", f24MetadataSet);
                        Assertions.assertNotNull(f24MetadataSet);
                    });
                    validateMetadataEventService.handleMetadataValidation(list.get(0).getPayload()).block();
                } catch (org.awaitility.core.ConditionTimeoutException ex) {
                    throw ex;
                }
            });
        }));

    }
}
