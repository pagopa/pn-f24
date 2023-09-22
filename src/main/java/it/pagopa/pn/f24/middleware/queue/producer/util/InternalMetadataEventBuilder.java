package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;

import java.time.Instant;

public class InternalMetadataEventBuilder {
    private static final String EVENT_PUBLISHER = "pn-f24";
    private static final String VALIDATE_METADATA_EVENT_ID_DESCRIPTOR = "_validate_f24_metadata_";

    public static InternalMetadataEvent buildValidateMetadataEvent(String setId, String cxId) {
        return InternalMetadataEvent.builder()
                .header(buildInternalEventHeader(setId, cxId, VALIDATE_METADATA_EVENT_ID_DESCRIPTOR, F24InternalEventType.VALIDATE_METADATA))
                .payload(InternalMetadataEvent.Payload.builder()
                        .setId(setId)
                        .cxId(cxId)
                        .build())
                .build();
    }
    private static GenericEventHeader buildInternalEventHeader(String setId, String cxId, String descriptor, F24InternalEventType f24InternalEventType) {
        String eventId = cxId + descriptor + setId;
        return GenericEventHeader.builder()
                .eventId(eventId)
                .eventType(f24InternalEventType.getValue())
                .publisher(EVENT_PUBLISHER)
                .createdAt(Instant.now())
                .build();
    }
}
