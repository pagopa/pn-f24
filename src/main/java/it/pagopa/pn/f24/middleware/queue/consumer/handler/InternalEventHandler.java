package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import io.awspring.cloud.sqs.annotation.SqsListener;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.consumer.AbstractConsumerMessage;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import it.pagopa.pn.f24.service.JsonService;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode.ALWAYS;

@Component
@AllArgsConstructor
@CustomLog
public class InternalEventHandler extends AbstractConsumerMessage {
    private final ValidateMetadataEventService validateMetadataEventService;

    private final PreparePdfEventService preparePdfEventService;

    private final GeneratePdfEventService generatePdfEventService;

    private final JsonService jsonService;

    @SqsListener(value = "${pn.f24.internal-queue-name}", acknowledgementMode = ALWAYS)
    void pnF24InternalEventRouter(@Payload String payload, @Headers MessageHeaders headers) {
        String eventType = (String) headers.get("eventType");
        switch (F24InternalEventType.valueOf(eventType)) {
            case VALIDATE_METADATA -> {
                ValidateMetadataSetEvent.Payload validatePayload = jsonService.parse(payload, ValidateMetadataSetEvent.Payload.class);
                pnF24ValidateMetadataEventInboundConsumer().accept(MessageBuilder.withPayload(validatePayload).copyHeaders(headers).build());
            }
            case PREPARE_PDF -> {
                PreparePdfEvent.Payload preparePdfPayload = jsonService.parse(payload, PreparePdfEvent.Payload.class);
                pnF24PreparePdfEventInboundConsumer().accept(MessageBuilder.withPayload(preparePdfPayload).copyHeaders(headers).build());
            }
            default -> log.warn("Received message with unknown eventType: {}", eventType);
        }
    }

    @SqsListener(value = "${pn.f24.internal-pdf-generator-queue-name}", acknowledgementMode = ALWAYS)
    void pnF24GeneratePdfEventListener(Message<GeneratePdfEvent.Payload> message) {
        initTraceId(message.getHeaders());
        pnF24GeneratePdfEventInboundConsumer().accept(message);
    }

    protected Consumer<Message<ValidateMetadataSetEvent.Payload>> pnF24ValidateMetadataEventInboundConsumer() {
        return message -> {
            log.debug("Handle validate metadata message with content {}", message);
            try {
                ValidateMetadataSetEvent.Payload payload = message.getPayload();
                HandleEventUtils.addSetIdToMdc(payload.getSetId());
                MDCUtils.addMDCToContextAndExecute(
                        validateMetadataEventService.handleMetadataValidation(payload)
                ).block();
            } catch (Exception ex) {
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }

    protected Consumer<Message<PreparePdfEvent.Payload>> pnF24PreparePdfEventInboundConsumer() {
        return message -> {
            log.debug("Prepare pdf for message with content {}", message);
            try {
                PreparePdfEvent.Payload payload = message.getPayload();
                HandleEventUtils.addRequestIdToMdc(payload.getRequestId());
                MDCUtils.addMDCToContextAndExecute(
                    preparePdfEventService.preparePdf(payload)
                ).block();
            } catch (Exception ex) {
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }

    protected Consumer<Message<GeneratePdfEvent.Payload>> pnF24GeneratePdfEventInboundConsumer() {
        return message -> {
            log.debug("Generate pdf for message with content {}", message);
            try {
                GeneratePdfEvent.Payload payload = message.getPayload();
                HandleEventUtils.addSetIdToMdc(payload.getSetId());
                MDCUtils.addMDCToContextAndExecute(
                    generatePdfEventService.generatePdf(payload)
                ).block();
            } catch (Exception ex) {
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }
}
