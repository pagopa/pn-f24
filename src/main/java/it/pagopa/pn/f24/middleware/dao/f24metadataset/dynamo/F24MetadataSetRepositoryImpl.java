package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper.F24MetadataSetMapper;
import it.pagopa.pn.f24.middleware.dao.util.EntityKeysUtils;
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

@Component
@Slf4j
public class F24MetadataSetRepositoryImpl implements F24MetadataSetDao {
    private static final String DEFAULT_SEPARATOR = "#";
    private final DynamoDbAsyncTable<F24MetadataSetEntity> table;

    private final F24Config f24Config;

    public F24MetadataSetRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.f24Config = f24Config;
        this.table = dynamoDbEnhancedClient.table(f24Config.getMetadataDao().getTableName(), TableSchema.fromBean(F24MetadataSetEntity.class));
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId, String cxId) {
        String partitionKey = EntityKeysUtils.F24MetadataSet.createPk(cxId, setId);
        return getItem(partitionKey, false);
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId, String cxId, boolean isConsistentRead) {
        String partitionKey = EntityKeysUtils.F24MetadataSet.createPk(cxId, setId);
        return getItem(partitionKey, isConsistentRead);
    }

    @Override
    public Mono<F24MetadataSet> getItem(String partitionKey, boolean isConsistentRead) {
        Key pk = Key.builder().partitionValue(partitionKey).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24MetadataSetMapper::entityToDto);
    }

    @Override
    public Mono<Void> putItem(F24MetadataSet f24MetadataSet) {
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
    public Mono<F24MetadataSet> updateItem(F24MetadataSet f24MetadataSet) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(F24MetadataSetMapper.dtoToEntity(f24MetadataSet))))
                .map(F24MetadataSetMapper::entityToDto);
    }

    private UpdateItemEnhancedRequest<F24MetadataSetEntity> createUpdateItemEnhancedRequest(F24MetadataSetEntity entity) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#pk", "pk");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":pk", AttributeValue.builder().s(entity.getPk()).build());

        return UpdateItemEnhancedRequest
                .builder(F24MetadataSetEntity.class)
                .conditionExpression(expressionBuilder("#pk = :pk", expressionValues, expressionNames))
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
