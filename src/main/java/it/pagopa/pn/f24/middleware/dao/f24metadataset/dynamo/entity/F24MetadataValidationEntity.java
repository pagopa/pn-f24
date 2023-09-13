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
public class F24MetadataValidationEntity {
    private static final String FIELD_CODE = "code";
    private static final String FIELD_ELEMENT = "element";
    private static final String FIELD_DETAIL = "detail";
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_CODE)}))
    private String code;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_ELEMENT)}))
    private String element;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_DETAIL)}))
    private String detail;
}
