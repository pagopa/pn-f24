package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.*;

import it.pagopa.pn.f24.dto.F24ExternalEventType;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExternalEventBuilderHelper {
    private static final String OK_STATUS = "OK";
    private static final String KO_STATUS = "KO";
    private static final String METADATA_VALIDATION_EVENT_ID_DESCRIPTOR = "_f24_metadata_validation_";

    public static PnF24AsyncEvent buildMetadataValidationEndEvent(String cxId, String setId, List<F24MetadataValidationIssue> errors) {
        return PnF24AsyncEvent.builder()
            .header(buildF24EventHeader(cxId, setId, METADATA_VALIDATION_EVENT_ID_DESCRIPTOR, F24ExternalEventType.F24_VALIDATION_ENDED))
            .payload(
                PnF24AsyncEvent.Payload.builder()
                    .cxId(cxId)
                    .metadataValidationEnd(buildMetadataValidationEndPayload(setId, errors))
                    .build()
            )
            .build();
    }

    private static PnF24EventHeader buildF24EventHeader(String cxId, String setId, String descriptor, F24ExternalEventType f24ExternalEventType) {
        String eventId = cxId + descriptor + setId;
        return PnF24EventHeader.builder()
                .eventId(eventId)
                .eventType(f24ExternalEventType.getValue())
                .publisher(EventPublisher.F24.name())
                .cxId(cxId)
                .setId(setId)
                .createdAt(Instant.now())
                .build();
    }

    private static PnF24MetadataValidationEndEventPayload buildMetadataValidationEndPayload(String setId, List<F24MetadataValidationIssue> errors) {
        String status = errors != null && errors.size() != 0 ? OK_STATUS : KO_STATUS;

        return PnF24MetadataValidationEndEventPayload.builder()
                .setId(setId)
                .status(status)
                .errors(convertErrors(errors))
                .build();
    }

    public static List<PnF24MetadataValidationIssue> convertErrors(List<F24MetadataValidationIssue> errors) {
        if(errors == null) {
            return new ArrayList<>();
        }

        return errors.stream()
                .map(e -> PnF24MetadataValidationIssue.builder()
                        .detail(e.getDetail())
                        .code(e.getCode())
                        .element(e.getElement())
                        .build()
                )
                .toList();
    }
}
