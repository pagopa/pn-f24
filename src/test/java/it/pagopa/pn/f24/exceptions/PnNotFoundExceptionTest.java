package it.pagopa.pn.f24.exceptions;

import it.pagopa.pn.f24.exception.PnNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PnNotFoundExceptionTest {

    private PnNotFoundException pnNotFoundException;

    @Test
    void constructorPnNotFoundException1() {
        pnNotFoundException = new PnNotFoundException("Title", "Message", "");
        Assertions.assertEquals("Title", pnNotFoundException.getMessage());
    }

    @Test
    void constructorPnNotFoundException2() {
        pnNotFoundException = new PnNotFoundException("Title", "Message", "Error Code");
        Assertions.assertEquals("Title", pnNotFoundException.getMessage());
    }
}