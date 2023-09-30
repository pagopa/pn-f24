package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class F24FileRequestRepositoryImpl implements F24FileRequestDao {
    private final DynamoDbAsyncTable<F24FileRequestEntity> table;

    public F24FileRequestRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.table = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileRequestEntity.class));
    }

    @Override
    public Mono<F24Request> getItem(String requestId) {
        return getItem(requestId, false);
    }

    @Override
    public Mono<F24Request> getItem(String requestId, boolean isConsistentRead) {
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity(requestId);

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(k -> k.partitionValue(f24FileRequestEntity.getPk()))
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24FileRequestMapper::entityToDto);
    }

    public Mono<Void> putItemIfAbsent(F24Request f24Request) {
        PutItemEnhancedRequest<F24FileRequestEntity> putItemEnhancedRequest = PutItemEnhancedRequest.builder(F24FileRequestEntity.class)
                .item(F24FileRequestMapper.dtoToEntity(f24Request))
                .conditionExpression(
                        Expression.builder()
                                .expression("attribute_not_exists(pk)")
                                .build()
                )
                .build();

        return Mono.fromFuture(table.putItem(putItemEnhancedRequest));
    }

    @Override
    public Mono<F24Request> updateItem(F24Request f24Request) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(F24FileRequestMapper.dtoToEntity(f24Request))))
                .map(F24FileRequestMapper::entityToDto);
    }
    private UpdateItemEnhancedRequest<F24FileRequestEntity> createUpdateItemEnhancedRequest(F24FileRequestEntity entity) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#pk", "pk");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":pk", AttributeValue.builder().s(entity.getPk()).build());

        return UpdateItemEnhancedRequest
                .builder(F24FileRequestEntity.class)
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
