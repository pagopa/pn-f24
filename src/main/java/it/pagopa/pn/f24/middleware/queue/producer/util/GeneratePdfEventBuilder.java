package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24InternalEventType;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;

import java.time.Instant;
import java.util.UUID;

public class GeneratePdfEventBuilder {
    private GeneratePdfEventBuilder() { }
    private static final String GENERATE_PDF_EVENT_ID_DESCRIPTOR = "generate_pdf_";

    public static GeneratePdfEvent buildGeneratePdfEvent(PreparePdfLists.F24FileToCreate f24FileToCreate) {
        F24File f24File = f24FileToCreate.getFile();
        String metadataFileKey = f24FileToCreate.getMetadataFileKey();
        return GeneratePdfEvent.builder()
                .header(buildInternalEventHeader(f24File.getPk()))
                .payload(
                        GeneratePdfEvent.Payload.builder()
                                .filePk(f24File.getPk())
                                .setId(f24File.getSetId())
                                .metadataFileKey(metadataFileKey)
                                .build()
                )
                .build();
    }

    private static GenericEventHeader buildInternalEventHeader(String f24FilePk) {
        return GenericEventHeader.builder()
                .eventId(generateEventId(f24FilePk))
                .eventType(F24InternalEventType.GENERATE_PDF.getValue())
                .publisher(EventPublisher.F24.name())
                .createdAt(Instant.now())
                .build();
    }


    private static String generateEventId(String requestId){
        // il carattere # non è accettato come eventId
        String eventId = (GENERATE_PDF_EVENT_ID_DESCRIPTOR + UUID.randomUUID() +requestId).replaceAll("[^a-zA-Z0-9]","_");
        return eventId.substring(0,Math.min(79, eventId.length()));
    }
}
