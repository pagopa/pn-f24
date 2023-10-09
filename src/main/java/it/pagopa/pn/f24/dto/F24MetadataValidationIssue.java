package it.pagopa.pn.f24.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class F24MetadataValidationIssue {
    private String code;
    private String element;
    private String detail;
}
