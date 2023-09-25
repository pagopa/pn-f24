package it.pagopa.pn.f24.dto;

public enum F24FileStatus {
    TO_PROCESS("TO_PROCESS"),
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
