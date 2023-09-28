package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper.F24MetadataSetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity.FIELD_STATUS;

@Component
@Slf4j
public class F24MetadataSetRepositoryImpl implements F24MetadataSetDao {
    private final DynamoDbAsyncTable<F24MetadataSetEntity> table;

    private final F24Config f24Config;

    public F24MetadataSetRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.f24Config = f24Config;
        this.table = dynamoDbEnhancedClient.table(f24Config.getMetadataSetTableName(), TableSchema.fromBean(F24MetadataSetEntity.class));
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId) {
        return getItem(setId,false);
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId, boolean isConsistentRead) {
        Key pk = Key.builder().partitionValue(setId).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24MetadataSetMapper::entityToDto);
    }

    @Override
    public Mono<Void> putItemIfAbsent(F24MetadataSet f24MetadataSet) {
        PutItemEnhancedRequest<F24MetadataSetEntity> putItemEnhancedRequest = PutItemEnhancedRequest.builder(F24MetadataSetEntity.class)
                .item(F24MetadataSetMapper.dtoToEntity(f24MetadataSet))
                .conditionExpression(
                    Expression.builder()
                            .expression("attribute_not_exists(pk)")
                            .build()
                )
                .build();

        return Mono.fromFuture(table.putItem(putItemEnhancedRequest));
    }

    @Override
    public Mono<F24MetadataSet> setF24MetadataSetStatusValidationEnded(F24MetadataSet f24MetadataSet) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#status", FIELD_STATUS);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":status", AttributeValue.builder().s(F24MetadataSetStatusEntity.TO_VALIDATE.getValue()).build());

        UpdateItemEnhancedRequest<F24MetadataSetEntity> updateItemEnhancedRequest = UpdateItemEnhancedRequest
                .builder(F24MetadataSetEntity.class)
                .conditionExpression(expressionBuilder("#status = :status", expressionValues, expressionNames))
                .item(F24MetadataSetMapper.dtoToEntity(f24MetadataSet))
                .build();

        return Mono.fromFuture(table.updateItem(updateItemEnhancedRequest))
                .map(F24MetadataSetMapper::entityToDto);
    }

    @Override
    public Mono<F24MetadataSet> updateItem(F24MetadataSet f24MetadataSet) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(F24MetadataSetMapper.dtoToEntity(f24MetadataSet))))
                .map(F24MetadataSetMapper::entityToDto);
    }

    private UpdateItemEnhancedRequest<F24MetadataSetEntity> createUpdateItemEnhancedRequest(F24MetadataSetEntity entity) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#setId", "setId");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":setId", AttributeValue.builder().s(entity.getSetId()).build());

        return UpdateItemEnhancedRequest
                .builder(F24MetadataSetEntity.class)
                .conditionExpression(expressionBuilder("#setId = :setId", expressionValues, expressionNames))
                .item(entity)
                .build();
    }

    public static Expression expressionBuilder(String expression, Map<String, AttributeValue> expressionValues, Map<String, String> expressionNames) {
        Expression.Builder expressionBuilder = Expression.builder();
        if (expression != null) {
            expressionBuilder.expression(expression);
        }
        if (expressionValues != null) {
            expressionBuilder.expressionValues(expressionValues);
        }
        if (expressionNames != null) {
            expressionBuilder.expressionNames(expressionNames);
        }
        return expressionBuilder.build();
    }
}
