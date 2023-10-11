package it.pagopa.pn.f24.business.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExciseMetadataInspectorTest {
    /**
     * Method under test: {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        F24Excise f24Excise = F24Excise.builder().build();

        TreasurySection treasurySection = new TreasurySection();
        Tax tax = new Tax();
        tax.setApplyCost(true);
        treasurySection.setRecords(List.of(tax));
        f24Excise.setTreasury(treasurySection);

        InpsSection inpsSection = new InpsSection();
        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(true);
        inpsSection.setRecords(List.of(inpsRecord));
        f24Excise.setInps(inpsSection);

        RegionSection regionSection = new RegionSection();
        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(true);
        regionSection.setRecords(List.of(regionRecord));
        f24Excise.setRegion(regionSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        localTaxSection.setRecords(List.of(localTaxRecord));
        f24Excise.setLocalTax(localTaxSection);

        ExciseSection exciseSection = new ExciseSection();
        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setApplyCost(true);
        exciseSection.setRecords(List.of(exciseTax));
        f24Excise.setExcise(exciseSection);

        f24Metadata.setF24Excise(f24Excise);
        assertEquals(5, exciseMetadataInspector.countMetadataApplyCost(f24Metadata));
    }

    @Test
    void testCountMetadataApplyCostNull() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();

        assertEquals(0, exciseMetadataInspector.countMetadataApplyCost(f24Metadata));
    }

    @Test
    void testCheckApplyCostOk() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataExciseValidWithAllApplyCostFalse();
        f24Metadata.getF24Excise().getTreasury().getRecords().get(0).setApplyCost(true);
        assertEquals(ApplyCostValidation.OK, exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }


    @Test
    void testCheckApplyCostFailWithRequiredButNotGiven() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataExciseValidWithAllApplyCostFalse();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithInvalidApplyCost() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataExciseValidWithAllApplyCostFalse();
        f24Metadata.getF24Excise().getTreasury().getRecords().get(0).setApplyCost(true);
        f24Metadata.getF24Excise().getTreasury().getRecords().get(0).setCredit(100);
        assertEquals(ApplyCostValidation.INVALID_APPLY_COST_GIVEN, exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGivenAndMetadataIsNull() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN, exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    /**
     * Method under test: {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testAddCostToDebit() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        Tax tax = new Tax();
        tax.setApplyCost(true);
        tax.setDebit(0);

        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(List.of(tax));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setTreasury(treasurySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(f24Excise);

        exciseMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, f24Metadata.getF24Excise().getTreasury().getRecords().get(0).getDebit());
    }

    @Test
    void testAddCostToDebit1() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(true);
        inpsRecord.setDebit(0);

        InpsSection inps = new InpsSection();
        inps.setRecords(List.of(inpsRecord));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setInps(inps);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(f24Excise);

        exciseMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, f24Metadata.getF24Excise().getInps().getRecords().get(0).getDebit());
    }

    @Test
    void testAddCostToDebit2() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(true);
        regionRecord.setDebit(0);

        RegionSection regionSection = new RegionSection();
        regionSection.setRecords(List.of(regionRecord));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setRegion(regionSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(f24Excise);

        exciseMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, f24Metadata.getF24Excise().getRegion().getRecords().get(0).getDebit());
    }

    @Test
    void testAddCostToDebit3() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        localTaxRecord.setDebit(0);

        LocalTaxSection localTax = new LocalTaxSection();
        localTax.setRecords(List.of(localTaxRecord));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setLocalTax(localTax);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(f24Excise);

        exciseMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, f24Metadata.getF24Excise().getLocalTax().getRecords().get(0).getDebit());
    }

    @Test
    void testAddCostToDebit4() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setApplyCost(true);
        exciseTax.setDebit(0);

        ExciseSection exciseSection = new ExciseSection();
        exciseSection.setRecords(List.of(exciseTax));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setExcise(exciseSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Excise(f24Excise);

        exciseMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, f24Metadata.getF24Excise().getExcise().getRecords().get(0).getDebit());
    }

    private F24Metadata getF24MetadataExciseValidWithAllApplyCostFalse() {
        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setApplyCost(false);
        exciseTax.setDebit(0);

        ExciseSection exciseSection = new ExciseSection();
        List<ExciseTax> exciseRecords = new ArrayList<>();
        exciseRecords.add(exciseTax);
        exciseSection.setRecords(exciseRecords);

        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(false);
        inpsRecord.setDebit(0);

        InpsSection inpsSection = new InpsSection();
        List<InpsRecord> inpsRecords = new ArrayList<>();
        inpsSection.setRecords(inpsRecords);

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(false);
        localTaxRecord.setDebit(0);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        List<LocalTaxRecord> localTaxRecords = new ArrayList<>();
        localTaxRecords.add(localTaxRecord);
        localTaxSection.setRecords(localTaxRecords);

        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(false);
        regionRecord.setDebit(0);

        RegionSection regionSection = new RegionSection();
        List<RegionRecord> regionRecords = new ArrayList<>();
        regionRecords.add(regionRecord);
        regionSection.setRecords(regionRecords);

        Tax tax = new Tax();
        tax.setApplyCost(false);
        tax.setDebit(0);
        TreasurySection treasurySection = new TreasurySection();
        List<Tax> taxes = new ArrayList<>();
        taxes.add(tax);
        treasurySection.setRecords(taxes);

        F24Excise f24Excise = new F24Excise();
        f24Excise.setExcise(exciseSection);
        f24Excise.setInps(inpsSection);
        f24Excise.setLocalTax(localTaxSection);
        f24Excise.setRegion(regionSection);
        f24Excise.setTreasury(treasurySection);

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Excise(f24Excise);

        return f24Metadata;
    }
}

