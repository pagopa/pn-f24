package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import io.awspring.cloud.sqs.annotation.SqsListener;
import it.pagopa.pn.commons.exceptions.PnInternalException;
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

import static io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode.ON_SUCCESS;

@Component
@AllArgsConstructor
@CustomLog
public class InternalEventHandler extends AbstractConsumerMessage {
    public  static final String INVALID_EVENT_TYPE = "INVALID_EVENT_TYPE";

    private final ValidateMetadataEventService validateMetadataEventService;

    private final PreparePdfEventService preparePdfEventService;

    private final GeneratePdfEventService generatePdfEventService;

    private final JsonService jsonService;

    @SqsListener(value = "${pn.f24.internal-queue-name}", acknowledgementMode = ON_SUCCESS)
    void pnF24InternalEventRouter(@Payload String payload, @Headers MessageHeaders headers) {
        initTraceId(headers);
        F24InternalEventType eventTypeEnum = getEventTypeFromHeaders(headers);
        switch (eventTypeEnum) {
            case VALIDATE_METADATA -> {
                ValidateMetadataSetEvent.Payload validatePayload = jsonService.parse(payload, ValidateMetadataSetEvent.Payload.class);
                pnF24ValidateMetadataEventInboundConsumer().accept(MessageBuilder.withPayload(validatePayload).copyHeaders(headers).build());
            }
            case PREPARE_PDF -> {
                PreparePdfEvent.Payload preparePdfPayload = jsonService.parse(payload, PreparePdfEvent.Payload.class);
                pnF24PreparePdfEventInboundConsumer().accept(MessageBuilder.withPayload(preparePdfPayload).copyHeaders(headers).build());
            }
            default -> log.warn("The eventType '{}' is not handled by this router.", eventTypeEnum.getValue());
        }
    }

    private F24InternalEventType getEventTypeFromHeaders(MessageHeaders headers) {
        String eventType = (String) headers.get("eventType");
        try {
            return F24InternalEventType.valueOf(eventType);
        } catch (IllegalArgumentException | NullPointerException ex) {
            log.warn("Received message with invalid or missing eventType header: {}", eventType, ex);
            throw new PnInternalException(ex.getMessage(), INVALID_EVENT_TYPE);
        }
    }

    @SqsListener(
            value = "${pn.f24.internal-pdf-generator-queue-name}",
            acknowledgementMode = ON_SUCCESS,
            maxConcurrentMessages = "${pdf.generator.queue.concurrency}",
            maxMessagesPerPoll = "${pdf.generator.queue.concurrency}"
    )
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
