package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;

import java.util.List;

public class PreparePdfSqsProducerMock implements MomProducer<PreparePdfEvent> {
    @Override
    public void push(List<PreparePdfEvent> list) {

    }
}
