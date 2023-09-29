package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {InternalEventHandler.class})
@ExtendWith(SpringExtension.class)
class InternalEventHandlerTest {
    @Autowired
    private InternalEventHandler internalEventHandler;

    @MockBean
    ValidateMetadataEventService validateMetadataEventService;

    @Test
    void testPnF24ValidateMetadataEventInboundConsumer() {
        Message<ValidateMetadataSetEvent.Payload> message = getValidateMetadataSetMessage();

        when(validateMetadataEventService.handleMetadataValidation(any())).thenReturn(Mono.empty());
        Consumer<Message<ValidateMetadataSetEvent.Payload>> consumer = internalEventHandler.pnF24ValidateMetadataEventInboundConsumer();
        consumer.accept(message);


        verify(validateMetadataEventService).handleMetadataValidation(any());
    }

    private Message<ValidateMetadataSetEvent.Payload> getValidateMetadataSetMessage() {
        return new Message<>() {
            @Override
            @NotNull
            public ValidateMetadataSetEvent.Payload getPayload() {
                return ValidateMetadataSetEvent.Payload.builder()
                        .setId("setId")
                        .build();
            }

            @Override
            @NotNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
    }
}