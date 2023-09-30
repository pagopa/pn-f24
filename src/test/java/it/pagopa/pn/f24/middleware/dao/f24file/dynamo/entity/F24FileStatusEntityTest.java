package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class F24FileStatusEntityTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileStatusEntity#valueOf(String)}
     *   <li>{@link F24FileStatusEntity#getValue()}
     * </ul>
     */
    @Test
    public void testValueOf() {
        assertEquals("TO_PROCESS", F24FileStatusEntity.valueOf("TO_PROCESS").getValue());
    }
}

