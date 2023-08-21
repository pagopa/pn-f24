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
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataEntity.FILE_KEY_GSI;

@Component
@Slf4j
public class F24MetadataRepositoryImpl implements F24MetadataDao {
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
        Key pk = Key.builder().partitionValue(setId).sortValue(cxId).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24MetadataMapper::entityToDto);
    }

    @Override
    public Mono<F24Metadata> getItemByFileKey(String fileKey) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(fileKey)
                .build());

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(false)
                .build();
        return Mono.from(table.index(FILE_KEY_GSI).query(queryEnhancedRequest))
                .map(f24MetadataEntityPage -> F24MetadataMapper.entityToDto(f24MetadataEntityPage.items().get(0)));
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
        expressionNames.put("#setId", "setId");
        expressionNames.put("#cxId", "cxId");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":setId", AttributeValue.builder().s(entity.getSetId()).build());
        expressionValues.put(":cxId", AttributeValue.builder().s(entity.getCxId()).build());

        return UpdateItemEnhancedRequest
                .builder(F24MetadataEntity.class)
                .conditionExpression(expressionBuilder("#setId = :setId AND #cxId = :cxId", expressionValues, expressionNames))
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
