package it.pagopa.pn.f24.middleware.eventbus.abstraction;

public interface Producer<T> {
    void sendEvent(T event);
}
