package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import org.f24.dto.component.TaxPayer;
import org.f24.dto.form.F24Form;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class F24StandardConverterTest {

    /**
     * Method under test: {@link F24StandardConverter#convert(F24Metadata)}
     */
    @Test
    void testConvertNull() {
        F24StandardConverter f24StandardConverter = new F24StandardConverter();
        F24Form convertedMetadata = f24StandardConverter.convert(new F24Metadata());
        assertNotNull(convertedMetadata);
    }

    /**
     * Method under test: {@link F24StandardConverter#convert(F24Metadata)}
     */
    @Test
    void testConvert() {
        F24StandardConverter f24StandardConverter = new F24StandardConverter();

        it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecuritySection socialSecuritySection = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecuritySection();
        socialSecuritySection.addSocSecRecordsItem(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecurityRecord());
        socialSecuritySection.addRecordsItem(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.InailRecord());
        TaxPayerStandard taxPayer = new TaxPayerStandard();
        taxPayer.setCompany(new CompanyData());
        taxPayer.setPerson(new PersonData());
        it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection treasury = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection();
        InpsSection inps = new InpsSection();
        RegionSection region = new RegionSection();
        it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard f24Standard = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard(
                taxPayer, treasury, inps, region, new it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection(),
                socialSecuritySection);

        org.f24.dto.form.F24Standard actualConvertResult = f24StandardConverter
                .convert(new F24Metadata(f24Standard, new F24Simplified(), new F24Excise(), new F24Elid()));
        org.f24.dto.component.SocialSecuritySection socialSecuritySection1 = actualConvertResult
                .getSocialSecuritySection();
        assertEquals(1, socialSecuritySection1.getSocialSecurityRecordList().size());
        assertEquals(1, socialSecuritySection1.getInailRecords().size());
        org.f24.dto.component.LocalTaxSection localTaxSection = actualConvertResult.getLocalTaxSection();
        assertNull(localTaxSection.getDeduction());
        TaxPayer taxPayer1 = actualConvertResult.getTaxPayer();
        assertFalse(taxPayer1.getIsNotTaxYear());
        assertNull(taxPayer1.getIdCode());
        org.f24.dto.component.TreasurySection treasurySection = actualConvertResult.getTreasurySection();
        assertNull(treasurySection.getOfficeCode());
        assertNull(treasurySection.getDocumentCode());
        assertNull(localTaxSection.getOperationId());
        assertNull(taxPayer1.getTaxCode());
        assertNull(taxPayer1.getRelativePersonTaxCode());
    }
}

