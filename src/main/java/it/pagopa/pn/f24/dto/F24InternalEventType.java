package it.pagopa.pn.f24.dto;

public enum F24InternalEventType {
    VALIDATE_METADATA("VALIDATE_METADATA"),
    PREPARE_PDF("PREPARE_PDF"),
    GENERATE_PDF("GENERATE_PDF");
    private final String value;
    F24InternalEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
