package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.JsonService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {InternalEventHandler.class})
@ExtendWith(SpringExtension.class)
class InternalEventHandlerTest {
    @Autowired
    private InternalEventHandler internalEventHandler;

    @MockitoBean
    ValidateMetadataEventService validateMetadataEventService;

    @MockitoBean
    PreparePdfEventService preparePdfEventService;

    @MockitoBean
    GeneratePdfEventService generatePdfEventService;

    @MockitoBean
    JsonService jsonService;

    @Test
    void testPnF24ValidateMetadataEventInboundConsumer() {
        Message<ValidateMetadataSetEvent.Payload> message = getValidateMetadataSetMessage();

        when(validateMetadataEventService.handleMetadataValidation(any())).thenReturn(Mono.empty());
        Consumer<Message<ValidateMetadataSetEvent.Payload>> consumer = internalEventHandler.pnF24ValidateMetadataEventInboundConsumer();
        consumer.accept(message);


        verify(validateMetadataEventService).handleMetadataValidation(any());
    }

    @Test
    void testErrorPnF24ValidateMetadataEventInboundConsumer() {
        Message<ValidateMetadataSetEvent.Payload> message = getValidateMetadataSetMessage();

        when(validateMetadataEventService.handleMetadataValidation(any())).thenThrow(RuntimeException.class);
        Consumer<Message<ValidateMetadataSetEvent.Payload>> consumer = internalEventHandler.pnF24ValidateMetadataEventInboundConsumer();

        assertThrows(RuntimeException.class, () -> consumer.accept(message));
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
    @Test
    void testPnF24PreparePdfEventInboundConsumer() {
        Message<PreparePdfEvent.Payload> message = getPreparePdfMessage();

        when(preparePdfEventService.preparePdf(any())).thenReturn(Mono.empty());
        Consumer<Message<PreparePdfEvent.Payload>> consumer = internalEventHandler.pnF24PreparePdfEventInboundConsumer();
        consumer.accept(message);


        verify(preparePdfEventService).preparePdf(any());
    }

    @Test
    void testErrorPnF24PreparePdfEventInboundConsumer() {
        Message<PreparePdfEvent.Payload> message = getPreparePdfMessage();

        when(preparePdfEventService.preparePdf(any())).thenThrow(RuntimeException.class);
        Consumer<Message<PreparePdfEvent.Payload>> consumer = internalEventHandler.pnF24PreparePdfEventInboundConsumer();

        assertThrows(RuntimeException.class, () -> consumer.accept(message));
    }

    private Message<PreparePdfEvent.Payload> getPreparePdfMessage() {
        return new Message<>() {
            @Override
            @NotNull
            public PreparePdfEvent.Payload getPayload() {
                return PreparePdfEvent.Payload.builder()
                        .requestId("requestId")
                        .build();
            }

            @Override
            @NotNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
    }

    @Test
    void testPnF24GeneratePdfEventInboundConsumer() {
        Message<GeneratePdfEvent.Payload> message = getGeneratePdfMessage();

        when(generatePdfEventService.generatePdf(any())).thenReturn(Mono.empty());
        Consumer<Message<GeneratePdfEvent.Payload>> consumer = internalEventHandler.pnF24GeneratePdfEventInboundConsumer();
        consumer.accept(message);


        verify(generatePdfEventService).generatePdf(any());
    }

    @Test
    void testErrorPnF24GeneratePdfEventInboundConsumer() {
        Message<GeneratePdfEvent.Payload> message = getGeneratePdfMessage();

        when(generatePdfEventService.generatePdf(any())).thenThrow(RuntimeException.class);
        Consumer<Message<GeneratePdfEvent.Payload>> consumer = internalEventHandler.pnF24GeneratePdfEventInboundConsumer();

        assertThrows(RuntimeException.class, () -> consumer.accept(message));
    }

    private Message<GeneratePdfEvent.Payload> getGeneratePdfMessage() {
        return new Message<>() {
            @Override
            @NotNull
            public GeneratePdfEvent.Payload getPayload() {
                return GeneratePdfEvent.Payload.builder()
                        .metadataFileKey("metadataFileKey")
                        .setId("setId")
                        .filePk("CACHE#setId#NO_COST#0_0")
                        .build();
            }

            @Override
            @NotNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
    }

    @Test
    void testPnF24InternalEventRouter_ValidateMetadata() {
        String payload = "{\"setId\":\"testSetId\"}";
        HashMap<String, Object> headersMap = new HashMap<>();
        headersMap.put("eventType", "VALIDATE_METADATA");
        MessageHeaders headers = new MessageHeaders(headersMap);

        when(jsonService.parse(payload, ValidateMetadataSetEvent.Payload.class))
                .thenReturn(ValidateMetadataSetEvent.Payload.builder().setId("testSetId").build());
        when(validateMetadataEventService.handleMetadataValidation(any())).thenReturn(Mono.empty());

        internalEventHandler.pnF24InternalEventRouter(payload, headers);
        verify(validateMetadataEventService).handleMetadataValidation(any());
    }

    @Test
    void testPnF24InternalEventRouter_PreparePdf() {
        String payload = "{\"requestId\":\"testRequestId\"}";
        HashMap<String, Object> headersMap = new HashMap<>();
        headersMap.put("eventType", "PREPARE_PDF");
        MessageHeaders headers = new MessageHeaders(headersMap);

        when(jsonService.parse(payload, PreparePdfEvent.Payload.class))
                .thenReturn(PreparePdfEvent.Payload.builder().requestId("testRequestId").build());
        when(preparePdfEventService.preparePdf(any())).thenReturn(Mono.empty());

        internalEventHandler.pnF24InternalEventRouter(payload, headers);
        verify(preparePdfEventService).preparePdf(any());
    }

    @Test
    void testPnF24PdfGeneratePdfEventListener() {
        Message<GeneratePdfEvent.Payload> message = getGeneratePdfMessage();

        when(generatePdfEventService.generatePdf(any())).thenReturn(Mono.empty());
        internalEventHandler.pnF24GeneratePdfEventListener(message);

        verify(generatePdfEventService).generatePdf(any());
    }
}