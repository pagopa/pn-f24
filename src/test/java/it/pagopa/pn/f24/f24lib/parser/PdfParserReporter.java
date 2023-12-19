package it.pagopa.pn.f24.f24lib.parser;

import it.pagopa.pn.f24.f24lib.util.LibTestException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PdfParserReporter {
    private final PdfParser parser;
    private final List<IntegratedField> integratedFields;
    public PdfParserReporter(PdfParser parser, List<IntegratedField> integratedFields) {
        this.parser = parser;
        this.integratedFields = integratedFields;
    }

    public void executeAnalysisAndProduceReport() {
        log.info("starting pdf analysis on {} fields", integratedFields.size());
        List<IntegratedField> fieldsWithError = new ArrayList<>();
        integratedFields.forEach(integratedField -> {
            String fieldPdfValue = parser.getFieldValue(integratedField.getPdfFieldName());
            String formattedFieldPdfValue = integratedField.getPdfFieldFormatter().format(fieldPdfValue);
            integratedField.setPdfFieldValue(formattedFieldPdfValue);
            if (!integratedField.getMetadataFieldValue().equals(integratedField.getPdfFieldValue())) {
                fieldsWithError.add(integratedField);
            }
        });

        produceReport(fieldsWithError);
    }

    public static void produceReport(List<IntegratedField> integratedField) {
        StringBuilder sb = new StringBuilder();
        integratedField.forEach(m -> {
            sb.append("Pdf field name: ").append(m.getPdfFieldName()).append("\n");
            sb.append("Pdf field value: ").append(m.getPdfFieldValue()).append("\n");
            sb.append("Metadata field name: ").append(m.getMetadataFieldName()).append("\n");
            sb.append("Metadata field value: ").append(m.getMetadataFieldValue()).append("\n");
            sb.append("\n");
        });

        if (!sb.isEmpty()) {
            String firstRowHeader = "Found inconsistent values parsing PDF:" + "\n";
            throw new LibTestException(firstRowHeader + sb);
        }
    }


}
