package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.business.F24Converter;
import org.f24.dto.component.CompanyData;
import org.f24.dto.component.PaymentReasonRecord;
import org.f24.dto.component.PaymentReasonSection;
import org.f24.dto.component.PersonData;
import org.f24.dto.component.TaxPayer;

public class F24SimplifiedConverter extends F24Converter {

    @Override
    public org.f24.dto.form.F24Simplified convert(F24Metadata f24Metadata) {
        org.f24.dto.form.F24Simplified formF24Simplified = new org.f24.dto.form.F24Simplified();
        F24Simplified f24Simplified = f24Metadata.getF24Simplified();

        if (f24Simplified == null) {
            return formF24Simplified;
        }

        if (f24Simplified.getTaxPayer() != null) {
            formF24Simplified.setTaxPayer(this.convertTaxPayer(f24Simplified.getTaxPayer()));
        }

        if (f24Simplified.getPayments() != null) {
            formF24Simplified.setPaymentReasonSection(this.convertPaymentReasonSection(f24Simplified.getPayments()));
        }
        return formF24Simplified;
    }

    private TaxPayer convertTaxPayer(TaxPayerSimplified inputTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setIdCode(inputTaxPayer.getId());
        taxPayer.setTaxCode(inputTaxPayer.getTaxCode());
        taxPayer.setOfficeCode(inputTaxPayer.getOffice());
        taxPayer.setDocumentCode(inputTaxPayer.getDocument());
        taxPayer.setRelativePersonTaxCode(inputTaxPayer.getRelativePersonTaxCode());

        if (inputTaxPayer.getPersonalData() != null) {
            PersonData personData = new PersonData();
            personData.setPersonalData(this.convertPersonalData(inputTaxPayer.getPersonalData()));
            taxPayer.setPersonData(personData);
        }
        if (inputTaxPayer.getCompany() != null) {
            taxPayer.setCompanyData(this.convertCompany(inputTaxPayer.getCompany()));
        }
        return taxPayer;
    }

    private CompanyData convertCompany(CompanyDataSimplified company) {
        CompanyData companyData = new CompanyData();
        companyData.setName(company.getName());
        return companyData;
    }


    private PaymentReasonSection convertPaymentReasonSection(SimplifiedPaymentSection payments) {
        PaymentReasonSection paymentReasonSection = new PaymentReasonSection();
        paymentReasonSection.setOperationId(payments.getOperationId());
        if (payments.getRecords() != null) {
            paymentReasonSection.setReasonRecordList(
                    payments.getRecords()
                            .stream()
                            .map(this::convertReasonRecord)
                            .toList());
        }
        return paymentReasonSection;
    }

    private PaymentReasonRecord convertReasonRecord(SimplifiedPaymentRecord simplifiedPaymentRecord) {
        PaymentReasonRecord paymentReasonRecord = new PaymentReasonRecord();
        paymentReasonRecord.setSection(simplifiedPaymentRecord.getSection());
        paymentReasonRecord.setAdvancePayment(simplifiedPaymentRecord.getAdvancePayment());
        paymentReasonRecord.setFullPayment(simplifiedPaymentRecord.getFullPayment());
        paymentReasonRecord.setYear(simplifiedPaymentRecord.getYear());
        paymentReasonRecord.setReconsideration(simplifiedPaymentRecord.getReconsideration());
        paymentReasonRecord.setMunicipalityCode(simplifiedPaymentRecord.getMunicipality());
        paymentReasonRecord.setPropertiesChanges(simplifiedPaymentRecord.getPropertiesChanges());
        paymentReasonRecord.setTaxTypeCode(simplifiedPaymentRecord.getTaxType());
        paymentReasonRecord.setNumberOfProperties(simplifiedPaymentRecord.getNumberOfProperties());
        paymentReasonRecord.setMonth(simplifiedPaymentRecord.getInstallment());
        paymentReasonRecord.setDebitAmount(integerToString(simplifiedPaymentRecord.getDebit()));
        paymentReasonRecord.setCreditAmount(integerToString(simplifiedPaymentRecord.getCredit()));
        return paymentReasonRecord;
    }
}
