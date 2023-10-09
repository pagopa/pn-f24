package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.List;

import org.f24.dto.component.TaxPayer;
import org.f24.dto.form.F24Form;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class F24ElidConverterTest {
    /**
     * Method under test: {@link F24ElidConverter#convert(F24Metadata)}
     */
    @Test
    void testConvertNull() {
        F24ElidConverter f24SimplifiedConverter = new F24ElidConverter();
        F24Form convertedMetadata = f24SimplifiedConverter.convert(new F24Metadata());
        assertNotNull(convertedMetadata);
    }

    /**
     * Method under test: {@link F24ElidConverter#convert(F24Metadata)}
     */
    @Test
    void testConvert() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        TaxPayerElide taxPayer = new TaxPayerElide(
    "Tax Code",
            new PersonData(),
            new CompanyData(),
    "Relative Person Tax Code",
            "42"
        );
        TreasuryAndOtherSection treasuryAndOtherSection = new TreasuryAndOtherSection();
        treasuryAndOtherSection.setRecords(List.of(new TreasuryRecord()));
        F24Elid f24Elid = new F24Elid(taxPayer, treasuryAndOtherSection);

        org.f24.dto.form.F24Elid actualConvertResult = f24ElidConverter.convert(new F24Metadata(new F24Standard(),
                new F24Simplified(), new F24Excise(), f24Elid));
        org.f24.dto.component.TreasuryAndOtherSection treasuryAndOtherSectionConverted = actualConvertResult
                .getTreasuryAndOtherSection();
        assertNull(treasuryAndOtherSectionConverted.getOfficeCode());
        TaxPayer taxPayer1 = actualConvertResult.getTaxPayer();
        assertEquals("Tax Code", taxPayer1.getTaxCode());
        assertNull(treasuryAndOtherSectionConverted.getDocumentCode());
        assertEquals("Relative Person Tax Code", taxPayer1.getRelativePersonTaxCode());
        assertEquals("42", taxPayer1.getIdCode());
        assertNull(taxPayer1.getCompanyData().getName());
    }

}

