package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class ExciseMetadataInspectorTest {
    /**
     * Method under test: {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        assertEquals(0, exciseMetadataInspector.countMetadataApplyCost(new F24Metadata()));
    }

    /**
     * Method under test: {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
     */
    @Test
    void testCountMetadataApplyCost2() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);

        LocalTaxSection localTax = new LocalTaxSection();
        localTax.setRecords(List.of(localTaxRecord));

        Tax tax = new Tax();
        tax.setApplyCost(true);

        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(List.of(tax));

        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(true);

        InpsSection inpsSection = new InpsSection();
        inpsSection.setRecords(List.of(inpsRecord));

        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(true);

        RegionSection regionSection = new RegionSection();
        regionSection.setRecords(List.of(regionRecord));

        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setApplyCost(true);

        ExciseSection exciseSection = new ExciseSection();
        exciseSection.setRecords(List.of(exciseTax));

        F24Excise f24Excise = new F24Excise();
        f24Excise.setLocalTax(localTax);
        f24Excise.setTreasury(treasurySection);
        f24Excise.setInps(inpsSection);
        f24Excise.setRegion(regionSection);
        f24Excise.setExcise(exciseSection);

        assertEquals(5, exciseMetadataInspector
                .countMetadataApplyCost(new F24Metadata(new F24Standard(), new F24Simplified(), f24Excise, new F24Elid())));
    }


}

