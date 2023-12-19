package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.f24.service.pdf.util.FieldEnum.*;

public class ExciseMetadataFieldsToPdfFieldsMapper extends MetadataFieldsToPdfFieldsMapper {
    private final List<IntegratedField> integratedField = new ArrayList<>();
    private final String F24_EXCISE = "f24Excise.";

    @Override
    public List<IntegratedField> connectMetadataFieldsToPdfFields(F24Metadata f24Metadata) {
        F24Excise f24Excise = f24Metadata.getF24Excise();
        if (f24Excise == null) {
            return integratedField;
        }
        mapTaxPayerSection(f24Excise);
        mapTreasurySection(f24Excise);
        mapInpsSection(f24Excise);
        mapRegionSection(f24Excise);
        mapLocalTaxSection(f24Excise);
        mapExciseSection(f24Excise);

        return integratedField;

    }

    private void mapTaxPayerSection(F24Excise f24Excise) {
        TaxPayerExcise taxPayerExcise = f24Excise.getTaxPayer();
        if (taxPayerExcise == null) {
            return;
        }
        String TAX_PAYER = "taxPayer.";
        if (StringUtils.isNotBlank(taxPayerExcise.getTaxCode())) {
            integratedField.add(new IntegratedField(TAX_CODE.getName(), F24_EXCISE + TAX_PAYER + "taxCode", taxPayerExcise.getTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerExcise.getId())) {
            integratedField.add(new IntegratedField(ID_CODE.getName(), F24_EXCISE + TAX_PAYER + "id", taxPayerExcise.getId()));
        }
        if (StringUtils.isNotBlank(taxPayerExcise.getRelativePersonTaxCode())) {
            integratedField.add(new IntegratedField(RELATIVE_PERSON_TAX_CODE.getName(), F24_EXCISE + TAX_PAYER + "relativePersonTaxCode", taxPayerExcise.getRelativePersonTaxCode()));
        }
        if (taxPayerExcise.getIsNotTaxYear() != null) {
            integratedField.add(new IntegratedField(IS_NOT_TAX_YEAR.getName(), F24_EXCISE + TAX_PAYER + "isNotTaxYear", convertBooleanToString(taxPayerExcise.getIsNotTaxYear())));
        }
        if (taxPayerExcise.getCompany() != null) {
            integratedField.addAll(mapCompanyData(taxPayerExcise.getCompany(), F24_EXCISE + TAX_PAYER + "company"));
        }
        if (taxPayerExcise.getPerson() != null) {
            integratedField.addAll(mapPersonData(taxPayerExcise.getPerson(), F24_EXCISE + TAX_PAYER + "person"));
        }
    }

    private void mapTreasurySection(F24Excise f24Excise) {
        TreasurySection treasurySection = f24Excise.getTreasury();
        if (treasurySection == null) {
            return;
        }
        if (StringUtils.isNotBlank(treasurySection.getOffice())) {
            integratedField.add(new IntegratedField(OFFICE_CODE.getName(), F24_EXCISE + "treasury.office", treasurySection.getOffice()));
        }
        if (StringUtils.isNotBlank(treasurySection.getDocument())) {
            integratedField.add(new IntegratedField(DOCUMENT_CODE.getName(), F24_EXCISE + "treasury.document", treasurySection.getDocument()));
        }
        if (treasurySection.getRecords() != null && !treasurySection.getRecords().isEmpty()) {
            integratedField.addAll(mapTax(treasurySection, F24_EXCISE + "treasury.records"));
        }
    }

    private void mapInpsSection(F24Excise f24Excise) {
        InpsSection inpsSection = f24Excise.getInps();
        if (inpsSection == null) {
            return;
        }
        integratedField.addAll(mapInps(inpsSection, F24_EXCISE + "inps.records"));
    }

    private void mapRegionSection(F24Excise f24Excise) {
        RegionSection regionSection = f24Excise.getRegion();
        if (regionSection == null) {
            return;
        }
        integratedField.addAll(mapRegionSection(regionSection, F24_EXCISE + "region.records"));
    }

    private void mapLocalTaxSection(F24Excise f24Excise) {
        LocalTaxSection localTaxSection = f24Excise.getLocalTax();
        if (localTaxSection == null) {
            return;
        }
        if (StringUtils.isNotBlank(localTaxSection.getOperationId())) {
            integratedField.add(new IntegratedField(OPERATION_ID.getName(), F24_EXCISE + "localTax.operationId", localTaxSection.getOperationId()));
        }
        if (StringUtils.isNotBlank(localTaxSection.getDeduction())) {
            integratedField.add(new IntegratedField(DEDUCTION.getName(), F24_EXCISE + "localTax.deduction", localTaxSection.getDeduction()));
        }

        integratedField.addAll(mapLocalTaxSection(localTaxSection, F24_EXCISE + "localTax.records"));
    }

    private void mapExciseSection(F24Excise f24Excise) {
        ExciseSection exciseSection = f24Excise.getExcise();
        if (exciseSection == null) {
            return;
        }
        if (StringUtils.isNotBlank(exciseSection.getOffice())) {
            integratedField.add(new IntegratedField(OFFICE_CODE.getName(), "F24Excise.excise.office", exciseSection.getOffice()));
        }
        if (StringUtils.isNotBlank(exciseSection.getDocument())) {
            integratedField.add(new IntegratedField(DOCUMENT_CODE.getName(), "F24Excise.excise.document", exciseSection.getDocument()));
        }

        mapExciseTax(exciseSection);
    }

    private void mapExciseTax(ExciseSection exciseSection) {
        if (exciseSection.getRecords() != null || !exciseSection.getRecords().isEmpty()) {
            for (int i = 0; i < exciseSection.getRecords().size(); i++) {
                ExciseTax exciseTax = exciseSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(exciseTax.getInstitution())) {
                    integratedField.add(new IntegratedField(MUNICIPALITY_CODE.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "institution", exciseTax.getInstitution()));
                }
                if (StringUtils.isNotBlank(exciseTax.getId())) {
                    integratedField.add(new IntegratedField(ID_ELEMENT.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "id", exciseTax.getId()));
                }
                if (StringUtils.isNotBlank(exciseTax.getProvince())) {
                    integratedField.add(new IntegratedField(TAX_PROVINCE.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "province", exciseTax.getProvince()));
                }
                if (StringUtils.isNotBlank(exciseTax.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "taxType", exciseTax.getTaxType()));
                }
                if (StringUtils.isNotBlank(exciseTax.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "installment", exciseTax.getInstallment()));
                }
                if (StringUtils.isNotBlank(exciseTax.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "year", exciseTax.getYear()));
                }
                if (exciseTax.getDebit() != null)
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, F24_EXCISE + "excise.records[" + i + "]" + "debit", exciseTax.getDebit().toString(), PdfFieldFormatter.IMPORT));
            }
        }
    }
}
