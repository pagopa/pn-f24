package it.pagopa.pn.f24.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PnF24ExceptionCodesTest {
    @Test
    void checkAll() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("PN_F24_PATH_TOKENS_NOT_ALLOWED", PnF24ExceptionCodes.ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED),
                () -> Assertions.assertEquals("PN_F24_EVENTTYPENOTSUPPORTED", PnF24ExceptionCodes.ERROR_CODE_F24_EVENTTYPENOTSUPPORTED)
        );
    }
}