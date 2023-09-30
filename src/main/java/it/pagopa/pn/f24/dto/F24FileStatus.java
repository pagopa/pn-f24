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

    private static final F24FileStatus[] statuses = values();

    public F24FileStatus prev() {
        return statuses[(ordinal() - 1  + statuses.length) % statuses.length];
    }

    public F24FileStatus next() {
        return statuses[(ordinal() + 1) % statuses.length];
    }
}
