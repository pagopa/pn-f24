package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GeneratePdfSqsProducerMock implements MomProducer<GeneratePdfEvent> {
    private final GeneratePdfEventService generatePdfEventService;

    public GeneratePdfSqsProducerMock(GeneratePdfEventService generatePdfEventService) {
        this.generatePdfEventService = generatePdfEventService;
    }
    @Override
    public void push(List<GeneratePdfEvent> events) {
        log.info("[TEST] pushing {} GeneratePdfEvents events={} to queue", events.size(), events);
        events.forEach(event -> generatePdfEventService.generatePdf(event.getPayload()).block());
    }
}
