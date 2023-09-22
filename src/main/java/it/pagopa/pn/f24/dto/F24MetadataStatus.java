package it.pagopa.pn.f24.dto;

public enum F24MetadataStatus {
    VALIDATION_ENDED("VALIDATION_ENDED"),

    PROCESSING("PROCESSING"),

    TO_VALIDATE("TO_VALIDATE");

    private final String value;

    F24MetadataStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
