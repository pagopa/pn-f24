package it.pagopa.pn.f24.it.mockbean;

import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;

public class EventBridgeAsyncClientMock implements EventBridgeAsyncClient {
    @Override
    public String serviceName() {
        return null;
    }

    @Override
    public void close() {

    }
}
