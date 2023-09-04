package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity;

import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper.F24MetadataStatusEntityConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDbBean
public class F24MetadataEntity {
    public static final String FIELD_PK = "pk";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_FILE_KEYS = "fileKeys";

    public static final String FIELD_SHA_256 = "sha256";
    public static final String FIELD_HAVE_TO_SEND_VALIDATION_EVENT = "haveToSendValidationEvent";
    public static final String FIELD_VALIDATION_EVENT_SENT = "validationEventSent";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_UPDATED = "updated";

    public static final String FIELD_VALIDATION_RESULT = "validationResult";

    @Getter(onMethod=@__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(FIELD_PK)
    }))
    private String pk;
    @Getter(onMethod=@__({
            @DynamoDbAttribute(FIELD_STATUS),
            @DynamoDbConvertedBy(F24MetadataStatusEntityConverter.class)
    }))
    private F24MetadataStatusEntity status;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_FILE_KEYS)}))
    private Map<String, F24MetadataItemEntity> fileKeys;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_SHA_256)}))
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
