package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24Request;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24FileRequestRepositoryImplTest {
    @MockBean
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @MockBean
    private DynamoDbAsyncTable<Object> dynamoDbAsyncTable;

    @MockBean
    private F24Config f24Config;

    /**
     * Method under test: {@link F24FileRequestRepositoryImpl#getItem(String)}
     */
    @Test
    void testGetItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

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

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

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
    @Disabled
    void testPutItemIfAbsent() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

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

        F24FileRequestRepositoryImpl f24FileRequestRepository = new F24FileRequestRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

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
}

