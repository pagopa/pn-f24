package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24RequestStatusEntityConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DynamoDbBean
public class F24FileRequestEntity extends BaseEntity {
    private static final String PK_PREFIX = "REQUEST#";
    public static final String COL_FILES = "files";
    public static final String COL_SET_ID = "setId";
    public static final String COL_CX_ID = "cxId";
    public static final String COL_PATH_TOKENS = "pathTokens";
    public static final String COL_COST = "cost";
    public static final String COL_RECORD_VERSION = "recordVersion";
    public static final String COL_STATUS = "status";
    public static final String COL_TTL = "ttl";
    private static final int REQUEST_ID_INDEX = 1;


    public F24FileRequestEntity(String requestId) {
        this.setPk(PK_PREFIX + requestId);
    }

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_FILES)}))
    private Map<String, FileRefEntity> files;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_SET_ID)}))
    private String setId;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_CX_ID)}))
    private String cxId;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_PATH_TOKENS)}))
    private String pathTokens;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_COST)}))
    private Integer cost;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_STATUS), @DynamoDbConvertedBy(F24RequestStatusEntityConverter.class)}))
    private F24RequestStatusEntity status;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_RECORD_VERSION)}))
    private Integer recordVersion;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TTL)}))
    private Long ttl;

    @DynamoDbIgnore
    public String getRequestId() { return this.getPk().split(ITEMS_SEPARATOR)[REQUEST_ID_INDEX]; }
}
