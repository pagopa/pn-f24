package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity;

import lombok.ToString;

@ToString
public enum F24MetadataSetStatusEntity {
    SAVED("SAVED"),

    TO_VALIDATE("TO_VALIDATE");

    private final String value;

    F24MetadataSetStatusEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
