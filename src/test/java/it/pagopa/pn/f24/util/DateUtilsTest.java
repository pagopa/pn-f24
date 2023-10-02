package it.pagopa.pn.f24.util;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    /**
     * Method under test: {@link DateUtils#formatInstantToString(Instant)}
     */
    @Test
    public void testFormatInstantToString() {
        assertEquals("1970-01-01T00:00:00.000Z",
                DateUtils.formatInstantToString(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }
}

