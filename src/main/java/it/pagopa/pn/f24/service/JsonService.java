package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface JsonService {
    F24Metadata parseMetadataFile(byte[] json);

    <T> String stringifyObject(T object);

    <T> Set<ConstraintViolation<T>> validate(T object);

}
