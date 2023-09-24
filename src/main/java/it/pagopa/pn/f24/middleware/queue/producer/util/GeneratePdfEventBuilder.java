package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;

import java.time.Instant;

public class GeneratePdfEventBuilder {
    private static final String GENERATE_PDF_EVENT_ID_DESCRIPTOR = "_generate_pdf";

    public static GeneratePdfEvent buildGeneratePdfEvent(F24File f24File) {
        return GeneratePdfEvent.builder()
                .header(buildInternalEventHeader(f24File.getPk(), GENERATE_PDF_EVENT_ID_DESCRIPTOR, F24InternalEventType.GENERATE_PDF))
                .payload(
                        GeneratePdfEvent.Payload.builder()
                                .filePk(f24File.getPk())
                                .cxId(f24File.getCxId())
                                .setId(f24File.getSetId())
                                .pathTokens(f24File.getPathTokens())
                                .build()
                )
                .build();
    }

    private static GenericEventHeader buildInternalEventHeader(String f24FilePk, String descriptor, F24InternalEventType f24InternalEventType) {
        String eventId = f24FilePk + descriptor;
        return GenericEventHeader.builder()
                .eventId(eventId)
                .eventType(f24InternalEventType.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }
}
