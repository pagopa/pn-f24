package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;

import java.util.List;

public class GeneratePdfSqsProducerMock implements MomProducer<GeneratePdfEvent> {
    @Override
    public void push(List list) {

    }
}
