package it.pagopa.pn.f24.middleware.eventbus.util;

import it.pagopa.pn.api.dto.events.*;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PnF24AsyncEventBuilderHelper {
    private PnF24AsyncEventBuilderHelper() { }
    private static final String OK_STATUS = "OK";
    private static final String KO_STATUS = "KO";
    public static PnF24MetadataValidationEndEvent buildMetadataValidationEndEvent(String cxId, String setId, List<F24MetadataValidationIssue> errors) {
        return PnF24MetadataValidationEndEvent.builder()
                .detail(
                        PnF24MetadataValidationEndEvent.Detail.builder()
                                .clientId(cxId)
                                .metadataValidationEnd(buildMetadataValidationEndPayload(setId, errors))
                                .build()
                )
                .build();
    }

    private static PnF24MetadataValidationEndEventPayload buildMetadataValidationEndPayload(String setId, List<F24MetadataValidationIssue> errors) {
        String status = errors != null && !errors.isEmpty() ? KO_STATUS : OK_STATUS;

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

    public static PnF24PdfSetReadyEvent buildPdfSetReadyEvent(F24Request f24Request) {
        //All files in the list should share cxId, requestId and setId.
        return PnF24PdfSetReadyEvent.builder()
                .detail(
                        PnF24PdfSetReadyEvent.Detail.builder()
                                .clientId(f24Request.getCxId())
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
                            .pathTokens(extractPathTokensFromFilePk(entry.getKey()))
                            .uri(entry.getValue().getFileKey())
                            .build()
                ).toList();
    }

    private static String extractPathTokensFromFilePk(String pk) {
         F24FileCacheEntity entity = new F24FileCacheEntity();
         entity.setPk(pk);
         return entity.getPathTokens();
    }
}
