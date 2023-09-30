package it.pagopa.pn.f24.exception;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import org.springframework.http.HttpStatus;

public class PnConflictException extends PnRuntimeException {
    public PnConflictException(String message, String description, String errorCode) {
        super(message, description, HttpStatus.CONFLICT.value(), errorCode, null, description);
    }
}
