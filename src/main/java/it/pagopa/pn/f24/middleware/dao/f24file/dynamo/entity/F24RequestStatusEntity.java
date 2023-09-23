package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.ToString;

@ToString
public enum F24RequestStatusEntity {
    TO_PROCESS("TO_PROCESS"),

    PROCESSING("PROCESSING"),

    DONE("DONE");

    private final String value;

    F24RequestStatusEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
