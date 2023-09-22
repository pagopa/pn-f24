package it.pagopa.pn.f24.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class UtilityTest {
    /**
     * Method under test: {@link Utility#getIndexOfByPredicate(List, Predicate)}
     */
    @Test
    void testGetIndexOfByPredicate() {
        assertEquals(Utility.INDEX_NOT_FOUND,
                Utility.getIndexOfByPredicate(new ArrayList<>(), (Predicate<Integer>) mock(Predicate.class)).intValue());
    }

    /**
     * Method under test: {@link Utility#getIndexOfByPredicate(List, Predicate)}
     */
    @Test
    void testGetIndexOfByPredicate2() {
        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add("42");
        Predicate<Integer> predicate = (Predicate<Integer>) mock(Predicate.class);
        when(predicate.test(any())).thenReturn(true);
        assertEquals(0, Utility.getIndexOfByPredicate(objectList, predicate).intValue());
        verify(predicate).test(any());
    }

    /**
     * Method under test: {@link Utility#countElementsByPredicate(List, Predicate)}
     */
    @Test
    void testCountElementsByPredicate() {
        assertEquals(0,
                Utility.countElementsByPredicate(new ArrayList<>(), (Predicate<Object>) mock(Predicate.class)).intValue());
    }

    /**
     * Method under test: {@link Utility#countElementsByPredicate(List, Predicate)}
     */
    @Test
    void testCountElementsByPredicate2() {
        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add("42");
        Predicate<Object> predicate = (Predicate<Object>) mock(Predicate.class);
        when(predicate.test(any())).thenReturn(true);
        assertEquals(1, Utility.countElementsByPredicate(objectList, predicate).intValue());
        verify(predicate).test(any());
    }

    /**
     * Method under test: {@link Utility#objectToJsonString(Object)}
     */
    @Test
    void testObjectToJsonString() {
        assertEquals("\"Object\"", Utility.objectToJsonString("Object"));
        assertEquals("\"42\"", Utility.objectToJsonString("42"));
        assertEquals("42", Utility.objectToJsonString(42));
    }

    /**
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata() {
        assertThrows(RuntimeException.class, () -> Utility.getF24TypeFromMetadata(new F24Metadata()));
    }


    /**
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata2() {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());
        assertEquals(F24Type.F24_STANDARD, Utility.getF24TypeFromMetadata(f24Metadata));
    }

    /**
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata3() {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Simplified(new F24Simplified());
        assertEquals(F24Type.F24_SIMPLIFIED, Utility.getF24TypeFromMetadata(f24Metadata));
    }

    /**
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata4() {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(new F24Excise());
        assertEquals(F24Type.F24_EXCISE, Utility.getF24TypeFromMetadata(f24Metadata));
    }

    /**
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata5() {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Elid(new F24Elid());
        assertEquals(F24Type.F24_ELID, Utility.getF24TypeFromMetadata(f24Metadata));
    }
}

