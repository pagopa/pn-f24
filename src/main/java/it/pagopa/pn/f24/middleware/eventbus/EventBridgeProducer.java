package it.pagopa.pn.f24.middleware.eventbus;

public interface EventBridgeProducer<T> {
    void sendEvent(T event);
}