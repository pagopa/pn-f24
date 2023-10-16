package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;

import java.time.Instant;
import java.util.UUID;

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

        return GenericEventHeader.builder()
                .eventId(generateEventId(setId))
                .eventType(F24InternalEventType.VALIDATE_METADATA.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }

    private static String generateEventId(String requestId){
        String eventId = (VALIDATE_METADATA_EVENT_ID_DESCRIPTOR + UUID.randomUUID() + requestId).replaceAll("[^a-zA-Z0-9]","_");
        return eventId.substring(0,Math.min(79, eventId.length()));
    }
}
