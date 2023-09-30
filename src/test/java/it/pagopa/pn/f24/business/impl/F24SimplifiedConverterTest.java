package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import java.util.List;

import org.f24.dto.component.TaxPayer;
import org.f24.dto.form.F24Form;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class F24SimplifiedConverterTest {
    /**
     * Method under test: {@link F24SimplifiedConverter#convert(F24Metadata)}
     */
    @Test
    void testConvertNull() {
        F24SimplifiedConverter f24SimplifiedConverter = new F24SimplifiedConverter();
        F24Form convertedMetadata = f24SimplifiedConverter.convert(new F24Metadata());
        assertNotNull(convertedMetadata);
    }

    /**
     * Method under test: {@link F24SimplifiedConverter#convert(F24Metadata)}
     */
    @Test
    void testConvert() {
        F24SimplifiedConverter f24SimplifiedConverter = new F24SimplifiedConverter();
        TaxPayerSimplified taxPayer = new TaxPayerSimplified();
        taxPayer.setPersonalData(new PersonalData());

        SimplifiedPaymentSection simplifiedPaymentSection = new SimplifiedPaymentSection();
        simplifiedPaymentSection.setOperationId("operationId");
        simplifiedPaymentSection.setRecords(List.of(new SimplifiedPaymentRecord()));

        it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified f24Simplified = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified(
                taxPayer, simplifiedPaymentSection);

        org.f24.dto.form.F24Simplified actualConvertResult = f24SimplifiedConverter
                .convert(new F24Metadata(new F24Standard(), f24Simplified, new F24Excise(), new F24Elid()));
        assertEquals("operationId", actualConvertResult.getPaymentReasonSection().getOperationId());
        TaxPayer taxPayer1 = actualConvertResult.getTaxPayer();
        assertNull(taxPayer1.getIdCode());
        assertNull(taxPayer1.getDocumentCode());
        assertNull(taxPayer1.getRelativePersonTaxCode());
        assertNull(taxPayer1.getTaxCode());
        assertNull(taxPayer1.getOfficeCode());
    }
}

