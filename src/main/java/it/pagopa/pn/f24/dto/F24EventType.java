package it.pagopa.pn.f24.dto;

public enum F24EventType {
    VALIDATE_METADATA("VALIDATE_METADATA");
    private final String value;
    F24EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
