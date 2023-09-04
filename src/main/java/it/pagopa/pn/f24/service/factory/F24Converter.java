package it.pagopa.pn.f24.service.factory;

import it.pagopa.pn.f24.dto.metadata.*;
import org.apache.commons.lang3.StringUtils;
import org.f24.dto.form.F24Form;

import java.util.stream.Collectors;

public abstract class F24Converter {
    public abstract F24Form convert(F24Item f24Item);

    protected org.f24.dto.component.TreasurySection convertTreasurySection(TreasurySection dvTreasurySection) {
        org.f24.dto.component.TreasurySection treasurySection = new org.f24.dto.component.TreasurySection();
        treasurySection.setOfficeCode(dvTreasurySection.getOffice());
        treasurySection.setDocumentCode(dvTreasurySection.getDocument());
        //TODO ? treasurySection.setTaxList();
        if(dvTreasurySection.getRecords() != null) {
            treasurySection.setRecordList(dvTreasurySection.getRecords().stream().map(this::convertTreasuryRecord).collect(Collectors.toList()));
        }
        return treasurySection;
    }

    private org.f24.dto.component.Record convertTreasuryRecord(Tax tax) {
        org.f24.dto.component.Record record = new org.f24.dto.component.Record();
        //TODO ? record.setDeduction(tax);
        record.setCreditAmount(tax.getCredit());
        record.setDebitAmount(tax.getDebit());
        //TODO ? tax.getTaxType();
        //TODO ? tax.getInstallment();
        //TODO ? tax.getYear();
        return record;
    }

    protected org.f24.dto.component.InpsSection convertInpsSection(InpsSection dvInpsSection) {
        org.f24.dto.component.InpsSection inpsSection = new org.f24.dto.component.InpsSection();
        if(dvInpsSection.getRecords() != null) {
            inpsSection.setInpsRecordList(
                    dvInpsSection.getRecords()
                            .stream()
                            .map(this::convertInpsRecord)
                            .collect(Collectors.toList())
            );
        }
        //TODO ? inpsSection.setRecordList();
        return inpsSection;
    }

    private org.f24.dto.component.InpsRecord convertInpsRecord(InpsRecord dvInpsRecord) {
        org.f24.dto.component.InpsRecord inpsRecord = new org.f24.dto.component.InpsRecord();
        inpsRecord.setInpsCode(dvInpsRecord.getInps());
        inpsRecord.setOfficeCode(dvInpsRecord.getOffice());
        inpsRecord.setPeriod(this.convertPeriod(dvInpsRecord.getPeriod()));
        inpsRecord.setContributionReason(dvInpsRecord.getReason());
        inpsRecord.setCreditAmount(dvInpsRecord.getCredit());
        inpsRecord.setDebitAmount(dvInpsRecord.getDebit());
        //TODO inpsRecord.setDeduction(dv.g);
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

    protected org.f24.dto.component.RegionSection convertRegionSection(RegionSection dvRegionSection) {
        org.f24.dto.component.RegionSection regionSection = new org.f24.dto.component.RegionSection();
        if(dvRegionSection.getRecords() != null) {
            regionSection.setRegionRecordList(
                    dvRegionSection.getRecords()
                            .stream()
                            .map(this::convertRegionRecord)
                            .collect(Collectors.toList())
            );
        }
        //TODO ? regionSection.setRecordList();
        return regionSection;
    }

    private org.f24.dto.component.RegionRecord convertRegionRecord(RegionRecord dvRegionRecord) {
        org.f24.dto.component.RegionRecord regionRecord = new org.f24.dto.component.RegionRecord();
        regionRecord.setInstallment(dvRegionRecord.getInstallment());
        regionRecord.setRegionCode(dvRegionRecord.getRegion());
        regionRecord.setYear(dvRegionRecord.getYear());
        regionRecord.setTaxTypeCode(dvRegionRecord.getTaxType());
        //TODO ? regionRecord.setDeduction(dv);
        regionRecord.setCreditAmount(dvRegionRecord.getCredit());
        regionRecord.setDebitAmount(dvRegionRecord.getDebit());
        return regionRecord;
    }

    protected org.f24.dto.component.LocalTaxSection convertLocalTaxSection(LocalTaxSection dvLocalTaxSection) {
        org.f24.dto.component.LocalTaxSection localTaxSection = new org.f24.dto.component.LocalTaxSection();
        localTaxSection.setOperationId(dvLocalTaxSection.getOperationId());
        localTaxSection.setDeduction(dvLocalTaxSection.getDeduction());
        if(dvLocalTaxSection.getRecords() != null) {
            localTaxSection.setLocalTaxRecordList(
                    dvLocalTaxSection.getRecords()
                            .stream()
                            .map(this::convertLocalTaxRecord)
                            .collect(Collectors.toList())
            );
        }
        //TODO? localTaxSection.setRecordList(dvLocalTaxSection.get);

        return localTaxSection;
    }

    private org.f24.dto.component.LocalTaxRecord convertLocalTaxRecord(LocalTaxRecord dvLocalTaxRecord) {
        org.f24.dto.component.LocalTaxRecord localTaxRecord = new org.f24.dto.component.LocalTaxRecord();
        localTaxRecord.setAdvancePayment(dvLocalTaxRecord.getAdvancePayment());
        localTaxRecord.setFullPayment(dvLocalTaxRecord.getFullPayment());
        localTaxRecord.setMunicipalityCode(dvLocalTaxRecord.getMunicipality());
        localTaxRecord.setNumberOfProperties(dvLocalTaxRecord.getNumberOfProperties());
        localTaxRecord.setPropertiesChanges(dvLocalTaxRecord.getPropertiesChanges());
        localTaxRecord.setReconsideration(dvLocalTaxRecord.getReconsideration());
        localTaxRecord.setInstallment(dvLocalTaxRecord.getInstallment());
        localTaxRecord.setTaxTypeCode(dvLocalTaxRecord.getTaxType());
        localTaxRecord.setYear(dvLocalTaxRecord.getYear());
        localTaxRecord.setDebitAmount(dvLocalTaxRecord.getDebit());
        //TODO localTaxRecord.setDeduction(dvLocalTaxRecord.get);
        localTaxRecord.setCreditAmount(dvLocalTaxRecord.getCredit());
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
