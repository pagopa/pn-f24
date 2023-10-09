package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.ToString;

@ToString
public enum F24FileStatusEntity {
    TO_PROCESS("TO_PROCESS"),
    GENERATED("GENERATED"),
    DONE("DONE");

    private final String value;

    F24FileStatusEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
