package it.pagopa.pn.f24.middleware.queue.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.AbstractSqsMomProducer;
import it.pagopa.pn.f24.config.F24Config;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class InternalSqsProducer extends AbstractSqsMomProducer<InternalMetadataEvent> {
    protected InternalSqsProducer(SqsClient sqsClient, F24Config f24Config, ObjectMapper objectMapper) {
        super(sqsClient, f24Config.getInternalQueue().getName(), objectMapper, InternalMetadataEvent.class);
    }
}
