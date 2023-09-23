/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.pagopa.pn.f24.middleware.queue.consumer;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.f24.config.F24Config;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_CODE_F24_EVENTTYPENOTSUPPORTED;

@Configuration
@Slf4j
public class PnEventInboundService {
    private final EventHandler eventHandler;
    private final String safeStorageEventQueueName;
    
    public PnEventInboundService(EventHandler eventHandler, F24Config cfg) {
        this.eventHandler = eventHandler;
        this.safeStorageEventQueueName = cfg.getSafeStorageQueueName();
    }

    @Bean
    public MessageRoutingCallback customRouter() {
        return new MessageRoutingCallback() {
            @Override
            public FunctionRoutingResult routingResult(Message<?> message) {
                setMdc(message);
                return new FunctionRoutingResult(handleMessage(message));
            }
        };
    }

    private void setMdc(Message<?> message) {
        MessageHeaders messageHeaders = message.getHeaders();
        MDCUtils.clearMDCKeys();
        
        if (messageHeaders.containsKey("aws_messageId")){
            String awsMessageId = messageHeaders.get("aws_messageId", String.class);
            MDC.put(MDCUtils.MDC_PN_CTX_MESSAGE_ID, awsMessageId);
        }
        
        if (messageHeaders.containsKey("X-Amzn-Trace-Id")){
            String traceId = messageHeaders.get("X-Amzn-Trace-Id", String.class);
            MDC.put(MDCUtils.MDC_TRACE_ID_KEY, traceId);
        } else {
            MDC.put(MDCUtils.MDC_TRACE_ID_KEY, String.valueOf(UUID.randomUUID()));
        }

        String iun = (String) message.getHeaders().get("iun");
        if(iun != null){
            MDC.put(MDCUtils.MDC_PN_IUN_KEY, iun);
        }
    }

    private String handleMessage(Message<?> message) {
        log.debug("Received message from customRouter with header={}", message.getHeaders());

        String eventType = (String) message.getHeaders().get("eventType");
        log.debug("message has eventType={}", eventType);

        if (!StringUtils.hasText(eventType)) {
            log.debug("message has not an eventType, search other types");
            eventType = handleOtherEvent(message);
        }
        String handlerName = eventHandler.getHandler().get(eventType);
        if (!StringUtils.hasText(handlerName)) {
            log.error("undefined handler for eventType={}", eventType);
        }

        log.debug("Handler for eventType={} is {}", eventType, handlerName);

        return handlerName;
    }

    @NotNull
    private String handleOtherEvent(Message<?> message) {
        String eventType;
        String queueName = (String) message.getHeaders().get("aws_receivedQueue");
        if (Objects.equals(queueName, safeStorageEventQueueName)) {
            eventType = "SAFE_STORAGE_EVENTS";
        } else {
            log.error("eventType not present, cannot start scheduled action headers={} payload={}", message.getHeaders(), message.getPayload());
            throw new PnInternalException("eventType not present, cannot start scheduled action", ERROR_CODE_F24_EVENTTYPENOTSUPPORTED);
        }
        return eventType;
    }


}
