package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;

import java.time.Instant;

public class InternalMetadataEventBuilder {
    private InternalMetadataEventBuilder() { }
    private static final String VALIDATE_METADATA_EVENT_ID_DESCRIPTOR = "validate_f24_metadata_";

    public static ValidateMetadataSetEvent buildValidateMetadataEvent(String setId) {
        return ValidateMetadataSetEvent.builder()
                .header(buildInternalEventHeader(setId))
                .payload(ValidateMetadataSetEvent.Payload.builder()
                        .setId(setId)
                        .build())
                .build();
    }
    private static GenericEventHeader buildInternalEventHeader(String setId) {
        String eventId = VALIDATE_METADATA_EVENT_ID_DESCRIPTOR + setId;
        return GenericEventHeader.builder()
                .eventId(eventId)
                .eventType(F24InternalEventType.VALIDATE_METADATA.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }
}
