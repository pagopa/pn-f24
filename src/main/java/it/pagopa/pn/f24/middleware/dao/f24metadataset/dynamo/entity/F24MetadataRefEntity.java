package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
@DynamoDbBean
public class F24MetadataRefEntity {
    private static final String FIELD_FILE_KEY = "fileKey";
    private static final String FIELD_SHA256 = "sha256";
    private static final String FIELD_APPLY_COST = "applyCost";
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_FILE_KEY)}))
    private String fileKey;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_SHA256)}))
    private String sha256;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_APPLY_COST)}))
    private boolean applyCost;
}
