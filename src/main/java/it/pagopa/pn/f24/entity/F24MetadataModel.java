/*
package it.pagopa.pn.f24.entity;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

import static it.pagopa.pn.f24.constant.F24MetadataConstant.*;

@Data
@ToString
@DynamoDbBean
public class F24MetadataModel {
    @Getter(onMethod = @__({
            @DynamoDbPartitionKey,
            @DynamoDbAttribute(PK)
    }))
    private String pk;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_METADATA)
    }))
    private String metadata;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_STATUS)
    }))
    private String status;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_HAVE_TO_SEND_VALIDATION_EVENT)
    }))
    private boolean haveToSendValidationEvent ;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_VALIDATION_EVENT_SENDED)
    }))
    private boolean validationEventSended;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_CREATED)
    }))
    private LocalDateTime created;
    @Getter(onMethod = @__({
            @DynamoDbAttribute(COL_UPDATED)
    }))
    private LocalDateTime updated;
}
*/
