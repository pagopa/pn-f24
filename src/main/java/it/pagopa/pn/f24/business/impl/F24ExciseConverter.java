package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.business.F24Converter;
import org.f24.dto.component.TaxPayer;

import java.util.stream.Collectors;


public class F24ExciseConverter extends F24Converter {
    @Override
    public org.f24.dto.form.F24Excise convert(F24Metadata f24Metadata) {
        org.f24.dto.form.F24Excise f24Excise = new org.f24.dto.form.F24Excise();
        F24Excise f24ExciseItem = f24Metadata.getF24Excise();
        if(f24ExciseItem == null) {
            return f24Excise;
        }

        //TODO ? f24Excise.setIbanCode();

        if(f24ExciseItem.getInps() != null) {
            f24Excise.setInpsSection(this.convertInpsSection(f24ExciseItem.getInps()));
        }
        if(f24ExciseItem.getRegion() != null) {
            f24Excise.setRegionSection(this.convertRegionSection(f24ExciseItem.getRegion()));
        }
        if(f24ExciseItem.getTreasury() != null) {
            f24Excise.setTreasurySection(this.convertTreasurySection(f24ExciseItem.getTreasury()));
        }
        if(f24ExciseItem.getLocalTax() != null) {
            f24Excise.setLocalTaxSection(this.convertLocalTaxSection(f24ExciseItem.getLocalTax()));
        }
        if(f24ExciseItem.getTaxPayer() != null) {
            f24Excise.setTaxPayer(this.convertTaxPayer(f24ExciseItem.getTaxPayer()));
        }
        if(f24ExciseItem.getExcise() != null) {
            f24Excise.setExciseSection(this.convertExciseSection(f24ExciseItem.getExcise()));
        }

        return f24Excise;
    }

    private TaxPayer convertTaxPayer(TaxPayerExcise dvTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setIdCode(dvTaxPayer.getId());
        taxPayer.setTaxCode(dvTaxPayer.getTaxCode());
        taxPayer.setIsNotTaxYear(dvTaxPayer.getIsNotTaxYear());
        taxPayer.setRelativePersonTaxCode(dvTaxPayer.getRelativePersonTaxCode());
        // TODO ? taxPayer.setDocumentCode();
        // TODO ?  taxPayer.setOfficeCode();
        if(dvTaxPayer.getPerson() != null) {
            taxPayer.setPersonData(this.convertPersonData(dvTaxPayer.getPerson()));
        }
        if(dvTaxPayer.getCompany() != null) {
            taxPayer.setCompanyData(this.convertCompanyData(dvTaxPayer.getCompany()));
        }

        return taxPayer;
    }

    private org.f24.dto.component.ExciseSection convertExciseSection(ExciseSection excise) {
        org.f24.dto.component.ExciseSection exciseSection = new org.f24.dto.component.ExciseSection();
        exciseSection.setOfficeCode(excise.getOffice());
        exciseSection.setDocumentCode(excise.getDocument());
        if(excise.getRecords() != null) {
            exciseSection.setExciseTaxList(
                    excise.getRecords()
                            .stream()
                            .map(this::convertExciseRecord)
                            .collect(Collectors.toList())
            );
        }
        return exciseSection;
    }

    private org.f24.dto.component.ExciseTax convertExciseRecord(ExciseTax inputExciseTax) {
        org.f24.dto.component.ExciseTax exciseTax = new org.f24.dto.component.ExciseTax();
        // TODO ? inputExciseTax.getInstitution(); Non va settato?
        exciseTax.setIdCode(inputExciseTax.getId());
        exciseTax.setYear(inputExciseTax.getYear());
        exciseTax.setMonth(inputExciseTax.getMonth());
        exciseTax.setProvince(inputExciseTax.getProvince());
        exciseTax.setTaxTypeCode(inputExciseTax.getTaxType());
        exciseTax.setInstallment(inputExciseTax.getInstallment());
        exciseTax.setDebitAmount(inputExciseTax.getDebit());
        // TODO ? exciseTax.setMunicipality(dv);
        return exciseTax;
    }
}
