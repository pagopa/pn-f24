package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;

import java.time.Instant;
import java.util.UUID;

public class PreparePdfEventBuilder {
    private PreparePdfEventBuilder() { }
    private static final String PREPARE_PDF_EVENT_ID_DESCRIPTOR = "prepare_pdf_";

    public static PreparePdfEvent buildPreparePdfEvent(String requestId) {
        return PreparePdfEvent.builder()
                .header(buildInternalEventHeader(requestId))
                .payload(PreparePdfEvent.Payload.builder()
                        .requestId(requestId)
                        .build())
                .build();
    }

    private static GenericEventHeader buildInternalEventHeader(String requestId) {
        return GenericEventHeader.builder()
                .eventId(generateEventId(requestId))
                .eventType(F24InternalEventType.PREPARE_PDF.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }

    private static String generateEventId(String requestId){
        String eventId = (PREPARE_PDF_EVENT_ID_DESCRIPTOR + UUID.randomUUID() + requestId.replace("PREPARE_ANALOG_DOMICILE.IUN_","")).replaceAll("[^a-zA-Z0-9]","_");
        return eventId.substring(0,Math.min(79, eventId.length()));
    }
}
