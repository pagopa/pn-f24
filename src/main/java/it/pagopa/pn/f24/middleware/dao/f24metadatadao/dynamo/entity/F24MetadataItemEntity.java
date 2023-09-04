package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class F24MetadataItemEntity {
    private String fileKey;
    private String sha256;
    private boolean applyCost;
}
