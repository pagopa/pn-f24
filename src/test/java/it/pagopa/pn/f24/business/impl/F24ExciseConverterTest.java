package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.List;

import org.f24.dto.component.TaxPayer;
import org.f24.dto.form.F24Form;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class F24ExciseConverterTest {
    /**
     * Method under test: {@link F24ElidConverter#convert(F24Metadata)}
     */
    @Test
    void testConvertNull() {
        F24ExciseConverter f24SimplifiedConverter = new F24ExciseConverter();
        F24Form convertedMetadata = f24SimplifiedConverter.convert(new F24Metadata());
        assertNotNull(convertedMetadata);
    }
    /**
     * Method under test: {@link F24ExciseConverter#convert(F24Metadata)}
     */
    @Test
    void testConvert() {
        F24ExciseConverter f24ExciseConverter = new F24ExciseConverter();

        TaxPayerExcise taxPayerExcise = new TaxPayerExcise();
        taxPayerExcise.setIsNotTaxYear(true);
        taxPayerExcise.setCompany(new CompanyData());
        taxPayerExcise.setPerson(new PersonData());

        ExciseSection exciseSection = new ExciseSection();
        exciseSection.setRecords(List.of(new ExciseTax()));

        F24Excise f24Excise = new F24Excise(taxPayerExcise, new TreasurySection(), new InpsSection(), new RegionSection(), new LocalTaxSection(), exciseSection);

        org.f24.dto.form.F24Excise actualConvertResult = f24ExciseConverter
                .convert(new F24Metadata(new F24Standard(), new F24Simplified(), f24Excise, new F24Elid()));
        org.f24.dto.component.LocalTaxSection localTaxSection = actualConvertResult.getLocalTaxSection();
        assertNull(localTaxSection.getDeduction());
        TaxPayer taxPayer = actualConvertResult.getTaxPayer();
        assertNull(taxPayer.getRelativePersonTaxCode());
        assertTrue(taxPayer.getIsNotTaxYear());
        assertNull(taxPayer.getIdCode());
        org.f24.dto.component.TreasurySection treasurySection = actualConvertResult.getTreasurySection();
        assertNull(treasurySection.getOfficeCode());
        assertNull(treasurySection.getDocumentCode());
        org.f24.dto.component.ExciseSection exciseSectionConverted = actualConvertResult.getExciseSection();
        assertNull(exciseSectionConverted.getDocumentCode());
        assertNull(exciseSectionConverted.getOfficeCode());
        assertNull(taxPayer.getTaxCode());
        assertNull(localTaxSection.getOperationId());
    }

}

