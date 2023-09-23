package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.UpdateBehavior;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbUpdateBehavior;

import java.time.Instant;

@EqualsAndHashCode
public class BaseEntity {
    public static final String ITEMS_SEPARATOR = "#";
    public static final String COL_PK = "pk";
    private static final String COL_CREATED = "created";
    private static final String COL_UPDATED = "updated";

    protected BaseEntity(){
        this.setCreated(Instant.now());
        this.setUpdated(this.getCreated());
    }

    @Setter @Getter(onMethod=@__({@DynamoDbPartitionKey, @DynamoDbAttribute(COL_PK)}))
    private String pk;
    @Setter @Getter(onMethod=@__({@DynamoDbAttribute(COL_CREATED), @DynamoDbUpdateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)}))
    private Instant created;
    @Setter @Getter(onMethod=@__({@DynamoDbAttribute(COL_UPDATED)}))
    private Instant updated;
}