package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.f24.dto.form.F24Form;

public abstract class F24Converter {
    public abstract F24Form convert(F24Metadata f24Metadata);

    protected org.f24.dto.component.TreasurySection convertTreasurySection(TreasurySection inputTreasurySection) {
        org.f24.dto.component.TreasurySection treasurySection = new org.f24.dto.component.TreasurySection();
        treasurySection.setOfficeCode(inputTreasurySection.getOffice());
        treasurySection.setDocumentCode(inputTreasurySection.getDocument());
        if(inputTreasurySection.getRecords() != null) {
            treasurySection.setTaxList(
                    inputTreasurySection.getRecords().stream()
                            .map(this::convertTreasuryRecord)
                            .toList()
            );
        }
        return treasurySection;
    }

    private org.f24.dto.component.Tax convertTreasuryRecord(Tax tax) {
        org.f24.dto.component.Tax taxRecord = new org.f24.dto.component.Tax();
        taxRecord.setCreditAmount(tax.getCredit());
        taxRecord.setDebitAmount(tax.getDebit());
        taxRecord.setInstallment(tax.getInstallment());
        taxRecord.setYear(tax.getYear());
        taxRecord.setTaxTypeCode(tax.getTaxType());
        return taxRecord;
    }

    protected org.f24.dto.component.InpsSection convertInpsSection(InpsSection dvInpsSection) {
        org.f24.dto.component.InpsSection inpsSection = new org.f24.dto.component.InpsSection();
        if(dvInpsSection.getRecords() != null) {
            inpsSection.setInpsRecordList(
                    dvInpsSection.getRecords()
                            .stream()
                            .map(this::convertInpsRecord)
                            .toList()
            );
        }
        return inpsSection;
    }

    private org.f24.dto.component.InpsRecord convertInpsRecord(InpsRecord inputInpsRecord) {
        org.f24.dto.component.InpsRecord inpsRecord = new org.f24.dto.component.InpsRecord();
        inpsRecord.setInpsCode(inputInpsRecord.getInps());
        inpsRecord.setOfficeCode(inputInpsRecord.getOffice());
        inpsRecord.setPeriod(this.convertPeriod(inputInpsRecord.getPeriod()));
        inpsRecord.setContributionReason(inputInpsRecord.getReason());
        inpsRecord.setCreditAmount(inputInpsRecord.getCredit());
        inpsRecord.setDebitAmount(inputInpsRecord.getDebit());
        return inpsRecord;
    }

    protected org.f24.dto.component.Period convertPeriod(Period period) {
        org.f24.dto.component.Period outputPeriod = new org.f24.dto.component.Period();

        if(period == null) {
            return outputPeriod;
        }

        if(!StringUtils.isEmpty(period.getStartDate())) {
            outputPeriod.setStartDate(period.getStartDate());
        }

        if(!StringUtils.isEmpty(period.getEndDate())) {
            outputPeriod.setEndDate(period.getEndDate());
        }

        return outputPeriod;
    }

    protected org.f24.dto.component.RegionSection convertRegionSection(RegionSection inputRegionSection) {
        org.f24.dto.component.RegionSection regionSection = new org.f24.dto.component.RegionSection();
        if(inputRegionSection.getRecords() != null) {
            regionSection.setRegionRecordList(
                    inputRegionSection.getRecords()
                            .stream()
                            .map(this::convertRegionRecord)
                            .toList()
            );
        }
        return regionSection;
    }

    private org.f24.dto.component.RegionRecord convertRegionRecord(RegionRecord dvRegionRecord) {
        org.f24.dto.component.RegionRecord regionRecord = new org.f24.dto.component.RegionRecord();
        regionRecord.setInstallment(dvRegionRecord.getInstallment());
        regionRecord.setRegionCode(dvRegionRecord.getRegion());
        regionRecord.setYear(dvRegionRecord.getYear());
        regionRecord.setTaxTypeCode(dvRegionRecord.getTaxType());
        regionRecord.setCreditAmount(dvRegionRecord.getCredit());
        regionRecord.setDebitAmount(dvRegionRecord.getDebit());
        return regionRecord;
    }

    protected org.f24.dto.component.LocalTaxSection convertLocalTaxSection(LocalTaxSection inputLocalTaxSection) {
        org.f24.dto.component.LocalTaxSection localTaxSection = new org.f24.dto.component.LocalTaxSection();
        localTaxSection.setOperationId(inputLocalTaxSection.getOperationId());
        localTaxSection.setDeduction(inputLocalTaxSection.getDeduction());
        if(inputLocalTaxSection.getRecords() != null) {
            localTaxSection.setLocalTaxRecordList(
                    inputLocalTaxSection.getRecords()
                            .stream()
                            .map(this::convertLocalTaxRecord)
                            .toList()
            );
        }

        return localTaxSection;
    }

    private org.f24.dto.component.LocalTaxRecord convertLocalTaxRecord(LocalTaxRecord inputLocalTaxRecord) {
        org.f24.dto.component.LocalTaxRecord localTaxRecord = new org.f24.dto.component.LocalTaxRecord();
        localTaxRecord.setAdvancePayment(inputLocalTaxRecord.getAdvancePayment());
        localTaxRecord.setFullPayment(inputLocalTaxRecord.getFullPayment());
        localTaxRecord.setMunicipalityCode(inputLocalTaxRecord.getMunicipality());
        localTaxRecord.setNumberOfProperties(inputLocalTaxRecord.getNumberOfProperties());
        localTaxRecord.setPropertiesChanges(inputLocalTaxRecord.getPropertiesChanges());
        localTaxRecord.setReconsideration(inputLocalTaxRecord.getReconsideration());
        localTaxRecord.setInstallment(inputLocalTaxRecord.getInstallment());
        localTaxRecord.setTaxTypeCode(inputLocalTaxRecord.getTaxType());
        localTaxRecord.setYear(inputLocalTaxRecord.getYear());
        localTaxRecord.setDebitAmount(inputLocalTaxRecord.getDebit());
        localTaxRecord.setCreditAmount(inputLocalTaxRecord.getCredit());
        return localTaxRecord;
    }

    protected org.f24.dto.component.CompanyData convertCompanyData(CompanyData company) {
        org.f24.dto.component.CompanyData companyData = new org.f24.dto.component.CompanyData();
        companyData.setName(company.getName());
        if(company.getTaxAddress() != null) {
            companyData.setTaxAddress(this.convertTaxAddress(company.getTaxAddress()));
        }
        return companyData;
    }

    private org.f24.dto.component.TaxAddress convertTaxAddress(TaxAddress dvTaxAddress) {
        org.f24.dto.component.TaxAddress taxAddress = new org.f24.dto.component.TaxAddress();
        taxAddress.setAddress(dvTaxAddress.getAddress());
        taxAddress.setMunicipality(dvTaxAddress.getMunicipality());
        taxAddress.setProvince(dvTaxAddress.getProvince());
        return taxAddress;
    }

    protected org.f24.dto.component.PersonData convertPersonData(PersonData person) {
        org.f24.dto.component.PersonData personData = new org.f24.dto.component.PersonData();
        if(person.getTaxAddress() != null) {
            personData.setTaxAddress(convertTaxAddress(person.getTaxAddress()));
        }
        if(person.getPersonalData() != null) {
            personData.setPersonalData(this.convertPersonalData(person.getPersonalData()));
        }
        return personData;
    }

    protected org.f24.dto.component.PersonalData convertPersonalData(PersonalData dvPersonalData) {
        org.f24.dto.component.PersonalData personalData = new org.f24.dto.component.PersonalData();
        personalData.setName(dvPersonalData.getName());
        personalData.setSurname(dvPersonalData.getSurname());
        personalData.setSex(dvPersonalData.getSex());
        personalData.setBirthDate(dvPersonalData.getBirthDate());
        personalData.setBirthPlace(dvPersonalData.getBirthPlace());
        personalData.setBirthProvince(dvPersonalData.getBirthProvince());
        return personalData;
    }
}
