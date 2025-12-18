package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import io.awspring.cloud.sqs.annotation.SqsListener;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.AbstractConsumerMessage;
import it.pagopa.pn.f24.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode.ALWAYS;

@Component
@Slf4j
public class SafeStorageEventHandler extends AbstractConsumerMessage {

    private final F24Config f24Config;

    private final SafeStorageEventService safeStorageEventService;

    public SafeStorageEventHandler(F24Config f24Config, SafeStorageEventService safeStorageEventService) {
        this.f24Config = f24Config;
        this.safeStorageEventService = safeStorageEventService;
    }

    @SqsListener(value = "${pn.f24.safe-storage-queue-name}", acknowledgementMode = ALWAYS)
    void pnSafeStorageEventListener(Message<FileDownloadResponse> message) {
        initTraceId(message.getHeaders());
        pnSafeStorageEventInboundConsumer().accept(message);
    }


    protected Consumer<Message<FileDownloadResponse>> pnSafeStorageEventInboundConsumer() {
        return message -> {
            try {
                log.debug("Handle message from {} with content {}", PnSafeStorageClient.CLIENT_NAME, message);
                FileDownloadResponse response = message.getPayload();
                MDC.put(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY, response.getKey());

                if(f24Config.getSafeStorageF24DocType().equals(response.getDocumentType())) {
                    MDCUtils.addMDCToContextAndExecute(
                            safeStorageEventService.handleSafeStorageResponse(response)
                    ).block();
                } else {
                    log.debug("Safe storage event received is not handled - documentType={}", response.getDocumentType());
                }

                MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY);
            } catch (Exception ex) {
                MDC.remove(MDCUtils.MDC_PN_CTX_SAFESTORAGE_FILEKEY);
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }
}
