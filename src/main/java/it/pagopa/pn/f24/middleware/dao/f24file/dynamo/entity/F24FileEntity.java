package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDbBean
public class F24FileEntity {
    public static final String COL_PK = "pk";

    public static final String COL_SK = "sk";

    public static final String COL_FILE_KEY = "fileKey";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATED = "created";

    public static final String COL_REQUEST_ID = "requestId";

    public static final String COL_TTL = "ttl";

    public static final String COL_UPDATED = "updated";


    public static final String SK_PK_GSI = "sk-pk-gsi";

    public static final String GSI_R = "requestId-index";

    public static final String GSI_F = "fileKey-index";


    @Getter(onMethod = @__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(COL_PK)
    }))
    private String pk;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_CREATED),
            @DynamoDbSortKey
    }))
    private String created;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_SK),
            @DynamoDbSecondaryPartitionKey(indexNames = SK_PK_GSI)
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
