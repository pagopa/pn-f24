package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.F24MetadataDao;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper.F24MetadataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class F24MetadataRepositoryImpl implements F24MetadataDao {
    private static final String DEFAULT_SEPARATOR = "#";
    private final DynamoDbAsyncTable<F24MetadataEntity> table;

    private final F24Config f24Config;

    public F24MetadataRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.f24Config = f24Config;
        this.table = dynamoDbEnhancedClient.table(f24Config.getMetadataDao().getTableName(), TableSchema.fromBean(F24MetadataEntity.class));
    }

    @Override
    public Mono<F24Metadata> getItem(String setId, String cxId) {
        return getItem(setId, cxId, false);
    }

    @Override
    public Mono<F24Metadata> getItem(String setId, String cxId, boolean isConsistentRead) {
        String partitionKey = cxId+DEFAULT_SEPARATOR+setId;
        Key pk = Key.builder().partitionValue(partitionKey).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24MetadataMapper::entityToDto);
    }

    public Mono<Void> putItem(F24Metadata f24Metadata) {
        return Mono.fromFuture(table.putItem(F24MetadataMapper.dtoToEntity(f24Metadata)));
    }

    @Override
    public Mono<F24Metadata> updateItem(F24Metadata f24Metadata) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(F24MetadataMapper.dtoToEntity(f24Metadata))))
                .map(F24MetadataMapper::entityToDto);
    }

    private UpdateItemEnhancedRequest<F24MetadataEntity> createUpdateItemEnhancedRequest(F24MetadataEntity entity) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#pk", "pk");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":pk", AttributeValue.builder().s(entity.getPk()).build());

        return UpdateItemEnhancedRequest
                .builder(F24MetadataEntity.class)
                .conditionExpression(expressionBuilder("#pk = :pk", expressionValues, expressionNames))
                .item(entity)
                .ignoreNulls(true)
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
