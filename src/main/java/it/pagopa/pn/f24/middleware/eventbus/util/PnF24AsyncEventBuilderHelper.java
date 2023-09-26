package it.pagopa.pn.f24.middleware.eventbus.util;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEventPayload;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;

import java.util.ArrayList;
import java.util.List;

public class PnF24AsyncEventBuilderHelper {
    private static final String OK_STATUS = "OK";
    private static final String KO_STATUS = "KO";
    public static PnF24MetadataValidationEndEvent buildMetadataValidationEndEvent(String cxId, String setId, List<F24MetadataValidationIssue> errors) {
        return PnF24MetadataValidationEndEvent.builder()
                .detail(
                        PnF24MetadataValidationEndEvent.Detail.builder()
                                .cxId(cxId)
                                .metadataValidationEnd(buildMetadataValidationEndPayload(setId, errors))
                                .build()
                )
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
