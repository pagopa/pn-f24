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
        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();

        TreasurySection treasurySection = new TreasurySection();
        Tax tax = new Tax();
        tax.setApplyCost(true);
        treasurySection.setRecords(List.of(tax));
        f24Standard.setTreasury(treasurySection);

        InpsSection inpsSection = new InpsSection();
        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(true);
        inpsSection.setRecords(List.of(inpsRecord));
        f24Standard.setInps(inpsSection);

        RegionSection regionSection = new RegionSection();
        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(true);
        regionSection.setRecords(List.of(regionRecord));
        f24Standard.setRegion(regionSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        localTaxSection.setRecords(List.of(localTaxRecord));
        f24Standard.setLocalTax(localTaxSection);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        InailRecord inailRecord = new InailRecord();
        inailRecord.setApplyCost(true);
        socialSecuritySection.setRecords(List.of(inailRecord));
        f24Standard.setSocialSecurity(socialSecuritySection);

        SocialSecurityRecord socialSecurityRecord = new SocialSecurityRecord();
        socialSecurityRecord.setApplyCost(true);
        socialSecuritySection.setSocSecRecords(List.of(socialSecurityRecord));
        f24Standard.setSocialSecurity(socialSecuritySection);

        f24Metadata.setF24Standard(f24Standard);

        assertEquals(6, standardMetadataInspector.countMetadataApplyCost(f24Metadata));
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

    @Test
    void testAddCostToDebit() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        Tax tax = new Tax();
        tax.setApplyCost(true);
        tax.setDebit(0);

        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(List.of(tax));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setTreasury(treasurySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getTreasury().getRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit1() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(true);
        inpsRecord.setDebit(0);

        InpsSection inpsSection = new InpsSection();
        inpsSection.setRecords(List.of(inpsRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setInps(inpsSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getInps().getRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit2() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(true);
        regionRecord.setDebit(0);

        RegionSection regionSection = new RegionSection();
        regionSection.setRecords(List.of(regionRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setRegion(regionSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getRegion().getRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit3() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        localTaxRecord.setDebit(0);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        localTaxSection.setRecords(List.of(localTaxRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setLocalTax(localTaxSection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getLocalTax().getRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit4() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        InailRecord inailRecord = new InailRecord();
        inailRecord.setApplyCost(true);
        inailRecord.setDebit(0);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        socialSecuritySection.setRecords(List.of(inailRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setSocialSecurity(socialSecuritySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getSocialSecurity().getRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit5() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        SocialSecurityRecord socialSecurityRecord = new SocialSecurityRecord();
        socialSecurityRecord.setApplyCost(true);
        socialSecurityRecord.setDebit(0);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        socialSecuritySection.setSocSecRecords(List.of(socialSecurityRecord));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setSocialSecurity(socialSecuritySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.f24Standard(f24Standard);

        standardMetadataInspector.addCostToDebit(f24Metadata, 5);

        assertEquals(5, Integer.parseInt(f24Metadata.getF24Standard().getSocialSecurity().getSocSecRecords().get(0).getDebit().toString()));
    }

    @Test
    void testAddCostToDebit6() {

        F24Metadata f24Metadata = new F24Metadata();
        F24Excise f24Excise = new F24Excise();


        Tax tax = new Tax();
        tax.setApplyCost(false);
        tax.setDebit(0);
        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(List.of(tax));
        f24Excise.setTreasury(treasurySection);
        f24Metadata.f24Excise(f24Excise);

        assertEquals(0, Integer.parseInt(f24Metadata.getF24Excise().getTreasury().getRecords().get(0).getDebit().toString()));

        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setApplyCost(false);
        inpsRecord.setDebit(0);
        InpsSection inps = new InpsSection();
        inps.setRecords(List.of(inpsRecord));
        f24Excise.setInps(inps);
        f24Metadata.f24Excise(f24Excise);

        assertEquals(0, Integer.parseInt(f24Metadata.getF24Excise().getInps().getRecords().get(0).getDebit().toString()));

        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setApplyCost(false);
        regionRecord.setDebit(0);
        RegionSection regionSection = new RegionSection();
        regionSection.setRecords(List.of(regionRecord));
        f24Excise.setRegion(regionSection);
        f24Metadata.f24Excise(f24Excise);

        assertEquals(0, Integer.parseInt(f24Metadata.getF24Excise().getRegion().getRecords().get(0).getDebit().toString()));

        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(false);
        localTaxRecord.setDebit(0);
        LocalTaxSection localTax = new LocalTaxSection();
        localTax.setRecords(List.of(localTaxRecord));
        f24Excise.setLocalTax(localTax);
        f24Metadata.f24Excise(f24Excise);

        assertEquals(0, Integer.parseInt(f24Metadata.getF24Excise().getLocalTax().getRecords().get(0).getDebit().toString()));

        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setApplyCost(false);
        exciseTax.setDebit(0);
        ExciseSection exciseSection = new ExciseSection();
        exciseSection.setRecords(List.of(exciseTax));
        f24Excise.setExcise(exciseSection);

        f24Metadata.f24Excise(f24Excise);

        assertEquals(0, Integer.parseInt(f24Metadata.getF24Excise().getExcise().getRecords().get(0).getDebit().toString()));
    }

}

