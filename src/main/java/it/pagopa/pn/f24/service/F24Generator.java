package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

public interface F24Generator {
    byte[] generate(F24Metadata metadata);
}
