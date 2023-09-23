package it.pagopa.pn.f24.dto;

public enum F24ExternalEventType {
    F24_VALIDATION_ENDED("F24_VALIDATION_ENDED"),
    F24_PDF_READY("F24_PDF_READY");
    private final String value;
    F24ExternalEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
