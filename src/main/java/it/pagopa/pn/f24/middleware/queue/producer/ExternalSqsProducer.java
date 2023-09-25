package it.pagopa.pn.f24.middleware.queue.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.AbstractSqsMomProducer;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.config.F24Config;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class ExternalSqsProducer extends AbstractSqsMomProducer<PnF24AsyncEvent> {
    protected ExternalSqsProducer(SqsClient sqsClient, F24Config f24Config, ObjectMapper objectMapper) {
        super(sqsClient, f24Config.getExternalQueueName(), objectMapper, PnF24AsyncEvent.class);
    }
}
