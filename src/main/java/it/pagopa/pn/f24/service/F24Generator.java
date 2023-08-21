package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Item;

public interface F24Generator {
    void validate(F24Item metadata);

    byte[] generate(F24Item metadata);
}
