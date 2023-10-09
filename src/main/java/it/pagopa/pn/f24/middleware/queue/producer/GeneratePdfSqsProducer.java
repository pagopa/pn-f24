package it.pagopa.pn.f24.middleware.queue.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.AbstractSqsMomProducer;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class GeneratePdfSqsProducer extends AbstractSqsMomProducer<GeneratePdfEvent> {
    protected GeneratePdfSqsProducer(SqsClient sqsClient, F24Config f24Config, ObjectMapper objectMapper) {
        super(sqsClient, f24Config.getInternalQueueName(), objectMapper, GeneratePdfEvent.class);
    }
}
