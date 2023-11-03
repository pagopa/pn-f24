package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import reactor.core.publisher.Mono;

import java.util.List;

public class MetadataValidationEndedEventProducerMock implements EventBridgeProducer<PnF24MetadataValidationEndEvent> {
    @Override
    public Mono<Void> sendEvent(List events) {
        return null;
    }
}
