package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24FileCachRepositoryImplTest {
    @MockBean
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @MockBean
    private DynamoDbAsyncTable<Object> dynamoDbAsyncTable;

    @MockBean
    private F24Config f24Config;

    @Test
    void getItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        String cxId = "cxId";
        String setId = "setId";
        Integer cost = 1000;
        String pathTokens = "0_0";
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity(cxId, setId, cost, pathTokens);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.getItem(cxId, setId, cost, pathTokens))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("CACHE#cxId#setId#1000#0_0"))
                .verifyComplete();
    }

    @Test
    void getItemWithConsistentRead() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        String cxId = "cxId";
        String setId = "setId";
        Integer cost = 1000;
        String pathTokens = "0_0";
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity(cxId, setId, cost, pathTokens);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.getItem(cxId, setId, cost, pathTokens))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("CACHE#cxId#setId#1000#0_0"))
                .verifyComplete();
    }


    @Test
    @Disabled
    void putItemIfAbsent() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> null);

        F24File f24File = new F24File();
        f24File.setSetId("setId");
        f24File.setCxId("cxId");
        when(dynamoDbAsyncTable.putItem(f24File)).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.putItemIfAbsent(f24File))
                .expectComplete()
                .verify();
    }

    @Test
    void getItemByFileKey() {
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(dynamoDbAsyncTable);
        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        SdkPublisher<Page<Object>> sdkPublisher = mock(SdkPublisher.class);
        DynamoDbAsyncIndex<Object> index = mock(DynamoDbAsyncIndex.class);
        when(index.query((QueryEnhancedRequest) any())).thenReturn(sdkPublisher);
        when(dynamoDbAsyncTable.index(any())).thenReturn(index);
        StepVerifier.create(f24FileCacheRepository.getItemByFileKey("key"))
                .expectNextCount(0);
    }

    @Test
    void updateItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity();
        f24FileCacheEntity.setPk("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        F24File f24File = new F24File();
        f24File.setPk("42");
        StepVerifier.create(f24FileCacheRepository.updateItem(f24File))
                .expectNext()
                .expectComplete();
    }
}
