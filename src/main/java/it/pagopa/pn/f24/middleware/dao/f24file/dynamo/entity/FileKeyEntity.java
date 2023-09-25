package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDbBean
public class FileKeyEntity {
    public String fileKey;
}
