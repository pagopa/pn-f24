package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;

import java.time.Instant;

public class PreparePdfEventBuilder {
    private PreparePdfEventBuilder() { }
    private static final String PREPARE_PDF_EVENT_ID_DESCRIPTOR = "prepare_pdf_request_";

    public static PreparePdfEvent buildPreparePdfEvent(String requestId) {
        return PreparePdfEvent.builder()
                .header(buildInternalEventHeader(requestId))
                .payload(PreparePdfEvent.Payload.builder()
                        .requestId(requestId)
                        .build())
                .build();
    }

    private static GenericEventHeader buildInternalEventHeader(String requestId) {
        String eventId = PREPARE_PDF_EVENT_ID_DESCRIPTOR + requestId;
        return GenericEventHeader.builder()
                .eventId(eventId)
                .eventType(F24InternalEventType.PREPARE_PDF.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }
}
