package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class ElidMetadataInspectorTest {
    /**
     * Method under test: {@link ElidMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();
        assertEquals(0, elidMetadataInspector.countMetadataApplyCost(new F24Metadata()));
    }


    /**
     * Method under test: {@link ElidMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost2() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();

        TreasuryRecord treasuryRecord = new TreasuryRecord();
        treasuryRecord.setApplyCost(true);
        TreasuryAndOtherSection treasuryAndOtherSection = new TreasuryAndOtherSection();
        treasuryAndOtherSection.setRecords(List.of(treasuryRecord));

        F24Elid f24Elid = new F24Elid();
        f24Elid.setTreasury(treasuryAndOtherSection);

        assertEquals(1, elidMetadataInspector
                .countMetadataApplyCost(new F24Metadata(new F24Standard(), new F24Simplified(), new F24Excise(), f24Elid)));
    }

    @Test
    void testCheckApplyCostOk() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataElidWithRecord(true);
        assertEquals(ApplyCostValidation.OK, elidMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testGetTotalAmount() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataElidWithRecord(true);
        assertEquals(0, elidMetadataInspector.getTotalAmount(f24Metadata));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGiven() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataElidWithRecord(false);
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, elidMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGivenAndMetadataIsNull() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, elidMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testAddCostToDebit() {
        ElidMetadataInspector elidMetadataInspector = new ElidMetadataInspector();

        F24Metadata f24Metadata = getF24MetadataElidWithRecord(true);

        elidMetadataInspector.addCostToDebit(f24Metadata,5);

        assertEquals(5, f24Metadata.getF24Elid().getTreasury().getRecords().get(0).getDebit());
    }

    private F24Metadata getF24MetadataElidWithRecord(boolean applyCost) {
        TreasuryRecord treasuryRecord = new TreasuryRecord();
        treasuryRecord.setApplyCost(applyCost);
        treasuryRecord.setDebit(0);

        TreasuryAndOtherSection treasuryAndOtherSection = new TreasuryAndOtherSection();
        treasuryAndOtherSection.setRecords(List.of(treasuryRecord));

        F24Elid f24Elid = new F24Elid();
        f24Elid.setTreasury(treasuryAndOtherSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Elid(f24Elid);
        return f24Metadata;
    }

}

