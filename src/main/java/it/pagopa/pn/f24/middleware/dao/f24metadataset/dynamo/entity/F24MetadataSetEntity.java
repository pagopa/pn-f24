package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper.F24MetadataSetStatusEntityConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDbBean
public class F24MetadataSetEntity {
    public static final String ITEMS_SEPARATOR = "#";
    public static final String FIELD_PK = "pk";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_FILE_KEYS = "fileKeys";
    public static final String FIELD_SHA_256 = "sha256";
    public static final String FIELD_HAVE_TO_SEND_VALIDATION_EVENT = "haveToSendValidationEvent";
    public static final String FIELD_VALIDATION_EVENT_SENT = "validationEventSent";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_UPDATED = "updated";
    public static final String FIELD_TTL = "ttl";
    public static final String FIELD_VALIDATION_RESULT = "validationResult";

    private static final int CX_ID_INDEX = 0;
    private static final int SET_ID_INDEX = 1;

    public F24MetadataSetEntity(String cxId, String setId) {
        this.setPk(cxId, setId);
    }

    @Getter(onMethod=@__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(FIELD_PK)
    }))
    private String pk;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_STATUS), @DynamoDbConvertedBy(F24MetadataSetStatusEntityConverter.class)}))
    private F24MetadataSetStatusEntity status;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_FILE_KEYS)}))
    private Map<String, F24MetadataRefEntity> fileKeys;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_SHA_256)}))
    private String sha256;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_HAVE_TO_SEND_VALIDATION_EVENT)}))
    private Boolean haveToSendValidationEvent;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_VALIDATION_EVENT_SENT)}))
    private Boolean validationEventSent;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_VALIDATION_RESULT)}))
    private List<F24MetadataValidationEntity> validationResult;
    @Getter(onMethod=@__({@DynamoDbAttribute(FIELD_CREATED)}))
    private Instant created;
    @Getter(onMethod = @__({@DynamoDbAttribute(FIELD_UPDATED)}))
    private Instant updated;
    @Getter(onMethod = @__({@DynamoDbAttribute(FIELD_TTL)}))
    private Long ttl;

    private void setPk(String cxId, String setId) {
        this.pk = cxId + ITEMS_SEPARATOR + setId;
    }

    public String getCxId() {
        return this.pk.split(ITEMS_SEPARATOR)[CX_ID_INDEX];
    }

    public String getSetId() {
        return this.pk.split(ITEMS_SEPARATOR)[SET_ID_INDEX];
    }

}
