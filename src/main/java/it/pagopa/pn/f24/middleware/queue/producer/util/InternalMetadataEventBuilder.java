package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24EventType;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Item;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;

import java.time.Instant;
import java.util.List;

public class InternalMetadataEventBuilder {
    private static final String EVENT_PUBLISHER = "pn-f24";

    private static final String SAVE_METADATA_EVENT_ID_DESCRIPTOR = "_save_f24_metadata_";
    private static final String VALIDATE_METADATA_EVENT_ID_DESCRIPTOR = "_validate_f24_metadata_";

    public static InternalMetadataEvent buildValidateMetadataEvent(String setId, String cxId) {
        return InternalMetadataEvent.builder()
                .header(buildInternalEventHeader(setId, cxId, VALIDATE_METADATA_EVENT_ID_DESCRIPTOR, F24EventType.VALIDATE_METADATA))
                .payload(InternalMetadataEvent.Payload.builder()
                        .setId(setId)
                        .cxId(cxId)
                        .build())
                .build();
    }

    public static InternalMetadataEvent buildSaveMetadataEvent(String setId, String cxId, List<F24Item> f24Items) {
        return InternalMetadataEvent.builder()
                .header(buildInternalEventHeader(setId, cxId, SAVE_METADATA_EVENT_ID_DESCRIPTOR, F24EventType.SAVE_METADATA))
                .payload(InternalMetadataEvent.Payload.builder()
                        .setId(setId)
                        .cxId(cxId)
                        .f24Item(f24Items)
                        .build())
                .build();
    }
    private static GenericEventHeader buildInternalEventHeader(String setId, String cxId, String descriptor, F24EventType f24EventType) {
        String eventId = cxId + descriptor + setId;
        return GenericEventHeader.builder()
                .eventId(eventId)
                .eventType(f24EventType.getValue())
                .publisher(EVENT_PUBLISHER)
                .createdAt(Instant.now())
                .build();
    }
}
