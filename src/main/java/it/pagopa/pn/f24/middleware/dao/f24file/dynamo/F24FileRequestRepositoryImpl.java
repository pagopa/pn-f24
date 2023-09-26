package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileCacheMapper;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.BaseEntity.COL_UPDATED;
import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity.COL_RECORD_VERSION;
import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity.COL_STATUS;

@Component
@Slf4j
public class F24FileRequestRepositoryImpl implements F24FileRequestDao {
    private final DynamoDbAsyncTable<F24FileRequestEntity> f24FileRequestTable;
    private final DynamoDbAsyncTable<F24FileCacheEntity> f24FileCacheTable;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    public F24FileRequestRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.f24FileCacheTable = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileCacheEntity.class));
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedClient;
        this.f24FileRequestTable = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileRequestEntity.class));
    }

    @Override
    public Mono<F24Request> getItem(String cxId, String requestId) {
        return getItem(cxId, requestId, false);
    }

    @Override
    public Mono<F24Request> getItem(String cxId, String requestId, boolean isConsistentRead) {
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity(cxId, requestId);
        return getItem(f24FileRequestEntity.getPk(), isConsistentRead);
    }

    @Override
    public Mono<F24Request> getItem(String pk) {
        return getItem(pk, false);
    }

    @Override
    public Mono<F24Request> getItem(String pk, boolean isConsistentRead) {
        Key partitionKey = Key.builder().partitionValue(pk).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(partitionKey)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(f24FileRequestTable.getItem(getItemEnhancedRequest)).map(F24FileRequestMapper::entityToDto);
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

        return Mono.fromFuture(f24FileRequestTable.putItem(putItemEnhancedRequest));
    }

    @Override
    public Mono<F24Request> updateItem(F24Request f24Request) {
        return Mono.fromFuture(f24FileRequestTable.updateItem(createUpdateItemEnhancedRequest(F24FileRequestMapper.dtoToEntity(f24Request))))
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

    @Override
    public Mono<F24Request> setRequestStatusDone(F24Request f24Request) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#status", COL_STATUS);
        expressionNames.put("#recordVersion", COL_RECORD_VERSION);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        int previousRecordVersion = f24Request.getRecordVersion() - 1;
        expressionValues.put(":status", AttributeValue.builder().s(F24RequestStatusEntity.TO_PROCESS.getValue()).build());
        expressionValues.put(":recordVersion", AttributeValue.builder().n(Integer.toString(previousRecordVersion)).build());

        String expression = "#status = :status AND #recordVersion = :recordVersion";
        UpdateItemEnhancedRequest<F24FileRequestEntity> updateItemEnhancedRequest = UpdateItemEnhancedRequest
                .builder(F24FileRequestEntity.class)
                .conditionExpression(expressionBuilder(expression, expressionValues, expressionNames))
                .item(F24FileRequestMapper.dtoToEntity(f24Request))
                .build();

        return Mono.fromFuture(f24FileRequestTable.updateItem(updateItemEnhancedRequest))
                .map(F24FileRequestMapper::entityToDto);
    }

    @Override
    public Mono<Void> updateRequestAndRelatedFiles(PreparePdfLists preparePdfLists) {
        List<TransactPutItemEnhancedRequest<F24FileCacheEntity>> fileCachePutItemRequests = buildFileCachePutItemRequests(preparePdfLists.getFilesToCreate());

        TransactUpdateItemEnhancedRequest<F24FileRequestEntity> requestUpdate = buildFileRequestUpdateItemRequest(preparePdfLists.getF24Request());

        List<TransactUpdateItemEnhancedRequest<F24FileCacheEntity>> fileCacheUpdateItemRequests = preparePdfLists.getFilesNotReady().stream()
                .map(this::buildFileCacheUpdateItemRequest)
                .toList();

        TransactWriteItemsEnhancedRequest transaction = createTransactWriteItems(fileCachePutItemRequests, requestUpdate, fileCacheUpdateItemRequests);

        return Mono.fromFuture(dynamoDbEnhancedAsyncClient.transactWriteItems(transaction))
                .then();
    }

    private List<TransactPutItemEnhancedRequest<F24FileCacheEntity>> buildFileCachePutItemRequests(List<PreparePdfLists.F24FileToCreate> f24FilesToCreate) {
        return f24FilesToCreate.stream()
                .map(f24FileToCreate -> TransactPutItemEnhancedRequest
                            .builder(F24FileCacheEntity.class)
                            .item(F24FileCacheMapper.dtoToEntity(f24FileToCreate.getFile()))
                            .build()
                )
                .toList();
    }

    private TransactUpdateItemEnhancedRequest<F24FileRequestEntity> buildFileRequestUpdateItemRequest(F24Request f24Request) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#status", COL_STATUS);
        expressionNames.put("#recordVersion", COL_RECORD_VERSION);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        int previousRecordVersion = f24Request.getRecordVersion() - 1;
        expressionValues.put(":status", AttributeValue.builder().s(F24RequestStatusEntity.TO_PROCESS.getValue()).build());
        expressionValues.put(":recordVersion", AttributeValue.builder().n(Integer.toString(previousRecordVersion)).build());

        String expression = "#status = :status AND #recordVersion = :recordVersion";

        return TransactUpdateItemEnhancedRequest
                .builder(F24FileRequestEntity.class)
                .item(F24FileRequestMapper.dtoToEntity(f24Request))
                .conditionExpression(expressionBuilder(expression, expressionValues, expressionNames))
                .build();
    }

    private TransactUpdateItemEnhancedRequest<F24FileCacheEntity> buildFileCacheUpdateItemRequest(F24File f24File) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#updated", COL_UPDATED);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":updated", AttributeValue.builder().s(f24File.getUpdated().toString()).build());

        // Lo setto qui per permettere il check della condizione sul campo update del record già salvato
        f24File.setUpdated(Instant.now());

        return TransactUpdateItemEnhancedRequest
                .builder(F24FileCacheEntity.class)
                .item(F24FileCacheMapper.dtoToEntity(f24File))
                .conditionExpression(expressionBuilder("#updated = :updated", expressionValues, expressionNames))
                .build();
    }

    private TransactWriteItemsEnhancedRequest createTransactWriteItems(List<TransactPutItemEnhancedRequest<F24FileCacheEntity>> fileCachePutItemRequests,
                                                                       TransactUpdateItemEnhancedRequest<F24FileRequestEntity> fileRequestUpdate,
                                                                       List<TransactUpdateItemEnhancedRequest<F24FileCacheEntity>> fileCacheUpdateItemRequests) {
        TransactWriteItemsEnhancedRequest.Builder requestBuilder = TransactWriteItemsEnhancedRequest.builder();

        fileCachePutItemRequests.forEach(putItemRequest -> {
            requestBuilder.addPutItem(f24FileCacheTable, putItemRequest);
        });

        requestBuilder.addUpdateItem(f24FileRequestTable, fileRequestUpdate);

        fileCacheUpdateItemRequests.forEach(fileUpdateRequest -> {
            requestBuilder.addUpdateItem(f24FileCacheTable, fileUpdateRequest);
        });

        return requestBuilder.build();
    }

    @Override
    public Mono<Void> updateTransactionalFileAndRequests(List<F24Request> f24Requests, F24File f24File) {
        List<TransactUpdateItemEnhancedRequest<F24FileRequestEntity>> requestsUpdate = f24Requests.stream()
                .map(this::buildFileRequestUpdateItemRequest)
                .toList();

        TransactUpdateItemEnhancedRequest<F24FileCacheEntity> fileUpdate = buildFileCacheUpdateItemRequest(f24File);

        TransactWriteItemsEnhancedRequest.Builder requestBuilder = TransactWriteItemsEnhancedRequest.builder();
        requestBuilder.addUpdateItem(f24FileCacheTable, fileUpdate);
        requestsUpdate.forEach(requestUpdate -> requestBuilder.addUpdateItem(f24FileRequestTable, requestUpdate));
        TransactWriteItemsEnhancedRequest transactWriteItemsEnhancedRequest = requestBuilder.build();

        return Mono.fromFuture(dynamoDbEnhancedAsyncClient.transactWriteItems(transactWriteItemsEnhancedRequest))
                .then();
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
