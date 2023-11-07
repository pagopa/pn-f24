package it.pagopa.pn.f24.it.mockbean;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import java.util.concurrent.CompletableFuture;
@Slf4j
public class EventBridgeAsyncClientMock implements EventBridgeAsyncClient {
    @Override
    public String serviceName() {
        return null;
    }

    @Override
    public void close() { }

    @Override
    public CompletableFuture<PutEventsResponse> putEvents(PutEventsRequest putEventsRequest) {
        PutEventsRequestEntry requestEntry = putEventsRequest.entries().get(0);
        log.info("[TEST] invoked putEvents for eventBus: {} with detailType: {} and detail: {}", requestEntry.eventBusName(), requestEntry.detailType(), requestEntry.detail());

        return CompletableFuture.completedFuture(PutEventsResponse.builder().failedEntryCount(0).build());
    }
}
