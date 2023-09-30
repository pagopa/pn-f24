package it.pagopa.pn.f24.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PnConflictExceptionTest {

    @Test
    void constructorPnConflictException1() {
        PnConflictException pnConflictException = new PnConflictException("Title", "Message", "");
        Assertions.assertEquals("Title", pnConflictException.getMessage());
    }
}