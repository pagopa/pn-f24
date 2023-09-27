/*
package it.pagopa.pn.f24.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

public class F24FileTest {
    */
/**
     * Method under test: {@link F24File#canEqual(Object)}
     *//*

    @Test
    public void testCanEqual() {
        assertFalse((new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay())).canEqual("Other"));
    }

    */
/**
     * Method under test: {@link F24File#canEqual(Object)}
     *//*

    @Test
    public void testCanEqual2() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertTrue(f24File.canEqual(new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay())));
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#F24File(String, String, String, String, F24FileStatus, String, Long, LocalDateTime)}
     *   <li>{@link F24File#setCreated(String)}
     *   <li>{@link F24File#setFileKey(String)}
     *   <li>{@link F24File#setPk(String)}
     *   <li>{@link F24File#setRequestId(String)}
     *   <li>{@link F24File#setSk(String)}
     *   <li>{@link F24File#setStatus(F24FileStatus)}
     *   <li>{@link F24File#setTtl(Long)}
     *   <li>{@link F24File#setUpdated(LocalDateTime)}
     *   <li>{@link F24File#toString()}
     *   <li>{@link F24File#getCreated()}
     *   <li>{@link F24File#getFileKey()}
     *   <li>{@link F24File#getPk()}
     *   <li>{@link F24File#getRequestId()}
     *   <li>{@link F24File#getSk()}
     *   <li>{@link F24File#getStatus()}
     *   <li>{@link F24File#getTtl()}
     *   <li>{@link F24File#getUpdated()}
     * </ul>
     *//*

    @Test
    public void testConstructor() {
        F24File actualF24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        actualF24File.setCreated("Jan 1, 2020 8:00am GMT+0100");
        actualF24File.setFileKey("File Key");
        actualF24File.setPk("Pk");
        actualF24File.setRequestId("42");
        actualF24File.setSk("Sk");
        actualF24File.setStatus(F24FileStatus.PROCESSING);
        actualF24File.setTtl(1L);
        LocalDateTime updated = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualF24File.setUpdated(updated);
        String actualToStringResult = actualF24File.toString();
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualF24File.getCreated());
        assertEquals("File Key", actualF24File.getFileKey());
        assertEquals("Pk", actualF24File.getPk());
        assertEquals("42", actualF24File.getRequestId());
        assertEquals("Sk", actualF24File.getSk());
        assertEquals(F24FileStatus.PROCESSING, actualF24File.getStatus());
        assertEquals(1L, actualF24File.getTtl().longValue());
        assertSame(updated, actualF24File.getUpdated());
        assertEquals(
                "F24File(pk=Pk, created=Jan 1, 2020 8:00am GMT+0100, sk=Sk, requestId=42, status=F24FileStatus.PROCESSING"
                        + "(value=PROCESSING), fileKey=File Key, ttl=1, updated=1970-01-01T00:00)",
                actualToStringResult);
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals() {
        assertNotEquals(new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING, "File Key",
                1L, LocalDate.of(1970, 1, 1).atStartOfDay()), null);
        assertNotEquals(new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()), "Different type to F24File");
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#equals(Object)}
     *   <li>{@link F24File#hashCode()}
     * </ul>
     *//*

    @Test
    public void testEquals2() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertEquals(f24File, f24File);
        int expectedHashCodeResult = f24File.hashCode();
        assertEquals(expectedHashCodeResult, f24File.hashCode());
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#equals(Object)}
     *   <li>{@link F24File#hashCode()}
     * </ul>
     *//*

    @Test
    public void testEquals3() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        F24File f24File2 = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(f24File, f24File2);
        int expectedHashCodeResult = f24File.hashCode();
        assertEquals(expectedHashCodeResult, f24File2.hashCode());
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals4() {
        F24File f24File = new F24File("Jan 1, 2020 8:00am GMT+0100", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42",
                F24FileStatus.PROCESSING, "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals5() {
        F24File f24File = new F24File(null, "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals6() {
        F24File f24File = new F24File("Pk", "Pk", "Sk", "42", F24FileStatus.PROCESSING, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals7() {
        F24File f24File = new F24File("Pk", null, "Sk", "42", F24FileStatus.PROCESSING, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals8() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Pk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals9() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", null, "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals10() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "Pk", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals11() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", null, F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals12() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", null, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals13() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.GENERATED,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals14() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING, "Pk", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals15() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING, null, 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals16() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 3L, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals17() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", null, LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Method under test: {@link F24File#equals(Object)}
     *//*

    @Test
    public void testEquals18() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.now().atStartOfDay());
        assertNotEquals(f24File, new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#equals(Object)}
     *   <li>{@link F24File#hashCode()}
     * </ul>
     *//*

    @Test
    public void testEquals19() {
        F24File f24File = new F24File(null, "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        F24File f24File2 = new F24File(null, "Jan 1, 2020 8:00am GMT+0100", "Sk", "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(f24File, f24File2);
        int expectedHashCodeResult = f24File.hashCode();
        assertEquals(expectedHashCodeResult, f24File2.hashCode());
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#equals(Object)}
     *   <li>{@link F24File#hashCode()}
     * </ul>
     *//*

    @Test
    public void testEquals20() {
        F24File f24File = new F24File("Pk", null, "Sk", "42", F24FileStatus.PROCESSING, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        F24File f24File2 = new F24File("Pk", null, "Sk", "42", F24FileStatus.PROCESSING, "File Key", 1L,
                LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(f24File, f24File2);
        int expectedHashCodeResult = f24File.hashCode();
        assertEquals(expectedHashCodeResult, f24File2.hashCode());
    }

    */
/**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link F24File#equals(Object)}
     *   <li>{@link F24File#hashCode()}
     * </ul>
     *//*

    @Test
    public void testEquals21() {
        F24File f24File = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", null, "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());
        F24File f24File2 = new F24File("Pk", "Jan 1, 2020 8:00am GMT+0100", null, "42", F24FileStatus.PROCESSING,
                "File Key", 1L, LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(f24File, f24File2);
        int expectedHashCodeResult = f24File.hashCode();
        assertEquals(expectedHashCodeResult, f24File2.hashCode());
    }
}

*/
