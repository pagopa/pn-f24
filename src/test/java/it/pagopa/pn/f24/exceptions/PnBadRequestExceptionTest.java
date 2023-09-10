package it.pagopa.pn.f24.exceptions;

import it.pagopa.pn.f24.exception.PnBadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PnBadRequestExceptionTest {

    @Test
    void constructorPnBadRequestException1() {
        PnBadRequestException pnBadRequestException = new PnBadRequestException("Title", "Message", "");
        Assertions.assertEquals("Title", pnBadRequestException.getMessage());
    }
}