package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

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

        InailRecord inailRecord = new InailRecord();
        inailRecord.setApplyCost(true);

        SocialSecurityRecord socialSecurityRecord = new SocialSecurityRecord();
        socialSecurityRecord.setApplyCost(true);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        socialSecuritySection.setSocSecRecords(List.of(socialSecurityRecord));
        socialSecuritySection.setRecords(List.of(inailRecord));


        F24Standard f24Standard = new F24Standard();
        f24Standard.setLocalTax(localTax);
        f24Standard.setTreasury(treasurySection);
        f24Standard.setInps(inpsSection);
        f24Standard.setRegion(regionSection);
        f24Standard.setSocialSecurity(socialSecuritySection);

        assertEquals(6, standardMetadataInspector
                .countMetadataApplyCost(new F24Metadata(f24Standard, new F24Simplified(), new F24Excise(), new F24Elid())));
    }


}

