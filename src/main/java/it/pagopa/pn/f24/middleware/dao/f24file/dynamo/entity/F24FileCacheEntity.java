package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileStatusEntityConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DynamoDbBean
public class F24FileCacheEntity extends BaseEntity {
    private static final String PK_PREFIX = "CACHE#";
    public static final String COL_FILE_KEY = "fileKey";
    public static final String COL_STATUS = "status";
    public static final String COL_REQUEST_IDS = "requestIds";
    public static final String COL_TTL = "ttl";
    public static final String GSI_FILE_KEY = "fileKey-index";
    private static final int CX_ID_INDEX = 1;
    private static final int SET_ID_INDEX = 2;
    private static final int COST_INDEX = 3;
    private static final int PATH_TOKENS_INDEX = 4;
    public static final String NO_COST = "NO_COST";

    public F24FileCacheEntity(String cxId, String setId, Integer cost, String pathTokens) {
        this.setPk(cxId, setId, cost, pathTokens);
    }

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_STATUS),
            @DynamoDbConvertedBy(F24FileStatusEntityConverter.class)
    }))
    private F24FileStatusEntity status;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_FILE_KEY),
            @DynamoDbSecondaryPartitionKey(indexNames = GSI_FILE_KEY)
    }))
    private String fileKey;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_REQUEST_IDS)
    }))
    private List<String> requestIds;

    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_TTL)
    }))
    private Long ttl;

    public void setPk(String cxId, String setId, Integer cost, String pathTokens) {
        String evaluatedCost = cost != null ? cost.toString() : NO_COST;
        this.setPk(PK_PREFIX + cxId + ITEMS_SEPARATOR + setId + ITEMS_SEPARATOR + evaluatedCost + ITEMS_SEPARATOR + pathTokens);
    }
    @DynamoDbIgnore
    public String getCxId() {
        return this.getPk().split(ITEMS_SEPARATOR)[CX_ID_INDEX];
    }
    @DynamoDbIgnore
    public String getSetId() {
        return this.getPk().split(ITEMS_SEPARATOR)[SET_ID_INDEX];
    }
    @DynamoDbIgnore
    public Integer getCost() {
        String cost = this.getPk().split(ITEMS_SEPARATOR)[COST_INDEX];
        return cost.equals(NO_COST) ? null : Integer.parseInt(cost);
    }
    @DynamoDbIgnore
    public String getPathTokens() {
        return this.getPk().split(ITEMS_SEPARATOR)[PATH_TOKENS_INDEX];
    }

}
