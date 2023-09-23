package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;

import java.time.Instant;

public class InternalMetadataEventBuilder {
    private static final String VALIDATE_METADATA_EVENT_ID_DESCRIPTOR = "_validate_f24_metadata_";

    public static ValidateMetadataSetEvent buildValidateMetadataEvent(String setId, String cxId) {
        return ValidateMetadataSetEvent.builder()
                .header(buildInternalEventHeader(setId, cxId, VALIDATE_METADATA_EVENT_ID_DESCRIPTOR, F24InternalEventType.VALIDATE_METADATA))
                .payload(ValidateMetadataSetEvent.Payload.builder()
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
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }
}
