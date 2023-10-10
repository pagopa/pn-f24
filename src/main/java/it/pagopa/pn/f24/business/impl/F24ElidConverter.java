package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.business.F24Converter;
import org.f24.dto.component.TaxPayer;

public class F24ElidConverter extends F24Converter {

    @Override
    public org.f24.dto.form.F24Elid convert(F24Metadata f24Metadata) {
        org.f24.dto.form.F24Elid f24Elid = new org.f24.dto.form.F24Elid();
        F24Elid f24ElidItem = f24Metadata.getF24Elid();
        
        if(f24ElidItem == null) {
            return f24Elid;
        }

        if(f24ElidItem.getTaxPayer() != null) {
            f24Elid.setTaxPayer(this.convertTaxPayer(f24ElidItem.getTaxPayer()));
        }
        
        if(f24ElidItem.getTreasury() != null) {
            f24Elid.setTreasuryAndOtherSection(this.convertTreasuryAndOtherSection(f24ElidItem.getTreasury()));
        }
        return f24Elid;
    }

    private org.f24.dto.component.TreasuryAndOtherSection convertTreasuryAndOtherSection(TreasuryAndOtherSection treasury) {
        org.f24.dto.component.TreasuryAndOtherSection treasuryAndOtherSection = new org.f24.dto.component.TreasuryAndOtherSection();
        treasuryAndOtherSection.setOfficeCode(treasury.getOffice());
        treasuryAndOtherSection.setDocumentCode(treasury.getDocument());
        if(treasury.getRecords() != null) {
            treasuryAndOtherSection.setTreasuryRecords(
                    treasury.getRecords()
                            .stream()
                            .map(this::convertTreasuryAndOtherSectionRecord)
                            .toList()
            );
        }

        return treasuryAndOtherSection;
    }

    private org.f24.dto.component.TreasuryRecord convertTreasuryAndOtherSectionRecord(TreasuryRecord inputTreasuryRecord) {
        org.f24.dto.component.TreasuryRecord treasuryRecord = new org.f24.dto.component.TreasuryRecord();
        treasuryRecord.setIdElements(inputTreasuryRecord.getId());
        treasuryRecord.setYear(inputTreasuryRecord.getYear());
        treasuryRecord.setType(inputTreasuryRecord.getType());
        treasuryRecord.setTaxTypeCode(inputTreasuryRecord.getTaxType());
        treasuryRecord.setDebitAmount(integerToString(inputTreasuryRecord.getDebit()));
        return treasuryRecord;
    }

    private TaxPayer convertTaxPayer(TaxPayerElide inputTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setTaxCode(inputTaxPayer.getTaxCode());
        if(inputTaxPayer.getCompany() != null) {
            taxPayer.setCompanyData(this.convertCompanyData(inputTaxPayer.getCompany()));
        }
        if(inputTaxPayer.getPerson() != null) {
            taxPayer.setPersonData(this.convertPersonData(inputTaxPayer.getPerson()));
        }

        taxPayer.setIdCode(inputTaxPayer.getId());
        taxPayer.setRelativePersonTaxCode(inputTaxPayer.getRelativePersonTaxCode());

        return taxPayer;
    }

}
