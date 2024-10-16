package it.pagopa.pn.f24.middleware.queue.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.AbstractSqsMomProducer;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import java.util.List;

@Component
@Slf4j
public class GeneratePdfSqsProducer extends AbstractSqsMomProducer<GeneratePdfEvent> {
    private static final int MAX_SIZE_EVENTS_BATCH = 10;

    protected GeneratePdfSqsProducer(SqsClient sqsClient, F24Config f24Config, ObjectMapper objectMapper) {
        super(sqsClient, f24Config.getInternalPdfGeneratorQueueName(), objectMapper, GeneratePdfEvent.class);
    }

    @Override
    public void push(List<GeneratePdfEvent> events) {
        if(events.size() > MAX_SIZE_EVENTS_BATCH) {
            this.multiBatchPush(events);
        } else {
            super.push(events);
        }
    }

    public void multiBatchPush(List<GeneratePdfEvent> events) {
        List<List<GeneratePdfEvent>> batches = Utility.splitListInBatches(events, 10);
        log.debug("Executing multiBatchPush with {} batches", batches.size());

        for (List<GeneratePdfEvent> batch : batches) {
            super.push(batch);
        }
    }
}
