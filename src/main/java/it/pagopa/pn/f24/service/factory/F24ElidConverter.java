package it.pagopa.pn.f24.service.factory;

import it.pagopa.pn.f24.dto.metadata.*;
import org.f24.dto.component.TaxPayer;

import java.util.stream.Collectors;

public class F24ElidConverter extends F24Converter {

    @Override
    public org.f24.dto.form.F24Elid convert(F24Item f24Item) {
        org.f24.dto.form.F24Elid f24Elid = new org.f24.dto.form.F24Elid();
        F24Elid f24ElidItem = f24Item.getF24Elid();
        
        if(f24ElidItem == null) {
            return f24Elid;
        }
        //TODO ? f24Elid.setAbiCode();
        //TODO ? f24Elid.setBankId();
        //TODO ? f24Elid.setBankAccountNumber();
        if(f24ElidItem.getTreasury() != null) {
            f24Elid.setTreasuryAndOtherSection(this.convertTreasuryAndOtherSection(f24ElidItem.getTreasury()));
        }
        
        if(f24ElidItem.getTaxPayer() != null) {
            f24Elid.setTaxPayer(this.convertTaxPayer(f24ElidItem.getTaxPayer()));
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
                            .collect(Collectors.toList())
            );
        }

        return treasuryAndOtherSection;
    }

    private org.f24.dto.component.TreasuryRecord convertTreasuryAndOtherSectionRecord(TreasuryRecord dvTreasuryRecord) {
        org.f24.dto.component.TreasuryRecord treasuryRecord = new org.f24.dto.component.TreasuryRecord();
        treasuryRecord.setIdElements(dvTreasuryRecord.getId());
        treasuryRecord.setYear(dvTreasuryRecord.getYear());
        treasuryRecord.setType(dvTreasuryRecord.getType());
        treasuryRecord.setTaxTypeCode(dvTreasuryRecord.getTaxType());
        treasuryRecord.setDebitAmount(dvTreasuryRecord.getDebit());
        // TODO ? treasuryRecord.setCreditAmount(dv);
        // TODO ? treasuryRecord.setDeduction(dv);
        return treasuryRecord;
    }

    private TaxPayer convertTaxPayer(TaxPayerElide dvTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        // TODO ? taxPayer.setIsNotTaxYear(dvTaxPayer.ge);
        taxPayer.setTaxCode(dvTaxPayer.getTaxCode());
        if(dvTaxPayer.getCompany() != null) {
            taxPayer.setCompanyData(this.convertCompanyData(dvTaxPayer.getCompany()));
        }
        if(dvTaxPayer.getPerson() != null) {
            taxPayer.setPersonData(this.convertPersonData(dvTaxPayer.getPerson()));
        }

        taxPayer.setIdCode(dvTaxPayer.getId());
        taxPayer.setRelativePersonTaxCode(dvTaxPayer.getRelativePersonTaxCode());
        // TODO ? taxPayer.setDocumentCode(dv);
        // TODO ? taxPayer.setOfficeCode(dvTaxPayer.get);
        return taxPayer;
    }

}
