package it.pagopa.pn.f24.business.impl;

import static org.f24.service.pdf.util.FieldEnum.TAX_RECORDS_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.ExciseSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.InailRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.Period;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonData;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonalData;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SimplifiedPaymentSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecurityRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecuritySection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.Tax;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxAddress;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerElide;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerExcise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerSimplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerStandard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasuryAndOtherSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class StandardMetadataInspectorTest {
    /**
     * Method under test:
     * {@link StandardMetadataInspector#countMetadataApplyCost(F24Metadata)}
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
     * Method under test:
     * {@link StandardMetadataInspector#countMetadataApplyCost(F24Metadata)}
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

        assertEquals(5, f24Metadata.getF24Standard().getTreasury().getRecords().get(0).getDebit());
    }

    @Test
    void testCheckApplyCostOk() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataStandardValidWithAllApplyCostFalse();
        f24Metadata.getF24Standard().getTreasury().getRecords().get(0).setApplyCost(true);
        assertEquals(ApplyCostValidation.OK, standardMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGiven() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataStandardValidWithAllApplyCostFalse();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN,
                standardMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithInvalidApplyCost() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataStandardValidWithAllApplyCostFalse();
        f24Metadata.getF24Standard().getTreasury().getRecords().get(0).setApplyCost(true);
        f24Metadata.getF24Standard().getTreasury().getRecords().get(0).setCredit(100);
        assertEquals(ApplyCostValidation.INVALID_APPLY_COST_GIVEN,
                standardMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGivenAndMetadataIsNull() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN,
                standardMetadataInspector.checkApplyCost(f24Metadata, true));
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

        assertEquals(5, f24Metadata.getF24Standard().getInps().getRecords().get(0).getDebit());
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

        assertEquals(5, f24Metadata.getF24Standard().getRegion().getRecords().get(0).getDebit());
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

        assertEquals(5, f24Metadata.getF24Standard().getLocalTax().getRecords().get(0).getDebit());
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

        assertEquals(5, f24Metadata.getF24Standard().getSocialSecurity().getRecords().get(0).getDebit());
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

        assertEquals(5, f24Metadata.getF24Standard().getSocialSecurity().getSocSecRecords().get(0).getDebit());
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        assertEquals(0.0d, standardMetadataInspector.getTotalAmount(new F24Metadata()));
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount2() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Metadata.F24MetadataBuilder builderResult = F24Metadata.builder();
        F24Elid.F24ElidBuilder builderResult2 = F24Elid.builder();
        TaxPayerElide.TaxPayerElideBuilder builderResult3 = TaxPayerElide.builder();
        CompanyData.CompanyDataBuilder nameResult = CompanyData.builder().name("Name");
        TaxAddress taxAddress = TaxAddress.builder().municipality("Municipality").province("Province").build();
        CompanyData company = nameResult.taxAddress(taxAddress).build();
        TaxPayerElide.TaxPayerElideBuilder idResult = builderResult3.company(company).id("42");
        PersonData.PersonDataBuilder builderResult4 = PersonData.builder();
        PersonalData personalData = PersonalData.builder()
                .birthDate("2020-03-01")
                .birthPlace("Birth Place")
                .birthProvince("Birth Province")
                .name("Name")
                .sex("Sex")
                .surname("Doe")
                .build();
        PersonData.PersonDataBuilder personalDataResult = builderResult4.personalData(personalData);
        TaxAddress taxAddress2 = TaxAddress.builder().municipality("Municipality").province("Province").build();
        PersonData person = personalDataResult.taxAddress(taxAddress2).build();
        TaxPayerElide taxPayer = idResult.person(person)
                .relativePersonTaxCode("Relative Person Tax Code")
                .taxCode("Tax Code")
                .build();
        F24Elid.F24ElidBuilder taxPayerResult = builderResult2.taxPayer(taxPayer);
        TreasuryAndOtherSection.TreasuryAndOtherSectionBuilder officeResult = TreasuryAndOtherSection.builder()
                .document("Document")
                .office("Office");
        TreasuryAndOtherSection treasury = officeResult.records(new ArrayList<>()).build();
        F24Elid f24Elid = taxPayerResult.treasury(treasury).build();
        F24Metadata.F24MetadataBuilder f24ElidResult = builderResult.f24Elid(f24Elid);
        F24Excise.F24ExciseBuilder builderResult5 = F24Excise.builder();
        ExciseSection.ExciseSectionBuilder officeResult2 = ExciseSection.builder().document("Document").office("Office");
        ExciseSection excise = officeResult2.records(new ArrayList<>()).build();
        F24Excise.F24ExciseBuilder exciseResult = builderResult5.excise(excise);
        InpsSection.InpsSectionBuilder builderResult6 = InpsSection.builder();
        InpsSection inps = builderResult6.records(new ArrayList<>()).build();
        F24Excise.F24ExciseBuilder inpsResult = exciseResult.inps(inps);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection localTax = operationIdResult.records(new ArrayList<>()).build();
        F24Excise.F24ExciseBuilder localTaxResult = inpsResult.localTax(localTax);
        RegionSection.RegionSectionBuilder builderResult7 = RegionSection.builder();
        RegionSection region = builderResult7.records(new ArrayList<>()).build();
        F24Excise.F24ExciseBuilder regionResult = localTaxResult.region(region);
        TaxPayerExcise.TaxPayerExciseBuilder builderResult8 = TaxPayerExcise.builder();
        CompanyData.CompanyDataBuilder nameResult2 = CompanyData.builder().name("Name");
        TaxAddress taxAddress3 = TaxAddress.builder().municipality("Municipality").province("Province").build();
        CompanyData company2 = nameResult2.taxAddress(taxAddress3).build();
        TaxPayerExcise.TaxPayerExciseBuilder idResult2 = builderResult8.company(company2).id("42");
        PersonData.PersonDataBuilder builderResult9 = PersonData.builder();
        PersonalData personalData2 = PersonalData.builder()
                .birthDate("2020-03-01")
                .birthPlace("Birth Place")
                .birthProvince("Birth Province")
                .name("Name")
                .sex("Sex")
                .surname("Doe")
                .build();
        PersonData.PersonDataBuilder personalDataResult2 = builderResult9.personalData(personalData2);
        TaxAddress taxAddress4 = TaxAddress.builder().municipality("Municipality").province("Province").build();
        PersonData person2 = personalDataResult2.taxAddress(taxAddress4).build();
        TaxPayerExcise taxPayer2 = idResult2.person(person2)
                .relativePersonTaxCode("Relative Person Tax Code")
                .taxCode("Tax Code")
                .build();
        F24Excise.F24ExciseBuilder taxPayerResult2 = regionResult.taxPayer(taxPayer2);
        TreasurySection.TreasurySectionBuilder officeResult3 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection treasury2 = officeResult3.records(new ArrayList<>()).build();
        F24Excise f24Excise = taxPayerResult2.treasury(treasury2).build();
        F24Metadata.F24MetadataBuilder f24ExciseResult = f24ElidResult.f24Excise(f24Excise);
        F24Simplified.F24SimplifiedBuilder builderResult10 = F24Simplified.builder();
        SimplifiedPaymentSection.SimplifiedPaymentSectionBuilder operationIdResult2 = SimplifiedPaymentSection.builder()
                .operationId("42");
        SimplifiedPaymentSection payments = operationIdResult2.records(new ArrayList<>()).build();
        F24Simplified.F24SimplifiedBuilder paymentsResult = builderResult10.payments(payments);
        TaxPayerSimplified.TaxPayerSimplifiedBuilder officeResult4 = TaxPayerSimplified.builder()
                .document("Document")
                .id("42")
                .office("Office");
        PersonalData personalData3 = PersonalData.builder()
                .birthDate("2020-03-01")
                .birthPlace("Birth Place")
                .birthProvince("Birth Province")
                .name("Name")
                .sex("Sex")
                .surname("Doe")
                .build();
        TaxPayerSimplified taxPayer3 = officeResult4.personalData(personalData3)
                .relativePersonTaxCode("Relative Person Tax Code")
                .taxCode("Tax Code")
                .build();
        F24Simplified f24Simplified = paymentsResult.taxPayer(taxPayer3).build();
        F24Metadata.F24MetadataBuilder f24SimplifiedResult = f24ExciseResult.f24Simplified(f24Simplified);
        F24Standard.F24StandardBuilder builderResult11 = F24Standard.builder();
        InpsSection.InpsSectionBuilder builderResult12 = InpsSection.builder();
        InpsSection inps2 = builderResult12.records(new ArrayList<>()).build();
        F24Standard.F24StandardBuilder inpsResult2 = builderResult11.inps(inps2);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult3 = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection localTax2 = operationIdResult3.records(new ArrayList<>()).build();
        F24Standard.F24StandardBuilder localTaxResult2 = inpsResult2.localTax(localTax2);
        RegionSection.RegionSectionBuilder builderResult13 = RegionSection.builder();
        RegionSection region2 = builderResult13.records(new ArrayList<>()).build();
        F24Standard.F24StandardBuilder regionResult2 = localTaxResult2.region(region2);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult14 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult14.records(new ArrayList<>());
        SocialSecuritySection socialSecurity = recordsResult.socSecRecords(new ArrayList<>()).build();
        F24Standard.F24StandardBuilder socialSecurityResult = regionResult2.socialSecurity(socialSecurity);
        TaxPayerStandard.TaxPayerStandardBuilder builderResult15 = TaxPayerStandard.builder();
        CompanyData.CompanyDataBuilder nameResult3 = CompanyData.builder().name("Name");
        TaxAddress taxAddress5 = TaxAddress.builder().municipality("Municipality").province("Province").build();
        CompanyData company3 = nameResult3.taxAddress(taxAddress5).build();
        TaxPayerStandard.TaxPayerStandardBuilder idResult3 = builderResult15.company(company3).id("42");
        PersonData.PersonDataBuilder builderResult16 = PersonData.builder();
        PersonalData personalData4 = PersonalData.builder()
                .birthDate("2020-03-01")
                .birthPlace("Birth Place")
                .birthProvince("Birth Province")
                .name("Name")
                .sex("Sex")
                .surname("Doe")
                .build();
        PersonData.PersonDataBuilder personalDataResult3 = builderResult16.personalData(personalData4);
        TaxAddress taxAddress6 = TaxAddress.builder().municipality("Municipality").province("Province").build();
        PersonData person3 = personalDataResult3.taxAddress(taxAddress6).build();
        TaxPayerStandard taxPayer4 = idResult3.person(person3)
                .relativePersonTaxCode("Relative Person Tax Code")
                .taxCode("Tax Code")
                .build();
        F24Standard.F24StandardBuilder taxPayerResult3 = socialSecurityResult.taxPayer(taxPayer4);
        TreasurySection.TreasurySectionBuilder officeResult5 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection treasury3 = officeResult5.records(new ArrayList<>()).build();
        F24Standard f24Standard = taxPayerResult3.treasury(treasury3).build();
        F24Metadata f24Metadata = f24SimplifiedResult.f24Standard(f24Standard).build();
        assertEquals(0.0d, standardMetadataInspector.getTotalAmount(f24Metadata));
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount3() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult2 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult2);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult3 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult3);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult3 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult3.records(new ArrayList<>());
        SocialSecuritySection buildResult4 = recordsResult.socSecRecords(new ArrayList<>()).build();
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult4);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult5 = officeResult.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult5);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount4() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<InpsRecord> records = new ArrayList<>();
        InpsRecord.InpsRecordBuilder officeResult = InpsRecord.builder()
                .applyCost(true)
                .credit(0)
                .debit(1)
                .inps("Inps")
                .office("Office");
        Period period = Period.builder().endDate("2020-03-01").startDate("2020-03-01").build();
        InpsRecord buildResult = officeResult.period(period).reason("Just cause").build();
        records.add(buildResult);
        InpsSection buildResult2 = InpsSection.builder().records(records).build();
        F24Standard f24Standard = mock(F24Standard.class);
        when(f24Standard.getInps()).thenReturn(buildResult2);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult3 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult3);
        RegionSection.RegionSectionBuilder builderResult = RegionSection.builder();
        RegionSection buildResult4 = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult4);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult2 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult2.records(new ArrayList<>());
        SocialSecuritySection buildResult5 = recordsResult.socSecRecords(new ArrayList<>()).build();
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult5);
        TreasurySection.TreasurySectionBuilder officeResult2 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult6 = officeResult2.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult6);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.01d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount5() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<LocalTaxRecord> records = new ArrayList<>();
        LocalTaxRecord buildResult = LocalTaxRecord.builder()
                .advancePayment(true)
                .applyCost(true)
                .credit(1)
                .debit(1)
                .fullPayment(true)
                .installment("Installment")
                .municipality("Municipality")
                .numberOfProperties("42")
                .propertiesChanges(true)
                .reconsideration(true)
                .taxType("Tax Type")
                .year("Year")
                .build();
        records.add(buildResult);
        LocalTaxSection buildResult2 = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42")
                .records(records)
                .build();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult3);
        when(f24Standard.getLocalTax()).thenReturn(buildResult2);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult4 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult4);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult3 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult3.records(new ArrayList<>());
        SocialSecuritySection buildResult5 = recordsResult.socSecRecords(new ArrayList<>()).build();
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult5);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult6 = officeResult.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult6);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.00, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount6() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<RegionRecord> records = new ArrayList<>();
        RegionRecord buildResult = RegionRecord.builder()
                .applyCost(true)
                .credit(2)
                .debit(2)
                .installment("Installment")
                .region("us-east-2")
                .taxType("Tax Type")
                .year("Year")
                .build();
        records.add(buildResult);
        RegionSection buildResult2 = RegionSection.builder().records(records).build();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult4);
        when(f24Standard.getRegion()).thenReturn(buildResult2);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult2 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult2.records(new ArrayList<>());
        SocialSecuritySection buildResult5 = recordsResult.socSecRecords(new ArrayList<>()).build();
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult5);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult6 = officeResult.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult6);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.00, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount7() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<InailRecord> records = new ArrayList<>();
        InailRecord buildResult = InailRecord.builder()
                .applyCost(true)
                .company("Company")
                .control("Control")
                .credit(1)
                .debit(3)
                .office("Office")
                .reason("Just cause")
                .refNumber("42")
                .build();
        records.add(buildResult);
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = SocialSecuritySection.builder().records(records);
        SocialSecuritySection buildResult2 = recordsResult.socSecRecords(new ArrayList<>()).build();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult4);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult5 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult5);
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult2);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult6 = officeResult.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult6);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.02d, actualTotalAmount);
    }


    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount8() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<SocialSecurityRecord> socSecRecords = new ArrayList<>();
        SocialSecurityRecord.SocialSecurityRecordBuilder officeResult = SocialSecurityRecord.builder()
                .applyCost(true)
                .credit(1)
                .debit(1)
                .institution("Institution")
                .office("Office");
        Period period = Period.builder().endDate("2020-03-01").startDate("2020-03-01").build();
        SocialSecurityRecord buildResult = officeResult.period(period).position("Position").reason("Just cause").build();
        socSecRecords.add(buildResult);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult = SocialSecuritySection.builder();
        SocialSecuritySection buildResult2 = builderResult.records(new ArrayList<>()).socSecRecords(socSecRecords).build();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult2 = InpsSection.builder();
        InpsSection buildResult3 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult4);
        RegionSection.RegionSectionBuilder builderResult3 = RegionSection.builder();
        RegionSection buildResult5 = builderResult3.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult5);
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult2);
        TreasurySection.TreasurySectionBuilder officeResult2 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult6 = officeResult2.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult6);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.00, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link StandardMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount9() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<Tax> records = new ArrayList<>();
        Tax buildResult = Tax.builder()
                .applyCost(true)
                .credit(1)
                .debit(1)
                .installment("Installment")
                .taxType("Tax Type")
                .year("Year")
                .build();
        records.add(buildResult);
        TreasurySection buildResult2 = TreasurySection.builder()
                .document("Document")
                .office("Office")
                .records(records)
                .build();
        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult4);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult5 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult5);
        SocialSecuritySection.SocialSecuritySectionBuilder builderResult3 = SocialSecuritySection.builder();
        SocialSecuritySection.SocialSecuritySectionBuilder recordsResult = builderResult3.records(new ArrayList<>());
        SocialSecuritySection buildResult6 = recordsResult.socSecRecords(new ArrayList<>()).build();
        when(f24Standard.getSocialSecurity()).thenReturn(buildResult6);
        when(f24Standard.getTreasury()).thenReturn(buildResult2);
        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));
        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();
        assertEquals(0.00, actualTotalAmount);
    }

    @Test
    void testGetTotalAmountDoubleRecord() {
        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();

        ArrayList<SocialSecurityRecord> socSecRecords = new ArrayList<>();
        SocialSecurityRecord.SocialSecurityRecordBuilder officeResult = SocialSecurityRecord.builder()
                .applyCost(true)
                .credit(2)
                .debit(0)
                .institution("Institution")
                .office("Office");
        Period period = Period.builder().endDate("2020-03-01").startDate("2020-03-01").build();
        SocialSecurityRecord buildResult = officeResult.period(period).position("Position").reason("Just cause").build();
        socSecRecords.add(buildResult);
        SocialSecurityRecord.SocialSecurityRecordBuilder officeResult2 = SocialSecurityRecord.builder()
                .applyCost(true)
                .credit(0)
                .debit(2)
                .institution("Institution2")
                .office("Office2");
        Period period2 = Period.builder().endDate("2020-03-01").startDate("2020-03-01").build();
        SocialSecurityRecord buildResult2 = officeResult2.period(period2).position("Position2").reason("Just cause 2").build();
        socSecRecords.add(buildResult2);

        SocialSecuritySection.SocialSecuritySectionBuilder builderResult = SocialSecuritySection.builder();
        SocialSecuritySection buildResult3 = builderResult.records(new ArrayList<>()).socSecRecords(socSecRecords).build();

        F24Standard f24Standard = mock(F24Standard.class);
        InpsSection.InpsSectionBuilder builderResult2 = InpsSection.builder();
        InpsSection buildResult4 = builderResult2.records(new ArrayList<>()).build();
        when(f24Standard.getInps()).thenReturn(buildResult4);

        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult5 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Standard.getLocalTax()).thenReturn(buildResult5);

        RegionSection.RegionSectionBuilder builderResult3 = RegionSection.builder();
        RegionSection buildResult6 = builderResult3.records(new ArrayList<>()).build();
        when(f24Standard.getRegion()).thenReturn(buildResult6);

        when(f24Standard.getSocialSecurity()).thenReturn(buildResult3);

        TreasurySection.TreasurySectionBuilder officeResult3 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult7 = officeResult3.records(new ArrayList<>()).build();
        when(f24Standard.getTreasury()).thenReturn(buildResult7);

        double actualTotalAmount = standardMetadataInspector.getTotalAmount(
                new F24Metadata(f24Standard, mock(F24Simplified.class), mock(F24Excise.class), mock(F24Elid.class)));

        verify(f24Standard, atLeast(1)).getInps();
        verify(f24Standard, atLeast(1)).getLocalTax();
        verify(f24Standard, atLeast(1)).getRegion();
        verify(f24Standard, atLeast(1)).getSocialSecurity();
        verify(f24Standard, atLeast(1)).getTreasury();

        assertEquals(0.0, actualTotalAmount);
    }

    private F24Metadata getF24MetadataStandardValidWithAllApplyCostFalse() {
        InailRecord inailRecord = new InailRecord();
        inailRecord.setApplyCost(false);
        inailRecord.setDebit(0);
        List<InailRecord> inailRecords = new ArrayList<>();
        inailRecords.add(inailRecord);

        SocialSecurityRecord socialSecurityRecord = new SocialSecurityRecord();
        socialSecurityRecord.setApplyCost(false);
        socialSecurityRecord.setDebit(0);
        List<SocialSecurityRecord> socialSecurityRecords = new ArrayList<>();
        socialSecurityRecords.add(socialSecurityRecord);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        socialSecuritySection.setSocSecRecords(socialSecurityRecords);
        socialSecuritySection.setRecords(inailRecords);

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

        F24Standard f24Standard = new F24Standard();
        f24Standard.setSocialSecurity(socialSecuritySection);
        f24Standard.setInps(inpsSection);
        f24Standard.setLocalTax(localTaxSection);
        f24Standard.setRegion(regionSection);
        f24Standard.setTreasury(treasurySection);

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(f24Standard);

        return f24Metadata;
    }

    @Test
    void calculateExpectedNumberOfPagesWhenMetadataHasOneRecord() {

        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(createListOfRecords(1));
        InpsSection inpsSection = new InpsSection();
        inpsSection.setRecords(List.of(new InpsRecord()));
        RegionSection regionSection = new RegionSection();
        regionSection.setRecords(List.of(new RegionRecord()));
        LocalTaxSection localTaxSection = new LocalTaxSection();
        localTaxSection.setRecords(List.of(new LocalTaxRecord()));
        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        socialSecuritySection.setRecords(List.of(new InailRecord()));
        socialSecuritySection.setSocSecRecords(List.of(new SocialSecurityRecord()));


        F24Standard f24Standard = new F24Standard();
        f24Standard.setTreasury(treasurySection);
        f24Standard.setInps(inpsSection);
        f24Standard.setRegion(regionSection);
        f24Standard.setLocalTax(localTaxSection);
        f24Standard.setSocialSecurity(socialSecuritySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(f24Standard);

        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        assertEquals(3, standardMetadataInspector.getExpectedNumberOfPages(f24Metadata));
    }

    @Test
    void calculateExpectedNumberOfPagesWhenMetadataHasManyRecords() {

        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(createListOfRecords(TAX_RECORDS_NUMBER.getRecordsNum() + 1));

        F24Standard f24Standard = new F24Standard();
        f24Standard.setTreasury(treasurySection);
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(f24Standard);

        StandardMetadataInspector standardMetadataInspector = new StandardMetadataInspector();
        assertEquals(6, standardMetadataInspector.getExpectedNumberOfPages(f24Metadata));
    }

    private List<Tax> createListOfRecords(int num) {
        return Stream.generate(Tax::new).limit(num).toList();
    }

}

