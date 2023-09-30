package it.pagopa.pn.f24.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class F24FileStatusTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileStatus#valueOf(String)}
     *   <li>{@link F24FileStatus#getValue()}
     * </ul>
     */
    @Test
    public void testValueOf() {
        assertEquals("TO_PROCESS", F24FileStatus.valueOf("TO_PROCESS").getValue());
    }
}

