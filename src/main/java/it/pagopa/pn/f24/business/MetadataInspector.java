package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

public interface MetadataInspector {
    int countMetadataApplyCost(F24Metadata f24Metadata);
}
