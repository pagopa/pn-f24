package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity;

import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper.F24MetadataStatusEntityConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDbBean
public class F24MetadataEntity {
    public static final String FIELD_SET_ID = "setId";

    public static final String FIELD_CX_ID = "cxId";

    public static final String FIELD_PATH_TOKENS = "pathTokens";

    public static final Boolean COL_APPLY_COST = false;

    public static final String FIELD_STATUS = "status";

    public static final String FIELD_FILEKEY = "fileKey";

    public static final String FIELD_SHA256 = "sha256";

    public static final String FIELD_HAVE_TO_SEND_VALIDATION_EVENT = "haveToSendValidationEvent";

    public static final String FIELD_VALIDATION_EVENT_SENT = "validationEventSent";

    public static final String FIELD_CREATED = "created";

    public static final String FIELD_UPDATED = "updated";

    public static final String FILE_KEY_GSI = "fileKey-index";

    @Getter(onMethod=@__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(FIELD_SET_ID)
    }))
    private String setId;
    @Getter(onMethod=@__({
            @DynamoDbSortKey,
            @DynamoDbAttribute(FIELD_CX_ID)
    }))
    private String PathTokens;
    @Getter(onMethod=@__({
            @DynamoDbSortKey,
            @DynamoDbAttribute(FIELD_PATH_TOKENS)
    }))
    private String cxId;
    @Getter(onMethod=@__({
            @DynamoDbAttribute(FIELD_SET_ID)
    }))
    private Boolean applyCost;
    @Getter(onMethod=@__({
            @DynamoDbAttribute(FIELD_STATUS),
            @DynamoDbConvertedBy(F24MetadataStatusEntityConverter.class)
    }))
    private F24MetadataStatusEntity status;
    @Getter(onMethod=@__({
            @DynamoDbAttribute(FIELD_FILEKEY),
            @DynamoDbSecondaryPartitionKey(indexNames = FILE_KEY_GSI)
    }))
    private String fileKey;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_SHA256)}))
    private String sha256;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_HAVE_TO_SEND_VALIDATION_EVENT)}))
    private Boolean haveToSendValidationEvent;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_VALIDATION_EVENT_SENT)}))
    private Boolean validationEventSent;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_CREATED)}))
    private Instant created;
    @Getter(onMethod = @__({@DynamoDbAttribute(FIELD_UPDATED)}))
    private Instant updated;


}
