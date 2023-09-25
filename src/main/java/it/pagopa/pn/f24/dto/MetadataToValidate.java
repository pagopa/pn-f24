package it.pagopa.pn.f24.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataToValidate {
    private byte[] metadataFile;
    private F24MetadataRef ref;
    private String pathTokensKey;
}
