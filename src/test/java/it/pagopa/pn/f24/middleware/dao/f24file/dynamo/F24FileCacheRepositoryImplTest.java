package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24FileCacheRepositoryImplTest {
    @MockBean
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @MockBean
    private DynamoDbAsyncTable<Object> dynamoDbAsyncTable;

    @MockBean
    private F24Config f24Config;

    @Test
    void getItemByPk() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        String pk = "CACHE#setId#1000#0_0";
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity();
        f24FileCacheEntity.setPk(pk);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.getItem(pk))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("CACHE#setId#1000#0_0"))
                .verifyComplete();
    }

    @Test
    void getItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        String setId = "setId";
        Integer cost = 1000;
        String pathTokens = "0_0";
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity(setId, cost, pathTokens);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.getItem(setId, cost, pathTokens))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("CACHE#setId#1000#0_0"))
                .verifyComplete();
    }

    @Test
    @Disabled("to repair")
    void putItemIfAbsent() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> null);

        F24File f24File = new F24File();
        f24File.setSetId("setId");
        when(dynamoDbAsyncTable.putItem(f24File)).thenReturn(completableFuture);

        StepVerifier.create(f24FileCacheRepository.putItemIfAbsent(f24File))
                .expectComplete()
                .verify();
    }

    @Test
    void getItemByFileKey() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        DynamoDbAsyncIndex index = mock(DynamoDbAsyncIndex.class);
        when(dynamoDbAsyncTable.index(anyString())).thenReturn(index);

        F24FileCacheEntity entity = new F24FileCacheEntity();
        entity.setPk("setId", 1000, "0_0");
        entity.setStatus(F24FileStatusEntity.GENERATED);
        Page<F24FileCacheEntity> emptyPage = Page.create(List.of(entity));
        SdkPublisher<Page<F24FileCacheEntity>> sdkPublisher = SdkPublisher.adapt(Flux.just(emptyPage));

        when(index.query(any(QueryEnhancedRequest.class))).thenReturn(sdkPublisher);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        StepVerifier.create(f24FileCacheRepository.getItemByFileKey("key"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getItemByFileKey_EmptyPage() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        DynamoDbAsyncIndex index = mock(DynamoDbAsyncIndex.class);
        when(dynamoDbAsyncTable.index(anyString())).thenReturn(index);

        Page<F24FileCacheEntity> emptyPage = Page.create(Collections.emptyList());
        SdkPublisher<Page<F24FileCacheEntity>> sdkPublisher = SdkPublisher.adapt(Flux.just(emptyPage));

        when(index.query(any(QueryEnhancedRequest.class))).thenReturn(sdkPublisher);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        StepVerifier.create(f24FileCacheRepository.getItemByFileKey("key"))
                .expectNextCount(0)
                .verifyComplete();
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

    @Test
    void setStatusDone() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity();
        f24FileCacheEntity.setPk("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        F24File f24FileToUpdate = new F24File();
        f24FileToUpdate.setPk("42");
        StepVerifier.create(f24FileCacheRepository.setStatusDone(f24FileToUpdate))
                .expectNextMatches(f24FileUpdated -> f24FileUpdated.getStatus() == F24FileStatus.DONE)
                .expectComplete();
    }

    @Test
    void setFileKey() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileCacheRepositoryImpl f24FileCacheRepository = new F24FileCacheRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity();
        f24FileCacheEntity.setPk("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileCacheEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        F24File f24FileToUpdate = new F24File();
        f24FileToUpdate.setPk("42");
        StepVerifier.create(f24FileCacheRepository.setFileKey(f24FileToUpdate, "fileKey"))
                .expectNextMatches(f24FileUpdated -> f24FileUpdated.getStatus() == F24FileStatus.GENERATED && f24FileUpdated.getFileKey().equalsIgnoreCase("fileKey"))
                .expectComplete();
    }
}
