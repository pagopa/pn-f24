package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileCacheMapper;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.*;
import java.util.function.BiFunction;

import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.BaseEntity.COL_UPDATED;
import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity.COL_RECORD_VERSION;
import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity.COL_STATUS;

@Component
@Slf4j
public class F24FileRequestRepositoryImpl implements F24FileRequestDao {
    private static final int MAX_TRANSACTION_ITEMS = 100;
    private final DynamoDbAsyncTable<F24FileRequestEntity> f24FileRequestTable;
    private final DynamoDbAsyncTable<F24FileCacheEntity> f24FileCacheTable;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    public F24FileRequestRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.f24FileCacheTable = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileCacheEntity.class));
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedClient;
        this.f24FileRequestTable = dynamoDbEnhancedClient.table(f24Config.getFileTableName(), TableSchema.fromBean(F24FileRequestEntity.class));
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

        return Mono.fromFuture(f24FileRequestTable.getItem(getItemEnhancedRequest))
                .map(F24FileRequestMapper::entityToDto)
                .onErrorResume(ConditionalCheckFailedException.class, t -> Mono.error(new PnDbConflictException(t.getMessage())));
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

        return Mono.fromFuture(f24FileRequestTable.putItem(putItemEnhancedRequest))
                .onErrorMap(ConditionalCheckFailedException.class, t -> new PnDbConflictException(t.getMessage()));
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
                .map(F24FileRequestMapper::entityToDto)
                .onErrorMap(ConditionalCheckFailedException.class, t -> new PnDbConflictException(t.getMessage()));
    }

    @Override
    public Mono<Void> updateRequestAndRelatedFiles(PreparePdfLists preparePdfLists) {
        List<TransactPutItemEnhancedRequest<F24FileCacheEntity>> fileCachePutItemRequests = buildFileCachePutItemRequests(preparePdfLists.getFilesToCreate());

        TransactUpdateItemEnhancedRequest<F24FileRequestEntity> requestUpdate = buildFileRequestUpdateItemRequest(preparePdfLists.getF24Request());

        List<TransactUpdateItemEnhancedRequest<F24FileCacheEntity>> fileCacheUpdateItemRequests = preparePdfLists.getFilesNotReady().stream()
                .map(this::buildFileCacheUpdateItemRequest)
                .toList();

        List<TransactWriteItemsEnhancedRequest> transactions = createTransactWriteItems(fileCachePutItemRequests, requestUpdate, fileCacheUpdateItemRequests);

        return Flux.fromIterable(transactions)
                .flatMap(transaction -> Mono.fromFuture(dynamoDbEnhancedAsyncClient.transactWriteItems(transaction))
                            .onErrorMap(TransactionCanceledException.class, t -> new PnDbConflictException(t.getMessage()))
                )
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
        expressionValues.put(":status", AttributeValue.builder().s(F24RequestStatusEntity.TO_PROCESS.getValue()).build());
        expressionValues.put(":recordVersion", AttributeValue.builder().n(Integer.toString(f24Request.getRecordVersion())).build());
        String expression = "#status = :status AND #recordVersion = :recordVersion";

        f24Request.setRecordVersion(f24Request.getRecordVersion() + 1);

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

        return TransactUpdateItemEnhancedRequest
                .builder(F24FileCacheEntity.class)
                .item(F24FileCacheMapper.dtoToEntity(f24File))
                .conditionExpression(expressionBuilder("#updated = :updated", expressionValues, expressionNames))
                .build();
    }

    private List<TransactWriteItemsEnhancedRequest> createTransactWriteItems(List<TransactPutItemEnhancedRequest<F24FileCacheEntity>> fileCachePutItemRequests,
                                                                       TransactUpdateItemEnhancedRequest<F24FileRequestEntity> fileRequestUpdate,
                                                                       List<TransactUpdateItemEnhancedRequest<F24FileCacheEntity>> fileCacheUpdateItemRequests) {
        List<TransactWriteItemsEnhancedRequest.Builder> builders = new ArrayList<>();
        int transactionItemCounter = 1;
        transactionItemCounter = handleMultiTransactionWriteRequests(builders, transactionItemCounter, List.of(fileRequestUpdate), (requestBuilder, request) -> requestBuilder.addUpdateItem(f24FileRequestTable, fileRequestUpdate));
        transactionItemCounter = handleMultiTransactionWriteRequests(builders, transactionItemCounter, fileCachePutItemRequests, (requestBuilder, file) -> requestBuilder.addPutItem(f24FileCacheTable, file));
        transactionItemCounter = handleMultiTransactionWriteRequests(builders, transactionItemCounter, fileCacheUpdateItemRequests, (requestBuilder, file) -> requestBuilder.addUpdateItem(f24FileCacheTable, file));

        log.debug("Processed {} transactionItems and obtained {} TransactWriteItemsEnhancedRequest", transactionItemCounter - 1, builders.size());
        return builders.stream()
                .map(TransactWriteItemsEnhancedRequest.Builder::build)
                .toList();
    }


    /**
     * The handleMultiTransactions function is a helper function that takes in a list of builders,
     * the current transaction item counter, an iterable list to handle (such as a List&lt;T&gt;), and
     * finally a BiFunction that will be used to apply each element of the iterable list. The purpose
     * of this function is to allow for multiple items from an Iterable object (such as List&lt;T&gt;) to be added into one
     * or more TransactWriteItemsEnhancedRequest.Builder objects.
     * This allows us to add multiple items into one request builder if we are under 25MB, but also allows us to split up
     * our requests across multiple
     *
     * @param builders Store the requests
     * @param transactionItemCounter Keep track of the number of items in a transaction
     * @param listToHandle Iterate over the list of items to be handled
     * @param transactionItemSupplier Add items to the transaction
     *
     * @return The transactionitemcounter value
     *
     */
    private <T> int handleMultiTransactionWriteRequests(List<TransactWriteItemsEnhancedRequest.Builder> builders,
                                                        int transactionItemCounter,
                                                        Iterable<T> listToHandle,
                                                        BiFunction<TransactWriteItemsEnhancedRequest.Builder, T, TransactWriteItemsEnhancedRequest.Builder> transactionItemSupplier) {
        for (T item : listToHandle) {
            TransactWriteItemsEnhancedRequest.Builder requestBuilder = getTransactionRequestBuilder(builders, transactionItemCounter);
            transactionItemSupplier.apply(requestBuilder, item);
            transactionItemCounter++;
        }
        return transactionItemCounter;
    }

    /**
     * The getTransactionRequestBuilder function is used to get the correct TransactWriteItemsEnhancedRequest.Builder object
     * from a list of builders, based on the transactionItemCounter parameter. The function will return null if there are no
     * more builders available in the list and it will fill up any missing builder objects in between with new ones.

     *
     * @param builders Store the builders
     * @param transactionItemCounter Determine which builder to use
     *
     * @return A builder from a list of builders
     *
     */
    private TransactWriteItemsEnhancedRequest.Builder getTransactionRequestBuilder(List<TransactWriteItemsEnhancedRequest.Builder> builders, int transactionItemCounter) {
        int supposedBuilderIndex = getSupposedBuilderIndex(transactionItemCounter);
        int sizeToReach = supposedBuilderIndex + 1;
        if(builders.size() < sizeToReach) {
            padToListIndex(builders, supposedBuilderIndex);
        }

        return builders.get(supposedBuilderIndex);
    }

    /**
     * The padToListIndex function is used to ensure that the builders list has enough elements in it
     * so that we can add a new item to the builder at index supposedBuilderIndex.
     * If there are not enough elements, then this function will add more empty builders until there are.
     *
     * @param builders Store the builders that are used to create a transactwriteitemsenhancedrequest
     * @param supposedBuilderIndex Determine the index of the builder in which to add a new item
     *
     */
    private void padToListIndex(List<TransactWriteItemsEnhancedRequest.Builder> builders, int supposedBuilderIndex) {
        log.debug("Padding builders list to reach index: {}", supposedBuilderIndex);
        int actualLastIndex = builders.size() - 1;
        for(int i = actualLastIndex; i < supposedBuilderIndex; i++) {
            builders.add(TransactWriteItemsEnhancedRequest.builder());
        }
    }

    /**
     * The getSupposedIndexBuilder function is used to determine the index of the TransactionBuilder that
     * a given transaction item should be added to. The function takes in an integer representing how many
     * transaction items have been added so far, and returns an integer representing which TransactionBuilder
     * it should be added to. This is done by dividing the number of transaction items by MAX_TRANSACTION_ITEMS,
     * and then adding 1 if there was a remainder (i.e., if there are more than MAX_TRANSACTION_ITEMS).
     * Then we subtract 1 from this value because arrays start at 0 instead of 1.
     *
     * @param transactionItemCounter Determine the index of the builder to be used
     *
     * @return The index of the builder in the array
     *
     */
    private int getSupposedBuilderIndex(int transactionItemCounter) {
        if(transactionItemCounter == 0) {
            return 0;
        }

        int remainder = transactionItemCounter % MAX_TRANSACTION_ITEMS;
        return (transactionItemCounter / MAX_TRANSACTION_ITEMS) + (remainder > 0 ? 1 : 0) - 1;
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
                .onErrorMap(TransactionCanceledException.class, t -> new PnDbConflictException(t.getMessage()))
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
