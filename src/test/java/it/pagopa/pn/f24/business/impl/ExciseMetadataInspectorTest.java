package it.pagopa.pn.f24.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.ExciseSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
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

import org.junit.jupiter.api.Test;

class ExciseMetadataInspectorTest {
    /**
     * Method under test:
     * {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
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
    void testGetTotalAmount() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        F24Excise f24Excise = F24Excise.builder().build();

        TreasurySection treasurySection = new TreasurySection();
        Tax tax = new Tax();
        tax.setCredit(100);
        treasurySection.setRecords(List.of(tax));
        f24Excise.setTreasury(treasurySection);

        InpsSection inpsSection = new InpsSection();
        InpsRecord inpsRecord = new InpsRecord();
        inpsRecord.setCredit(100);
        inpsSection.setRecords(List.of(inpsRecord));
        f24Excise.setInps(inpsSection);

        RegionSection regionSection = new RegionSection();
        RegionRecord regionRecord = new RegionRecord();
        regionRecord.setCredit(100);
        regionSection.setRecords(List.of(regionRecord));
        f24Excise.setRegion(regionSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setCredit(100);
        localTaxSection.setRecords(List.of(localTaxRecord));
        f24Excise.setLocalTax(localTaxSection);

        ExciseSection exciseSection = new ExciseSection();
        ExciseTax exciseTax = new ExciseTax();
        exciseTax.setDebit(500);
        exciseSection.setRecords(List.of(exciseTax));
        f24Excise.setExcise(exciseSection);

        f24Metadata.setF24Excise(f24Excise);
        assertEquals(500, exciseMetadataInspector.getTotalAmount(f24Metadata));
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount2() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        assertEquals(0.0d, exciseMetadataInspector.getTotalAmount(new F24Metadata()));
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount3() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
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
        assertEquals(0.0d, exciseMetadataInspector.getTotalAmount(f24Metadata));
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount4() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Excise f24Excise = mock(F24Excise.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult = builderResult.records(new ArrayList<>()).build();
        when(f24Excise.getInps()).thenReturn(buildResult);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult2 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Excise.getLocalTax()).thenReturn(buildResult2);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult3 = builderResult2.records(new ArrayList<>()).build();
        when(f24Excise.getRegion()).thenReturn(buildResult3);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult4 = officeResult.records(new ArrayList<>()).build();
        when(f24Excise.getTreasury()).thenReturn(buildResult4);
        double actualTotalAmount = exciseMetadataInspector.getTotalAmount(
                new F24Metadata(mock(F24Standard.class), mock(F24Simplified.class), f24Excise, mock(F24Elid.class)));
        verify(f24Excise, atLeast(1)).getInps();
        verify(f24Excise, atLeast(1)).getLocalTax();
        verify(f24Excise, atLeast(1)).getRegion();
        verify(f24Excise, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount5() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        ArrayList<InpsRecord> records = new ArrayList<>();
        InpsRecord.InpsRecordBuilder officeResult = InpsRecord.builder()
                .applyCost(true)
                .credit(1)
                .debit(1)
                .inps("Inps")
                .office("Office");
        Period period = Period.builder().endDate("2020-03-01").startDate("2020-03-01").build();
        InpsRecord buildResult = officeResult.period(period).reason("Just cause").build();
        records.add(buildResult);
        InpsSection buildResult2 = InpsSection.builder().records(records).build();
        F24Excise f24Excise = mock(F24Excise.class);
        when(f24Excise.getInps()).thenReturn(buildResult2);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult3 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Excise.getLocalTax()).thenReturn(buildResult3);
        RegionSection.RegionSectionBuilder builderResult = RegionSection.builder();
        RegionSection buildResult4 = builderResult.records(new ArrayList<>()).build();
        when(f24Excise.getRegion()).thenReturn(buildResult4);
        TreasurySection.TreasurySectionBuilder officeResult2 = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult5 = officeResult2.records(new ArrayList<>()).build();
        when(f24Excise.getTreasury()).thenReturn(buildResult5);
        double actualTotalAmount = exciseMetadataInspector.getTotalAmount(
                new F24Metadata(mock(F24Standard.class), mock(F24Simplified.class), f24Excise, mock(F24Elid.class)));
        verify(f24Excise, atLeast(1)).getInps();
        verify(f24Excise, atLeast(1)).getLocalTax();
        verify(f24Excise, atLeast(1)).getRegion();
        verify(f24Excise, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount6() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

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
        F24Excise f24Excise = mock(F24Excise.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Excise.getInps()).thenReturn(buildResult3);
        when(f24Excise.getLocalTax()).thenReturn(buildResult2);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult4 = builderResult2.records(new ArrayList<>()).build();
        when(f24Excise.getRegion()).thenReturn(buildResult4);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult5 = officeResult.records(new ArrayList<>()).build();
        when(f24Excise.getTreasury()).thenReturn(buildResult5);
        double actualTotalAmount = exciseMetadataInspector.getTotalAmount(
                new F24Metadata(mock(F24Standard.class), mock(F24Simplified.class), f24Excise, mock(F24Elid.class)));
        verify(f24Excise, atLeast(1)).getInps();
        verify(f24Excise, atLeast(1)).getLocalTax();
        verify(f24Excise, atLeast(1)).getRegion();
        verify(f24Excise, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount7() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

        ArrayList<RegionRecord> records = new ArrayList<>();
        RegionRecord buildResult = RegionRecord.builder()
                .applyCost(true)
                .credit(1)
                .debit(1)
                .installment("Installment")
                .region("us-east-2")
                .taxType("Tax Type")
                .year("Year")
                .build();
        records.add(buildResult);
        RegionSection buildResult2 = RegionSection.builder().records(records).build();
        F24Excise f24Excise = mock(F24Excise.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Excise.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Excise.getLocalTax()).thenReturn(buildResult4);
        when(f24Excise.getRegion()).thenReturn(buildResult2);
        TreasurySection.TreasurySectionBuilder officeResult = TreasurySection.builder()
                .document("Document")
                .office("Office");
        TreasurySection buildResult5 = officeResult.records(new ArrayList<>()).build();
        when(f24Excise.getTreasury()).thenReturn(buildResult5);
        double actualTotalAmount = exciseMetadataInspector.getTotalAmount(
                new F24Metadata(mock(F24Standard.class), mock(F24Simplified.class), f24Excise, mock(F24Elid.class)));
        verify(f24Excise, atLeast(1)).getInps();
        verify(f24Excise, atLeast(1)).getLocalTax();
        verify(f24Excise, atLeast(1)).getRegion();
        verify(f24Excise, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#getTotalAmount(F24Metadata)}
     */
    @Test
    void testGetTotalAmount8() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();

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
        F24Excise f24Excise = mock(F24Excise.class);
        InpsSection.InpsSectionBuilder builderResult = InpsSection.builder();
        InpsSection buildResult3 = builderResult.records(new ArrayList<>()).build();
        when(f24Excise.getInps()).thenReturn(buildResult3);
        LocalTaxSection.LocalTaxSectionBuilder operationIdResult = LocalTaxSection.builder()
                .deduction("Deduction")
                .operationId("42");
        LocalTaxSection buildResult4 = operationIdResult.records(new ArrayList<>()).build();
        when(f24Excise.getLocalTax()).thenReturn(buildResult4);
        RegionSection.RegionSectionBuilder builderResult2 = RegionSection.builder();
        RegionSection buildResult5 = builderResult2.records(new ArrayList<>()).build();
        when(f24Excise.getRegion()).thenReturn(buildResult5);
        when(f24Excise.getTreasury()).thenReturn(buildResult2);
        double actualTotalAmount = exciseMetadataInspector.getTotalAmount(
                new F24Metadata(mock(F24Standard.class), mock(F24Simplified.class), f24Excise, mock(F24Elid.class)));
        verify(f24Excise, atLeast(1)).getInps();
        verify(f24Excise, atLeast(1)).getLocalTax();
        verify(f24Excise, atLeast(1)).getRegion();
        verify(f24Excise, atLeast(1)).getTreasury();
        assertEquals(0.0d, actualTotalAmount);
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
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN,
                exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithInvalidApplyCost() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = getF24MetadataExciseValidWithAllApplyCostFalse();
        f24Metadata.getF24Excise().getTreasury().getRecords().get(0).setApplyCost(true);
        f24Metadata.getF24Excise().getTreasury().getRecords().get(0).setCredit(100);
        assertEquals(ApplyCostValidation.INVALID_APPLY_COST_GIVEN,
                exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    @Test
    void testCheckApplyCostFailWithRequiredButNotGivenAndMetadataIsNull() {
        ExciseMetadataInspector exciseMetadataInspector = new ExciseMetadataInspector();
        F24Metadata f24Metadata = new F24Metadata();
        assertEquals(ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN,
                exciseMetadataInspector.checkApplyCost(f24Metadata, true));
    }

    /**
     * Method under test:
     * {@link ExciseMetadataInspector#countMetadataApplyCost(F24Metadata)}
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

