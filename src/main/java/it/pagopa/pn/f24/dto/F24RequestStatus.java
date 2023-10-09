package it.pagopa.pn.f24.dto;

public enum F24RequestStatus {
    TO_PROCESS("TO_PROCESS"),

    DONE("DONE");

    private final String value;

    F24RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
