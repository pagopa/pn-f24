package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class SimplifiedMetadataInspectorTest {
    /**
     * Method under test: {@link SimplifiedMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();
        assertEquals(0, simplifiedMetadataInspector.countMetadataApplyCost(new F24Metadata()));
    }

    /**
     * Method under test: {@link SimplifiedMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost2() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();

        SimplifiedPaymentRecord record = new SimplifiedPaymentRecord();
        record.setApplyCost(true);
        F24Simplified f24Simplified = new F24Simplified(new TaxPayerSimplified(), new SimplifiedPaymentSection("42", List.of(record)));

        assertEquals(1, simplifiedMetadataInspector
                .countMetadataApplyCost(new F24Metadata(new F24Standard(), f24Simplified, new F24Excise(), new F24Elid())));
    }

}

