package it.pagopa.pn.f24.exception;

public class EventBridgeSendException extends RuntimeException {
    public EventBridgeSendException(String message) {
        super(message);
    }
}
