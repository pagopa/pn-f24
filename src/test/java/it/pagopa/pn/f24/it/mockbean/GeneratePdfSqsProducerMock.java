package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.it.util.ThreadPool;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.awaitility.Awaitility.await;

@Slf4j
public class GeneratePdfSqsProducerMock implements MomProducer<GeneratePdfEvent> {
    private final GeneratePdfEventService generatePdfEventService;
    private final F24FileCacheDaoMock f24FileCacheDaoMock;

    public GeneratePdfSqsProducerMock(GeneratePdfEventService generatePdfEventService, F24FileCacheDaoMock f24FileCacheDaoMock) {
        this.generatePdfEventService = generatePdfEventService;
        this.f24FileCacheDaoMock = f24FileCacheDaoMock;
    }
    @Override
    public void push(List<GeneratePdfEvent> events) {
        log.info("[TEST] pushing {} GeneratePdfEvents events={} to queue", events.size(), events);
        ThreadPool.start(new Thread(() -> {
            Assertions.assertDoesNotThrow(() -> {
                try {
                    events.forEach(event -> {
                        await().untilAsserted(() -> {
                            F24File f24File = f24FileCacheDaoMock.getItem(event.getPayload().getFilePk()).block();                            Assertions.assertNotNull(f24File);
                        });
                        generatePdfEventService.generatePdf(event.getPayload()).block();
                    });
                } catch (org.awaitility.core.ConditionTimeoutException ex) {
                    throw ex;
                }
            });
        }));

    }
}
