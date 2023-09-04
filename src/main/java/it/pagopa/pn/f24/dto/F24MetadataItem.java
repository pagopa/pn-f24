package it.pagopa.pn.f24.dto;

import lombok.Data;

@Data
public class F24MetadataItem {
    private String fileKey;
    private String sha256;
    private boolean applyCost;
}
