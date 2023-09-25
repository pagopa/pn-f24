package it.pagopa.pn.f24.dto;

import lombok.ToString;

@ToString
public enum F24FileStatus {
    PROCESSING("PROCESSING"),
    GENERATED("GENERATED"),
    DONE("DONE");

    private final String value;

    F24FileStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
