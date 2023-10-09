package it.pagopa.pn.f24.exception;

public class PollingTimeOutException extends RuntimeException {
    public PollingTimeOutException(String message) {
        super(message);
    }
}
