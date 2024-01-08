package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SimplifiedPaymentRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SimplifiedPaymentSection;
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

    @Test
    void testCheckApplyCostOk() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataSimplifiedWithRecord(true);
        assertEquals(ApplyCostValidation.OK, simplifiedMetadataInspector.checkApplyCost(f24Metadata, true));
    }


    @Test
    void testCheckApplyCostFailWithRequiredButNotGiven() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataSimplifiedWithRecord(false);
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, simplifiedMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithInvalidApplyCost() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataSimplifiedWithRecord(true, 100);
        assertEquals(ApplyCostValidation.INVALID_APPLY_COST_GIVEN, simplifiedMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGivenAndMetadataIsNull() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, simplifiedMetadataInspector.checkApplyCost(f24Metadata, true));
    }
    @Test
    void testGetTotalAmount() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();

        F24Metadata f24Metadata = getF24MetadataSimplifiedWithRecord(true, 100);

        assertEquals(0, simplifiedMetadataInspector.getTotalAmount(f24Metadata));
    }

    @Test
    void testAddCostToDebit() {
        SimplifiedMetadataInspector simplifiedMetadataInspector = new SimplifiedMetadataInspector();

        F24Metadata f24Metadata = getF24MetadataSimplifiedWithRecord(true);

        simplifiedMetadataInspector.addCostToDebit(f24Metadata,5);

        assertEquals(5,  f24Metadata.getF24Simplified().getPayments().getRecords().get(0).getDebit());
    }

    private F24Metadata getF24MetadataSimplifiedWithRecord(boolean applyCost) {
        return getF24MetadataSimplifiedWithRecord(applyCost, null);
    }

    private F24Metadata getF24MetadataSimplifiedWithRecord(boolean applyCost, Integer credit) {
        SimplifiedPaymentRecord simplifiedPaymentRecord = new SimplifiedPaymentRecord();
        simplifiedPaymentRecord.setApplyCost(applyCost);
        simplifiedPaymentRecord.setCredit(credit);
        simplifiedPaymentRecord.setDebit(0);

        SimplifiedPaymentSection simplifiedPaymentSection = new SimplifiedPaymentSection();
        simplifiedPaymentSection.setRecords(List.of(simplifiedPaymentRecord));

        F24Simplified f24Simplified = new F24Simplified();
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Simplified(f24Simplified);
        f24Simplified.setPayments(simplifiedPaymentSection);

        return f24Metadata;
    }

}

