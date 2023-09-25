package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

public class F24FileEntityTest {
    /**
     * Method under test: {@link F24FileEntity#canEqual(Object)}
     */
    @Test
    public void testCanEqual() {
        assertFalse((new F24FileEntity()).canEqual("Other"));
    }

    /**
     * Method under test: {@link F24FileEntity#canEqual(Object)}
     */
    @Test
    public void testCanEqual2() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        assertTrue(f24FileEntity.canEqual(new F24FileEntity()));
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileEntity#F24FileEntity()}
     *   <li>{@link F24FileEntity#setCreated(String)}
     *   <li>{@link F24FileEntity#setFileKey(String)}
     *   <li>{@link F24FileEntity#setPk(String)}
     *   <li>{@link F24FileEntity#setRequestId(String)}
     *   <li>{@link F24FileEntity#setSk(String)}
     *   <li>{@link F24FileEntity#setStatus(F24FileStatusEntity)}
     *   <li>{@link F24FileEntity#setTtl(Long)}
     *   <li>{@link F24FileEntity#setUpdated(LocalDateTime)}
     *   <li>{@link F24FileEntity#toString()}
     *   <li>{@link F24FileEntity#getCreated()}
     *   <li>{@link F24FileEntity#getFileKey()}
     *   <li>{@link F24FileEntity#getPk()}
     *   <li>{@link F24FileEntity#getRequestId()}
     *   <li>{@link F24FileEntity#getSk()}
     *   <li>{@link F24FileEntity#getStatus()}
     *   <li>{@link F24FileEntity#getTtl()}
     *   <li>{@link F24FileEntity#getUpdated()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        F24FileEntity actualF24FileEntity = new F24FileEntity();
        actualF24FileEntity.setCreated("Jan 1, 2020 8:00am GMT+0100");
        actualF24FileEntity.setFileKey("File Key");
        actualF24FileEntity.setPk("Pk");
        actualF24FileEntity.setRequestId("42");
        actualF24FileEntity.setSk("Sk");
        actualF24FileEntity.setStatus(F24FileStatusEntity.PROCESSING);
        actualF24FileEntity.setTtl(1L);
        LocalDateTime updated = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualF24FileEntity.setUpdated(updated);
        String actualToStringResult = actualF24FileEntity.toString();
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualF24FileEntity.getCreated());
        assertEquals("File Key", actualF24FileEntity.getFileKey());
        assertEquals("Pk", actualF24FileEntity.getPk());
        assertEquals("42", actualF24FileEntity.getRequestId());
        assertEquals("Sk", actualF24FileEntity.getSk());
        assertEquals(F24FileStatusEntity.PROCESSING, actualF24FileEntity.getStatus());
        assertEquals(1L, actualF24FileEntity.getTtl().longValue());
        assertSame(updated, actualF24FileEntity.getUpdated());
        assertEquals(
                "F24FileEntity(pk=Pk, created=Jan 1, 2020 8:00am GMT+0100, sk=Sk, requestId=42, status=F24FileStatusEntity"
                        + ".PROCESSING(value=PROCESSING), fileKey=File Key, ttl=1, updated=1970-01-01T00:00)",
                actualToStringResult);
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileEntity#F24FileEntity(String, String, String, String, F24FileStatusEntity, String, Long, LocalDateTime)}
     *   <li>{@link F24FileEntity#setCreated(String)}
     *   <li>{@link F24FileEntity#setFileKey(String)}
     *   <li>{@link F24FileEntity#setPk(String)}
     *   <li>{@link F24FileEntity#setRequestId(String)}
     *   <li>{@link F24FileEntity#setSk(String)}
     *   <li>{@link F24FileEntity#setStatus(F24FileStatusEntity)}
     *   <li>{@link F24FileEntity#setTtl(Long)}
     *   <li>{@link F24FileEntity#setUpdated(LocalDateTime)}
     *   <li>{@link F24FileEntity#toString()}
     *   <li>{@link F24FileEntity#getCreated()}
     *   <li>{@link F24FileEntity#getFileKey()}
     *   <li>{@link F24FileEntity#getPk()}
     *   <li>{@link F24FileEntity#getRequestId()}
     *   <li>{@link F24FileEntity#getSk()}
     *   <li>{@link F24FileEntity#getStatus()}
     *   <li>{@link F24FileEntity#getTtl()}
     *   <li>{@link F24FileEntity#getUpdated()}
     * </ul>
     */
    @Test
    public void testConstructor2() {
        F24FileEntity actualF24FileEntity = new F24FileEntity("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatusEntity.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        actualF24FileEntity.setCreated("Jan 1, 2020 8:00am GMT+0100");
        actualF24FileEntity.setFileKey("File Key");
        actualF24FileEntity.setPk("Pk");
        actualF24FileEntity.setRequestId("42");
        actualF24FileEntity.setSk("Sk");
        actualF24FileEntity.setStatus(F24FileStatusEntity.PROCESSING);
        actualF24FileEntity.setTtl(1L);
        LocalDateTime updated = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualF24FileEntity.setUpdated(updated);
        String actualToStringResult = actualF24FileEntity.toString();
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualF24FileEntity.getCreated());
        assertEquals("File Key", actualF24FileEntity.getFileKey());
        assertEquals("Pk", actualF24FileEntity.getPk());
        assertEquals("42", actualF24FileEntity.getRequestId());
        assertEquals("Sk", actualF24FileEntity.getSk());
        assertEquals(F24FileStatusEntity.PROCESSING, actualF24FileEntity.getStatus());
        assertEquals(1L, actualF24FileEntity.getTtl().longValue());
        assertSame(updated, actualF24FileEntity.getUpdated());
        assertEquals(
                "F24FileEntity(pk=Pk, created=Jan 1, 2020 8:00am GMT+0100, sk=Sk, requestId=42, status=F24FileStatusEntity"
                        + ".PROCESSING(value=PROCESSING), fileKey=File Key, ttl=1, updated=1970-01-01T00:00)",
                actualToStringResult);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals() {
        assertNotEquals(new F24FileEntity(), null);
        assertNotEquals(new F24FileEntity(), "Different type to F24FileEntity");
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileEntity#equals(Object)}
     *   <li>{@link F24FileEntity#hashCode()}
     * </ul>
     */
    @Test
    public void testEquals2() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        assertEquals(f24FileEntity, f24FileEntity);
        int expectedHashCodeResult = f24FileEntity.hashCode();
        assertEquals(expectedHashCodeResult, f24FileEntity.hashCode());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileEntity#equals(Object)}
     *   <li>{@link F24FileEntity#hashCode()}
     * </ul>
     */
    @Test
    public void testEquals3() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        F24FileEntity f24FileEntity2 = new F24FileEntity();
        assertEquals(f24FileEntity, f24FileEntity2);
        int expectedHashCodeResult = f24FileEntity.hashCode();
        assertEquals(expectedHashCodeResult, f24FileEntity2.hashCode());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals4() {
        F24FileEntity f24FileEntity = new F24FileEntity("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatusEntity.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals5() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        assertNotEquals(f24FileEntity, new F24FileEntity("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatusEntity.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals6() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setPk("Pk");
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals7() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setCreated("Jan 1, 2020 8:00am GMT+0100");
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals8() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setSk("Sk");
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals9() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setRequestId("42");
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals10() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setStatus(F24FileStatusEntity.PROCESSING);
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals11() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setFileKey("File Key");
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals12() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setUpdated(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24FileEntity, new F24FileEntity());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24FileEntity#equals(Object)}
     *   <li>{@link F24FileEntity#hashCode()}
     * </ul>
     */
    @Test
    public void testEquals13() {
        F24FileEntity f24FileEntity = new F24FileEntity("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatusEntity.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        F24FileEntity f24FileEntity2 = new F24FileEntity("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatusEntity.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(f24FileEntity, f24FileEntity2);
        int expectedHashCodeResult = f24FileEntity.hashCode();
        assertEquals(expectedHashCodeResult, f24FileEntity2.hashCode());
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals14() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setPk("Pk");
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals15() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setCreated("Jan 1, 2020 8:00am GMT+0100");
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals16() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setSk("Sk");
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals17() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setRequestId("42");
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals18() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setStatus(F24FileStatusEntity.PROCESSING);
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals19() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setFileKey("File Key");
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }

    /**
     * Method under test: {@link F24FileEntity#equals(Object)}
     */
    @Test
    public void testEquals20() {
        F24FileEntity f24FileEntity = new F24FileEntity();

        F24FileEntity f24FileEntity2 = new F24FileEntity();
        f24FileEntity2.setUpdated(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24FileEntity, f24FileEntity2);
    }
}

