package it.pagopa.pn.f24.exception;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Getter;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_MESSAGE_F24_FILE_WITH_FILEKEY_NOT_FOUND;

@Getter
public class PnFileNotFoundException extends PnInternalException {
    public PnFileNotFoundException(String message, Throwable cause) {
        super(message, ERROR_MESSAGE_F24_FILE_WITH_FILEKEY_NOT_FOUND, cause);
    }
}