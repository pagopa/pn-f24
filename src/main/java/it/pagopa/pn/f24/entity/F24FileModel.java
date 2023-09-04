/*
package it.pagopa.pn.f24.entity;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;

import static it.pagopa.pn.f24.constant.F24FileConstant.*;

@Data
@ToString
@DynamoDbBean
public class F24FileModel {

    @Getter(onMethod = @__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(PK)
    }))
    private String pk;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(SK),
            @DynamoDbSortKey
    }))
    private String created;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_SK),
            @DynamoDbSecondaryPartitionKey(indexNames = GSI_S)
    }))
    private String sk;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_REQUEST_ID),
            @DynamoDbSecondaryPartitionKey(indexNames = GSI_R)
    }))
    private String requestId;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_STATUS)
    }))
    private String status;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_FILE_KEY),
            @DynamoDbSecondaryPartitionKey(indexNames = GSI_F)
    }))
    private String fileKey;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_TTL)
    }))
    private Long ttl;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_UPDATED)
    }))
    private LocalDateTime updated;

}
*/
