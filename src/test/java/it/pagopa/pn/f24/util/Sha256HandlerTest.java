package it.pagopa.pn.f24.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class Sha256HandlerTest {
    /**
     * Method under test: {@link Sha256Handler#computeSha256(String)}
     */
    @Test
    void testComputeSha256() {
        assertEquals("P+BNFFNADJvVS7dsN5mvZUN11CskHA4Ab6emI0/hMeM=",
                Sha256Handler.computeSha256("Not all who wander are lost"));
        assertEquals("w0q2q7eyu1lbwlw7OIyHL9HVdYGaj1XMaJUQKF4hI4U=",
                Sha256Handler.computeSha256("AAAAAAAA".getBytes(StandardCharsets.UTF_8)));
    }
}

