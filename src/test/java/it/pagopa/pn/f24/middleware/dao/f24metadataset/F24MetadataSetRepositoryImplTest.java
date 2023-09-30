package it.pagopa.pn.f24.middleware.dao.f24metadataset;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.F24MetadataSetRepositoryImpl;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
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
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class F24MetadataSetRepositoryImplTest {
    @MockBean
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @MockBean
    private DynamoDbAsyncTable<Object> dynamoDbAsyncTable;

    @MockBean
    private F24Config f24Config;

    @Test
    void getItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24MetadataSetRepositoryImpl f24MetadataSetRepository = new F24MetadataSetRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);


        F24MetadataSetEntity f24MetadataSet = new F24MetadataSetEntity();
        f24MetadataSet.setSetId("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24MetadataSet);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24MetadataSetRepository.getItem("42"))
                .expectNextMatches(f24MetadataSet1 -> f24MetadataSet1.getSetId().equalsIgnoreCase("42"))
                .verifyComplete();
    }

    @Test
    void getItem2() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24MetadataSetRepositoryImpl f24MetadataSetRepository = new F24MetadataSetRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);


        F24MetadataSetEntity f24MetadataSet = new F24MetadataSetEntity();
        f24MetadataSet.setSetId("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24MetadataSet);
        when(dynamoDbAsyncTable.getItem((GetItemEnhancedRequest) any())).thenReturn(completableFuture);

        StepVerifier.create(f24MetadataSetRepository.getItem("42", true))
                .expectNextMatches(f24MetadataSet1 -> f24MetadataSet1.getSetId().equalsIgnoreCase("42"))
                .verifyComplete();
    }

    @Test
    @Disabled("To repair")
    void putItemIfAbsent() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24MetadataSetRepositoryImpl f24MetadataSetRepository = new F24MetadataSetRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> null);
        when(dynamoDbAsyncTable.putItem((PutItemEnhancedRequest<F24MetadataSetEntity>) any())).thenReturn(completableFuture);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("42");

        StepVerifier.create(f24MetadataSetRepository.putItemIfAbsent(f24MetadataSet))
                .expectComplete()
                .verify();
    }

    @Test
    void setF24MetadataSetStatusValidationEnded() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24MetadataSetRepositoryImpl f24MetadataSetRepository = new F24MetadataSetRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        F24MetadataSetEntity f24MetadataSetEntity = new F24MetadataSetEntity();
        f24MetadataSetEntity.setSetId("setId");
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24MetadataSetEntity);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        String setId = "setId";
        f24MetadataSet.setSetId(setId);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        StepVerifier.create(f24MetadataSetRepository.setF24MetadataSetStatusValidationEnded(f24MetadataSet))
                .expectNextMatches(f24MetadataSet1 -> f24MetadataSet1.getSetId().equalsIgnoreCase(setId))
                .expectComplete()
                .verify();
    }

    @Test
    void updateItem() {
        when(dynamoDbEnhancedAsyncClient.table(any(),any())).thenReturn(dynamoDbAsyncTable);

        F24MetadataSetRepositoryImpl f24MetadataSetRepository = new F24MetadataSetRepositoryImpl(dynamoDbEnhancedAsyncClient, f24Config);

        F24MetadataSetEntity f24MetadataSetEntity = new F24MetadataSetEntity();
        f24MetadataSetEntity.setSetId("42");

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeAsync(() -> f24MetadataSetEntity);
        when(dynamoDbAsyncTable.updateItem((UpdateItemEnhancedRequest<Object>) any())).thenReturn(completableFuture);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("42");
        StepVerifier.create(f24MetadataSetRepository.updateItem(f24MetadataSet))
                .expectNext()
                .expectComplete();
    }
}
