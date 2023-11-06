package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.it.util.ThreadPool;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.List;


@Slf4j
public class PreparePdfSqsProducerMock implements MomProducer<PreparePdfEvent> {
    private final F24FileRequestDaoMock f24FileRequestDaoMock;
    private final PreparePdfEventService preparePdfEventService;

    public PreparePdfSqsProducerMock(F24FileRequestDaoMock f24FileRequestDaoMock, PreparePdfEventService preparePdfEventService) {
        this.f24FileRequestDaoMock = f24FileRequestDaoMock;
        this.preparePdfEventService = preparePdfEventService;
    }
    @Override
    public void push(List<PreparePdfEvent> list) {
        PreparePdfEvent.Payload payload = list.get(0).getPayload();
        log.info("[TEST] Start pushing PreparePdfEvent with requestId={} to queue", payload.getRequestId());

        ThreadPool.start(new Thread(() -> {
            Assertions.assertDoesNotThrow(() -> {
                try {
                    preparePdfEventService.preparePdf(payload).block();
                } catch (org.awaitility.core.ConditionTimeoutException ex) {
                    log.info("[TEST] Assertion ConditionTimeoutException for queue PreparePdfSqs");
                    throw ex;
                } catch (Exception e) {
                    log.warn("[TEST] Push event exception for queue PreparePdfSqs", e);
                }
            });
        }));
    }
}
