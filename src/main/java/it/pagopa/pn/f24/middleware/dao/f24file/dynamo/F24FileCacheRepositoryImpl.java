package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileCacheMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.BaseEntity.COL_PK;
import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity.GSI_FILE_KEY;

@Component
@Slf4j
public class F24FileCacheRepositoryImpl implements F24FileCacheDao {
    private final DynamoDbAsyncTable<F24FileCacheEntity> table;

    public F24FileCacheRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.table = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileCacheEntity.class));
    }

    @Override
    public Mono<F24File> getItem(String cxId, String setId, Integer cost, String pathTokens) {
        return getItem(cxId, setId, cost, pathTokens, false);
    }

    @Override
    public Mono<F24File> getItem(String cxId, String setId, Integer cost, String pathTokens, boolean isConsistentRead) {
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity(cxId, setId, cost, pathTokens);
        Key pk = Key.builder().partitionValue(f24FileCacheEntity.getPk()).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24FileCacheMapper::entityToDto);
    }

    @Override
    public Mono<F24File> updateItem(F24File f24File) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(F24FileCacheMapper.dtoToEntity(f24File))))
                .map(F24FileCacheMapper::entityToDto);
    }

    @Override
    public Mono<F24File> putItemIfAbsent(F24File f24File) {
        PutItemEnhancedRequest<F24FileCacheEntity> putItemEnhancedRequest = PutItemEnhancedRequest.builder(F24FileCacheEntity.class)
                .item(F24FileCacheMapper.dtoToEntity(f24File))
                .conditionExpression(
                        Expression.builder()
                                .expression("attribute_not_exists(pk)")
                                .build()
                )
                .build();
        return Mono.fromFuture(table.putItem(putItemEnhancedRequest))
                .thenReturn(f24File);
    }

    private UpdateItemEnhancedRequest<F24FileCacheEntity> createUpdateItemEnhancedRequest(F24FileCacheEntity entity) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#pk", COL_PK);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":pk", AttributeValue.builder().s(entity.getPk()).build());

        return UpdateItemEnhancedRequest
                .builder(F24FileCacheEntity.class)
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

    @Override
    public Mono<F24File> getItemByFileKey(String fileKey) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(fileKey)
                .build());

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(false)
                .build();

        return Mono.from(table.index(GSI_FILE_KEY).query(queryEnhancedRequest))
                .map(f24FileEntityPage -> f24FileEntityPage.items()
                        .stream()
                        .map(F24FileCacheMapper::entityToDto)
                        .toList()
                        .get(0)
                );
    }

}
