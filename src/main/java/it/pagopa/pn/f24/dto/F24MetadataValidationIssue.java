package it.pagopa.pn.f24.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class F24MetadataValidationIssue {
    private String code;
    private String element;
    private String detail;
}
