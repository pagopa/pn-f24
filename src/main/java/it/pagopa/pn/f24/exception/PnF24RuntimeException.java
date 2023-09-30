package it.pagopa.pn.f24.exception;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import org.springframework.http.HttpStatus;

public class PnF24RuntimeException extends PnRuntimeException {

    public PnF24RuntimeException(String message, String description, String errorcode) {
        super(message, description, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorcode, null, description);
    }

}
