package it.pagopa.pn.f24.middleware.eventbus.impl;

import com.amazonaws.services.eventbridge.AmazonEventBridgeAsync;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.middleware.eventbus.AbstractEventBridgeProducer;
import org.springframework.stereotype.Component;

@Component
public class PnF24AsyncEventBridgeProducerImpl extends AbstractEventBridgeProducer<PnF24AsyncEvent> {
    protected PnF24AsyncEventBridgeProducerImpl(AmazonEventBridgeAsync amazonEventBridge, F24Config f24Config) {
        super(amazonEventBridge, f24Config.getEventBus().getSource(), f24Config.getEventBus().getOutcomeEventDetailType(), f24Config.getEventBus().getName());
    }
}
