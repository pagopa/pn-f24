package it.pagopa.pn.f24.middleware.queue.consumer.handler;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
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

@ContextConfiguration(classes = {SafeStorageEventHandler.class})
@ExtendWith(SpringExtension.class)
class SafeStorageEventHandlerTest {
    private static final String F24_FILE_DOC_TYPE = "PN_F24";
    @MockBean
    private F24Config f24Config;
    @MockBean
    private SafeStorageEventService safeStorageEventService;
    @Autowired
    private SafeStorageEventHandler safeStorageEventHandler;


    @Test
    void testPnSafeStorageEventInboundConsumer() {
        when(f24Config.getSafeStorageF24DocType()).thenReturn(F24_FILE_DOC_TYPE);
        Message<FileDownloadResponse> message = getFileDownloadResponseMessage();

        when(safeStorageEventService.handleSafeStorageResponse(any())).thenReturn(Mono.empty());
        Consumer<Message<FileDownloadResponse>> consumer = safeStorageEventHandler.pnSafeStorageEventInboundConsumer();
        consumer.accept(message);


        verify(safeStorageEventService).handleSafeStorageResponse(any());
    }

    private Message<FileDownloadResponse> getFileDownloadResponseMessage() {
        return new Message<>() {
            @Override
            @NotNull
            public FileDownloadResponse getPayload() {
                FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
                fileDownloadResponse.setKey("key");
                fileDownloadResponse.setDocumentType(F24_FILE_DOC_TYPE);
                return fileDownloadResponse;
            }

            @Override
            @NotNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
    }
}