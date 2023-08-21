package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity;

import lombok.ToString;

@ToString
public enum F24MetadataStatusEntity {
    SAVED("SAVED"),

    TO_VALIDATE("TO_VALIDATE");

    private final String value;

    F24MetadataStatusEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
