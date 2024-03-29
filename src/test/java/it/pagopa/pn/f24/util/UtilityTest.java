package it.pagopa.pn.f24.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.commons.exceptions.PnInternalException;
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
     * Method under test: {@link Utility#getF24TypeFromMetadata(F24Metadata)}
     */
    @Test
    void testGetF24TypeFromMetadata() {
        F24Metadata f24Metadata = new F24Metadata();
        assertThrows(PnInternalException.class, () -> Utility.getF24TypeFromMetadata(f24Metadata));
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

    @Test
    void testSplitListInBatches() {
        List<String> listToSplit = List.of("one", "two", "three", "four");
        assertEquals(2, Utility.splitListInBatches(listToSplit, 2).size());
    }

    @Test
    void testSplitListInBatchesWhenSourceListIsNull() {
        assertEquals(0, Utility.splitListInBatches(null, 2).size());
    }
}

