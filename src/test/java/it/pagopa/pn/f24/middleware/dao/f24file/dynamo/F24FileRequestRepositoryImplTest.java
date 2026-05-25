package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24Request;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.pagopa.pn.f24.dto.F24RequestStatus;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24FileRequestRepositoryImplTest {
    @MockitoBean
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @MockitoBean
    private DynamoDbAsyncTable<Object> dynamoDbAsyncTable;

    @MockitoBean
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @MockitoBean
    private F24Config f24Config;

    /**
     * Method under test: {@link F24FileRequestRepositoryImpl#getItem(String)}
     */
    @Test
    void testGetItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        String requestId = "requestId";
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity(requestId);
        f24FileRequestEntity.setFiles(new HashMap<>());

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileRequestEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileRequestRepository.getItem(requestId))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("REQUEST#requestId"))
                .verifyComplete();
    }

    /**
     * Method under test: {@link F24FileRequestRepositoryImpl#getItem(String, boolean)}
     */
    @Test
    void testGetItem2() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        String requestId = "requestId";
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity(requestId);
        f24FileRequestEntity.setFiles(new HashMap<>());

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileRequestEntity);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24FileRequestRepository.getItem(requestId, false))
                .expectNextMatches(f24FileCache -> f24FileCache.getPk().equalsIgnoreCase("REQUEST#requestId"))
                .verifyComplete();
    }

    /**
     * Method under test: {@link F24FileRequestRepositoryImpl#putItemIfAbsent(F24Request)}
     */
    @Test
    @Disabled("to repair")
    void testPutItemIfAbsent() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> null);

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setCxId("cxId");
        when(dynamoDbAsyncTable.putItem(f24Request)).thenReturn(completableFuture);

        StepVerifier.create(f24FileRequestRepository.putItemIfAbsent(f24Request))
                .expectComplete()
                .verify();
    }

    /**
     * Method under test: {@link F24FileRequestRepositoryImpl#updateItem(F24Request)}
     */
    @Test
    void testUpdateItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity();
        f24FileRequestEntity.setPk("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileRequestEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        F24Request f24Request = new F24Request();
        f24Request.setPk("42");
        f24Request.setFiles(new HashMap<>());

        StepVerifier.create(f24FileRequestRepository.updateItem(f24Request))
                .expectNext()
                .expectComplete();
    }

    @Test
    void testSetRequestStatusDone() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity();
        f24FileRequestEntity.setPk("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24FileRequestEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any()))
                .thenReturn(completableFuture);

        F24Request f24Request = new F24Request();
        f24Request.setPk("42");
        f24Request.setFiles(new HashMap<>());
        f24Request.setRecordVersion(0);
        f24Request.setStatus(F24RequestStatus.DONE);

        StepVerifier.create(f24FileRequestRepository.setRequestStatusDone(f24Request))
                .expectNext()
                .expectComplete();
    }

    @Test
    void testUpdateTransactionalFileAndRequests() {
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(
                dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        // Mock low-level updateItem per setFileKeyOnRequest
        CompletableFuture<UpdateItemResponse> lowLevelFuture = new CompletableFuture<>();
        lowLevelFuture.complete(UpdateItemResponse.builder().build());
        when(dynamoDbAsyncClient.updateItem((UpdateItemRequest) any())).thenReturn(lowLevelFuture);

        // Mock Enhanced Client updateItem per setF24FileStatusDoneOrSwallow
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity();
        f24FileCacheEntity.setPk("CACHE#setId#200#0_0");
        CompletableFuture<Object> enhancedFuture = new CompletableFuture<>();
        enhancedFuture.complete(f24FileCacheEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(enhancedFuture);

        F24Request f24Request = new F24Request();
        f24Request.setPk("REQUEST#request0");
        f24Request.setRequestId("request0");
        f24Request.setFiles(new HashMap<>());

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setSetId("setId");
        f24File.setCost(200);
        f24File.setPathTokens("0_0");
        f24File.setFileKey("key_0_test");
        f24File.setStatus(F24FileStatus.GENERATED);

        StepVerifier.create(f24FileRequestRepository.updateRequestsAndSetFileDone(List.of(f24Request), f24File))
                .expectComplete()
                .verify();

        verify(dynamoDbAsyncClient, times(1)).updateItem((UpdateItemRequest) any());
        verify(dynamoDbAsyncTable, times(1)).updateItem((UpdateItemEnhancedRequest<Object>) any());
    }

    @Test
    void testUpdateTransactionalFileAndRequests_fileAlreadyDone_swallowed() {
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(
                dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        CompletableFuture<UpdateItemResponse> lowLevelFuture = new CompletableFuture<>();
        lowLevelFuture.complete(UpdateItemResponse.builder().build());
        when(dynamoDbAsyncClient.updateItem((UpdateItemRequest) any())).thenReturn(lowLevelFuture);

        // Simula ConditionalCheckFailedException (file già DONE)
        CompletableFuture<Object> enhancedFuture = new CompletableFuture<>();
        enhancedFuture.completeExceptionally(
                software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException.builder()
                        .message("The conditional request failed")
                        .build()
        );
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(enhancedFuture);

        F24Request f24Request = new F24Request();
        f24Request.setPk("REQUEST#request0");
        f24Request.setRequestId("request0");
        f24Request.setFiles(new HashMap<>());

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setSetId("setId");
        f24File.setCost(200);
        f24File.setPathTokens("0_0");
        f24File.setFileKey("key_0_test");
        f24File.setStatus(F24FileStatus.GENERATED);

        StepVerifier.create(f24FileRequestRepository.updateRequestsAndSetFileDone(List.of(f24Request), f24File))
                .expectComplete()
                .verify();
    }

    @Test
    @Disabled("to repair")
    void testUpdateRequestAndRelatedFiles() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, f24Config);

        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity();
        f24FileRequestEntity.setPk("42");

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> null);
        when(dynamoDbEnhancedAsyncClient.transactWriteItems((TransactWriteItemsEnhancedRequest) any())).thenReturn(completableFuture);

        F24Request f24Request = new F24Request();
        f24Request.setPk("42");
        f24Request.setFiles(new HashMap<>());
        f24Request.setRecordVersion(0);
        f24Request.setStatus(F24RequestStatus.DONE);

        PreparePdfLists preparePdfLists = new PreparePdfLists(f24Request);
        F24File f24FileNotReady = new F24File();
        f24FileNotReady.setPk("CACHE#IUN_001#NO_COST#0_0");
        f24FileNotReady.setUpdated(Instant.now());
        preparePdfLists.setFilesNotReady(List.of(f24FileNotReady));

        StepVerifier.create(f24FileRequestRepository.updateRequestAndRelatedFiles(preparePdfLists))
                .expectNext()
                .expectComplete();
    }
}

