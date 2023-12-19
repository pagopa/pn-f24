package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.f24.service.pdf.util.FieldEnum.*;


public class SimplifiedMetadataFieldsToPdfFieldsMapper extends MetadataFieldsToPdfFieldsMapper {
    private final List<IntegratedField> integratedField = new ArrayList<>();
    private final String F24_SIMPLIFIED = "f24Simplified.";

    @Override
    public List<IntegratedField> connectMetadataFieldsToPdfFields(F24Metadata f24Metadata) {
        F24Simplified f24Simplified = f24Metadata.getF24Simplified();
        if (f24Simplified == null) {
            return integratedField;
        }
        mapTaxPayerSection(f24Simplified);
        mapSimplifiedPaymentSection(f24Simplified);
        return integratedField;
    }

    private void mapTaxPayerSection(F24Simplified f24Simplified) {
        TaxPayerSimplified taxPayerSimplified = f24Simplified.getTaxPayer();
        if (taxPayerSimplified == null) {
            return;
        }
        String TAX_PAYER = "taxPayer.";
        if (StringUtils.isNotBlank(taxPayerSimplified.getTaxCode())) {
            integratedField.add(new IntegratedField(TAX_CODE.getName(), F24_SIMPLIFIED + TAX_PAYER + "taxCode", taxPayerSimplified.getTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerSimplified.getRelativePersonTaxCode())) {
            integratedField.add(new IntegratedField(RELATIVE_PERSON_TAX_CODE.getName(), F24_SIMPLIFIED + TAX_PAYER + "corporateName", taxPayerSimplified.getRelativePersonTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerSimplified.getId())) {
            integratedField.add(new IntegratedField(ID_CODE.getName(), F24_SIMPLIFIED + TAX_PAYER + "id", taxPayerSimplified.getId()));
        }
        if (StringUtils.isNotBlank(taxPayerSimplified.getDocument())) {
            integratedField.add(new IntegratedField(DOCUMENT_CODE.getName(), F24_SIMPLIFIED + TAX_PAYER + "document", taxPayerSimplified.getDocument()));
        }
        if (StringUtils.isNotBlank(taxPayerSimplified.getOffice())) {
            integratedField.add(new IntegratedField(OFFICE_CODE.getName(), F24_SIMPLIFIED + TAX_PAYER + "office", taxPayerSimplified.getOffice()));
        }
        if (taxPayerSimplified.getPersonalData() != null) {
            integratedField.addAll(mapPersonalData(taxPayerSimplified.getPersonalData(), F24_SIMPLIFIED + TAX_PAYER + "person."));
        }
    }

    private void mapSimplifiedPaymentSection(F24Simplified f24Simplified) {
        SimplifiedPaymentSection simplifiedPaymentSection = f24Simplified.getPayments();
        if (simplifiedPaymentSection == null) {
            return;
        }
        if (StringUtils.isNotBlank(simplifiedPaymentSection.getOperationId())) {
            integratedField.add(new IntegratedField(OPERATION_ID.getName(), F24_SIMPLIFIED + "payments.operationId", simplifiedPaymentSection.getOperationId()));
        }
        if (!simplifiedPaymentSection.getRecords().isEmpty())
            for (int i = 0; i < simplifiedPaymentSection.getRecords().size(); i++) {
                SimplifiedPaymentRecord record = simplifiedPaymentSection.getRecords().get(i);
                int index= i + 1;
                if (StringUtils.isNotBlank(record.getMunicipality())) {
                    integratedField.add(new IntegratedField(MUNICIPALITY_CODE.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].municipality", record.getMunicipality()));
                }
                if (record.getReconsideration() != null) {
                    integratedField.add(new IntegratedField(RECONSIDERATION.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].reconsideration", convertBooleanToString(record.getReconsideration())));
                }
                if (record.getPropertiesChanges() != null) {
                    integratedField.add(new IntegratedField(PROPERTIES_CHANGED.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].propertiesChanges", convertBooleanToString(record.getPropertiesChanges())));
                }
                if (record.getAdvancePayment() != null) {
                    integratedField.add(new IntegratedField(ADVANCE_PAYMENT.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].advancePayment", convertBooleanToString(record.getAdvancePayment())));
                }
                if (record.getFullPayment() != null) {
                    integratedField.add(new IntegratedField(FULL_PAYMENT.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].fullPayment", convertBooleanToString(record.getFullPayment())));
                }
                if (StringUtils.isNotBlank(record.getNumberOfProperties())) {
                    integratedField.add(new IntegratedField(NUMBER_OF_PROPERTIES.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].numberOfProperties", record.getNumberOfProperties()));
                }
                if (StringUtils.isNotBlank(record.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].taxType", record.getTaxType()));
                }
                if (StringUtils.isNotBlank(record.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].installment", record.getInstallment()));
                }
                if (StringUtils.isNotBlank(record.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].year", record.getYear()));
                }
                if (record.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].debit", record.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (record.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].credit", record.getCredit().toString(),PdfFieldFormatter.IMPORT));
                }
                if (StringUtils.isNotBlank(record.getSection())) {
                    integratedField.add(new IntegratedField(SECTION.getName() + index, F24_SIMPLIFIED + "payments.records["+i+"].section", record.getSection()));
                }
            }
    }
}
