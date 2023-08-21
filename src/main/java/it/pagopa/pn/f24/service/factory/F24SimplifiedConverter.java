package it.pagopa.pn.f24.service.factory;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.f24.dto.component.PaymentReasonRecord;
import org.f24.dto.component.PaymentReasonSection;
import org.f24.dto.component.PersonData;
import org.f24.dto.component.TaxPayer;
import java.util.stream.Collectors;

public class F24SimplifiedConverter extends F24Converter {

    @Override
    public org.f24.dto.form.F24Simplified convert(F24Item f24Item) {
        org.f24.dto.form.F24Simplified formF24Simplified = new org.f24.dto.form.F24Simplified();
        F24Simplified f24Simplified = f24Item.getF24Simplified();

        if(f24Simplified == null) {
            return formF24Simplified;
        }

        if(f24Simplified.getTaxPayer() != null) {
            formF24Simplified.setTaxPayer(this.convertTaxPayer(f24Simplified.getTaxPayer()));
        }

        if(f24Simplified.getPayments() != null) {
            formF24Simplified.setPaymentReasonSection(this.convertPaymentReasonSection(f24Simplified.getPayments()));
        }
        return formF24Simplified;
    }

    private TaxPayer convertTaxPayer(TaxPayerSimplified dvTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setIdCode(dvTaxPayer.getId());
        //TODO ? taxPayer.setIsNotTaxYear(dvTaxPayer.g);
        taxPayer.setTaxCode(dvTaxPayer.getTaxCode());
        taxPayer.setOfficeCode(dvTaxPayer.getOffice());
        taxPayer.setDocumentCode(dvTaxPayer.getDocument());
        taxPayer.setRelativePersonTaxCode(dvTaxPayer.getRelativePersonTaxCode());
        //TODO ? taxPayer.setCompanyData();

        if(dvTaxPayer.getPersonalData() != null) {
            // TODO è giusto andare a settare solo i PersonData dentro i Personal?
            PersonData personData = new PersonData();
            personData.setPersonalData(this.convertPersonalData(dvTaxPayer.getPersonalData()));
            taxPayer.setPersonData(personData);
        }
        return taxPayer;
    }

    private PaymentReasonSection convertPaymentReasonSection(SimplifiedPaymentSection payments) {
        PaymentReasonSection paymentReasonSection = new PaymentReasonSection();
        paymentReasonSection.setOperationId(payments.getOperationId());
        if(payments.getRecords() != null) {
            paymentReasonSection.setReasonRecordList(payments.getRecords().stream().map(this::convertReasonRecord).collect(Collectors.toList()));
        }
        //TODO ? paymentReasonSection.setRecordList();
        return paymentReasonSection;
    }

    private PaymentReasonRecord convertReasonRecord(SimplifiedPaymentRecord record) {
        PaymentReasonRecord paymentReasonRecord = new PaymentReasonRecord();
        paymentReasonRecord.setSection(record.getSection());
        /*
        TODO ?
        paymentReasonRecord.setAdvancePayment();
        paymentReasonRecord.setFullPayment();
        paymentReasonRecord.setMonth();
        paymentReasonRecord.setYear();
        paymentReasonRecord.setReconsideration();
        paymentReasonRecord.setMunicipalityCode();
        paymentReasonRecord.setPropertiesChanges();
        paymentReasonRecord.setTaxTypeCode();
        paymentReasonRecord.setNumberOfProperties();
        */
        return paymentReasonRecord;
    }
}
