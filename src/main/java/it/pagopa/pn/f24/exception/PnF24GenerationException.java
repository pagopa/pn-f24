package it.pagopa.pn.f24.exception;

import it.pagopa.pn.commons.exceptions.PnInternalException;

public class PnF24GenerationException extends PnInternalException {
    public PnF24GenerationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public PnF24GenerationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
