package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class F24MetadataValidationEntity {
    private String code;
    private String element;
    private String detail;
}
