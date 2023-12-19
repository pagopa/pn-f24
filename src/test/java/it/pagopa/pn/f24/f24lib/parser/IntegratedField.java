package it.pagopa.pn.f24.f24lib.parser;

import lombok.Data;

@Data
public class IntegratedField {
    private String pdfFieldName;
    private String pdfFieldValue;
    private String metadataFieldName;
    private String metadataFieldValue;
    private PdfFieldFormatter pdfFieldFormatter;

    public IntegratedField(String pdfFieldName, String metadataFieldName, String metadataFieldValue) {
        this.pdfFieldName = pdfFieldName;
        this.metadataFieldName = metadataFieldName;
        this.metadataFieldValue = metadataFieldValue;
        this.pdfFieldFormatter = PdfFieldFormatter.NONE;
    }
    public IntegratedField(String pdfFieldName, String metadataFieldName, String metadataFieldValue, PdfFieldFormatter pdfFieldFormatter) {
        this.pdfFieldName = pdfFieldName;
        this.metadataFieldName = metadataFieldName;
        this.metadataFieldValue = metadataFieldValue;
        this.pdfFieldFormatter = pdfFieldFormatter;
    }

}
