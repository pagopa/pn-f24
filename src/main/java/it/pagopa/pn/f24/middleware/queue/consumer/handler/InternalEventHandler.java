package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@AllArgsConstructor
@CustomLog
public class InternalEventHandler {
    private final ValidateMetadataEventService validateMetadataEventService;


    @Bean
    public Consumer<Message<ValidateMetadataSetEvent.Payload>> pnF24ValidateMetadataEventInboundConsumer() {
        return message -> {
            log.debug("Handle validate metadata message with content {}", message);
            try {
                ValidateMetadataSetEvent.Payload payload = message.getPayload();
                HandleEventUtils.addSetIdToMdc(payload.getSetId());
                validateMetadataEventService.handleMetadataValidation(payload)
                        .block();
            } catch (Exception ex) {
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }
}
