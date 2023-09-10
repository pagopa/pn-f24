package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SimplifiedPaymentSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerSimplified;

import java.util.ArrayList;

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

        TaxPayerSimplified taxPayer = new TaxPayerSimplified();
        F24Simplified f24Simplified = new F24Simplified(taxPayer, new SimplifiedPaymentSection("42", new ArrayList<>()));

        assertEquals(1, simplifiedMetadataInspector
                .countMetadataApplyCost(new F24Metadata(new F24Standard(), f24Simplified, new F24Excise(), new F24Elid())));
    }

}

