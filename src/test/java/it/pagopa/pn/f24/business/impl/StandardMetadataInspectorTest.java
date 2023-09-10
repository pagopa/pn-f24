package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection;

import java.util.List;

import org.junit.jupiter.api.Test;

class StandardMetadataInspectorTest {
    /**
     * Method under test: {@link StandardMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        assertEquals(0, standardMetadataInspector.countMetadataApplyCost(new F24Metadata()));
    }



    /**
     * Method under test: {@link StandardMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost2() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        LocalTaxSection localTax = new LocalTaxSection();
        localTax.setRecords(List.of(localTaxRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setLocalTax(localTax);
        assertEquals(1, standardMetadataInspector
                .countMetadataApplyCost(new F24Metadata(f24Standard, new F24Simplified(), new F24Excise(), new F24Elid())));
    }


}

