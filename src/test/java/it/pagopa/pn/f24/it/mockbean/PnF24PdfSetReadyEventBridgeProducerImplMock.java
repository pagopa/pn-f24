package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import reactor.core.publisher.Mono;

import java.util.List;

public class PnF24PdfSetReadyEventBridgeProducerImplMock implements EventBridgeProducer<PnF24PdfSetReadyEvent> {
    @Override
    public Mono<Void> sendEvent(List<PnF24PdfSetReadyEvent> events) {
        return null;
    }
}
