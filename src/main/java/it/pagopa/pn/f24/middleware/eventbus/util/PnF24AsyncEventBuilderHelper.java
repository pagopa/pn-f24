package it.pagopa.pn.f24.middleware.eventbus.util;

import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEventPayload;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PnF24AsyncEventBuilderHelper {
    private static final String OK_STATUS = "OK";
    private static final String KO_STATUS = "KO";
    private static final String METADATA_VALIDATION_EVENT_ID_DESCRIPTOR = "_f24_metadata_validation_";

    private static final String PDF_SET_READY_EVENT_ID_DESCRIPTOR = "_f24_pdf_set_ready_";

    public static PnF24AsyncEvent buildMetadataValidationEndEvent(String cxId, String setId, List<F24MetadataValidationIssue> errors) {
        return PnF24AsyncEvent.builder()
                .detail(
                        PnF24AsyncEvent.Detail.builder()
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

    public static PnF24AsyncEvent buildPdfSetReadyEvent(F24Request f24Request) {
        //All files in the list should share cxId, requestId and setId.
        return PnF24AsyncEvent.builder()
                .header(buildF24EventHeader(f24Request.getCxId(), f24Request.getSetId(), PDF_SET_READY_EVENT_ID_DESCRIPTOR, F24ExternalEventType.F24_PDF_READY))
                .payload(
                        PnF24AsyncEvent.Payload.builder()
                                .cxId(f24Request.getCxId())
                                .pdfSetReady(buildPdfSetReadyPayload(f24Request))
                                .build()
                )
                .build();
    }

    private static PnF24PdfSetReadyEventPayload buildPdfSetReadyPayload(F24Request f24Request) {
        return PnF24PdfSetReadyEventPayload.builder()
                .requestId(f24Request.getRequestId())
                .status(OK_STATUS)
                .generatedPdfsUrls(buildGeneratedPdfsUrls(f24Request.getFiles()))
                .build();
    }

    private static List<PnF24PdfSetReadyEventItem> buildGeneratedPdfsUrls(Map<String, F24Request.FileRef> f24Files) {
        return f24Files.entrySet().stream()
                .map(entry ->
                    PnF24PdfSetReadyEventItem.builder()
                            .pathTokens(entry.getKey())
                            .uri(entry.getValue().getFileKey())
                            .build()
                ).toList();
    }
}
