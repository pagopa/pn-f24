package it.pagopa.pn.f24.dto;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataToValidate {
    private byte[] metadataFile;
    private F24Metadata f24Metadata;
    private F24MetadataRef ref;
    private String pathTokensKey;
}
