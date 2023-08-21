package it.pagopa.pn.f24.middleware.eventbus.impl;

import com.amazonaws.services.eventbridge.AmazonEventBridgeAsync;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.MetadataValidationEndEvent;
import it.pagopa.pn.f24.middleware.eventbus.MetadataValidationEventProducer;
import it.pagopa.pn.f24.middleware.eventbus.abstraction.AbstractEventProducer;

public class MetadataValidationEventProducerImpl extends AbstractEventProducer implements MetadataValidationEventProducer {
    protected MetadataValidationEventProducerImpl(AmazonEventBridgeAsync amazonEventBridge, F24Config f24Config) {
        super(amazonEventBridge, f24Config, f24Config.getEventBus().getMetadataValidationEvent().getDetailType(), f24Config.getEventBus().getName());
    }

    @Override
    public void sendMetadataValidationEvent() {

    }

    private MetadataValidationEndEvent buildMetadataValidationEndEvent() {
        return null;
    }
}
