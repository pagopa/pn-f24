package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.f24.middleware.queue.consumer.service.GeneratePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.PreparePdfEventService;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
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

    private final PreparePdfEventService preparePdfEventService;

    private final GeneratePdfEventService generatePdfEventService;


    @Bean
    public Consumer<Message<ValidateMetadataSetEvent.Payload>> pnF24ValidateMetadataEventInboundConsumer() {
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

    @Bean
    public Consumer<Message<PreparePdfEvent.Payload>> pnF24PreparePdfEventInboundConsumer() {
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

    @Bean
    public Consumer<Message<GeneratePdfEvent.Payload>> pnF24GeneratePdfEventInboundConsumer() {
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
