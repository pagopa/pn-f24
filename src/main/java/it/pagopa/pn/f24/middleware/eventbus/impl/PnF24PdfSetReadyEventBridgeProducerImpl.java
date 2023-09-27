package it.pagopa.pn.f24.middleware.eventbus.impl;

import com.amazonaws.services.eventbridge.AmazonEventBridgeAsync;
import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.middleware.eventbus.AbstractEventBridgeProducer;
import it.pagopa.pn.f24.service.JsonService;
import org.springframework.stereotype.Component;

@Component
public class PnF24PdfSetReadyEventBridgeProducerImpl extends AbstractEventBridgeProducer<PnF24PdfSetReadyEvent> {
    protected PnF24PdfSetReadyEventBridgeProducerImpl(AmazonEventBridgeAsync amazonEventBridge, F24Config f24Config, JsonService jsonService) {
        super(amazonEventBridge, f24Config.getEventBus().getSource(), f24Config.getEventBus().getOutcomeEventDetailType(), f24Config.getEventBus().getName(), jsonService);
    }
}
