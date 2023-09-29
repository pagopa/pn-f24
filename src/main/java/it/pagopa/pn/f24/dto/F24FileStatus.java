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

    static public final F24FileStatus[] values = values();

    public F24FileStatus prev() {
        return values[(ordinal() - 1  + values.length) % values.length];
    }

    public F24FileStatus next() {
        return values[(ordinal() + 1) % values.length];
    }
}
