package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.f24.service.pdf.util.FieldEnum.*;

public class StandardMetadataFieldsToPdfFieldsMapper extends MetadataFieldsToPdfFieldsMapper {
    private final List<IntegratedField> integratedField = new ArrayList<>();
    private final String F24_STANDARD = "f24Standard.";

    @Override
    public List<IntegratedField> connectMetadataFieldsToPdfFields(F24Metadata f24Metadata) {
        F24Standard f24Standard = f24Metadata.getF24Standard();
        if (f24Standard == null) {
            return integratedField;
        }
        mapTaxPayerSection(f24Standard);
        mapTreasurySection(f24Standard,"1");
        mapInpsSection(f24Standard,"2");
        mapRegionSection(f24Standard,"3");
        mapLocalTaxSection(f24Standard,"4");
        mapSocialSecuritySection(f24Standard);

        return integratedField;

    }

    private void mapTaxPayerSection(F24Standard f24Standard) {
        TaxPayerStandard taxPayerStandard = f24Standard.getTaxPayer();
        if (taxPayerStandard == null) {
            return;
        }
        String TAX_PAYER = "taxPayer.";
        if (StringUtils.isNotBlank(taxPayerStandard.getTaxCode())) {
            integratedField.add(new IntegratedField(TAX_CODE.getName(), F24_STANDARD + TAX_PAYER + "taxCode", taxPayerStandard.getTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerStandard.getId())) {
            integratedField.add(new IntegratedField(ID_CODE.getName(), F24_STANDARD + TAX_PAYER + "id", taxPayerStandard.getId()));
        }
        if (StringUtils.isNotBlank(taxPayerStandard.getRelativePersonTaxCode())) {
            integratedField.add(new IntegratedField(RELATIVE_PERSON_TAX_CODE.getName(), F24_STANDARD + TAX_PAYER + "relativePersonTaxCode", taxPayerStandard.getRelativePersonTaxCode()));
        }
        if (taxPayerStandard.getIsNotTaxYear() != null) {
            integratedField.add(new IntegratedField(IS_NOT_TAX_YEAR.getName(), F24_STANDARD + TAX_PAYER + "isNotTaxYear", convertBooleanToString(taxPayerStandard.getIsNotTaxYear())));
        }
        if (taxPayerStandard.getCompany() != null) {
            integratedField.addAll(mapCompanyData(taxPayerStandard.getCompany(), F24_STANDARD + TAX_PAYER + "company"));
        }
        if (taxPayerStandard.getPerson() != null) {
            integratedField.addAll(mapPersonData(taxPayerStandard.getPerson(), F24_STANDARD + TAX_PAYER + "person"));
        }
    }

    private void mapTreasurySection(F24Standard f24Standard, String sectionId) {
        TreasurySection treasurySection = f24Standard.getTreasury();
        if (treasurySection == null) {
            return;
        }
        if (StringUtils.isNotBlank(treasurySection.getOffice())) {
            integratedField.add(new IntegratedField(OFFICE_CODE.getName()+sectionId, F24_STANDARD + "treasury.office", treasurySection.getOffice()));
        }
        if (StringUtils.isNotBlank(treasurySection.getDocument())) {
            integratedField.add(new IntegratedField(DOCUMENT_CODE.getName()+sectionId, F24_STANDARD + "treasury.document", treasurySection.getDocument()));
        }
        if (treasurySection.getRecords() != null && !treasurySection.getRecords().isEmpty()) {
            integratedField.addAll(mapTax(treasurySection, F24_STANDARD + "treasury.records",sectionId));
        }
    }

    private void mapInpsSection(F24Standard f24Standard, String sectionId) {
        InpsSection inpsSection = f24Standard.getInps();
        if (inpsSection == null) {
            return;
        }
        integratedField.addAll(mapInps(inpsSection, F24_STANDARD + "inps.records",sectionId));
    }
    private void mapRegionSection(F24Standard f24Standard, String sectionId) {
        RegionSection regionSection = f24Standard.getRegion();
        if (regionSection == null) {
            return;
        }
        integratedField.addAll(mapRegionSection(regionSection, F24_STANDARD + "region.records",sectionId));
    }

    private void mapLocalTaxSection(F24Standard f24Standard,String sectionId) {
        LocalTaxSection localTaxSection = f24Standard.getLocalTax();
        if (localTaxSection == null) {
            return;
        }
        if (StringUtils.isNotBlank(localTaxSection.getOperationId())) {
            integratedField.add(new IntegratedField(OPERATION_ID.getName(), F24_STANDARD + "localTax.operationId", localTaxSection.getOperationId()));
        }
        if (localTaxSection.getDeduction() != null  && !localTaxSection.getDeduction().isEmpty()) {
            Double parsedDeduction = Double.parseDouble(localTaxSection.getDeduction());
            integratedField.addAll(setMultiField(DEDUCTION.getName(), parsedDeduction, F24_STANDARD + "localTax.deduction"));
        }

        integratedField.addAll(mapLocalTaxSection(localTaxSection, F24_STANDARD + "localTax.records",sectionId));
    }

    private void mapSocialSecuritySection(F24Standard f24Standard) {
        SocialSecuritySection socialSecuritySection = f24Standard.getSocialSecurity();
        if (socialSecuritySection == null) {
            return;
        }
        mapSocSecRecords(socialSecuritySection,"6");
        mapInailRecord(socialSecuritySection,"5");

    }

    private void mapSocSecRecords(SocialSecuritySection socialSecuritySection, String sectionId) {
        if (socialSecuritySection.getSocSecRecords() != null || !socialSecuritySection.getSocSecRecords().isEmpty()) {
            for (int i = 0; i < socialSecuritySection.getSocSecRecords().size(); i++) {
                SocialSecurityRecord socialSecurityRecord = socialSecuritySection.getSocSecRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(socialSecurityRecord.getOffice())) {
                    integratedField.add(new IntegratedField(OFFICE_CODE.getName()+sectionId + index, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "office", socialSecurityRecord.getOffice()));
                }
                if (StringUtils.isNotBlank(socialSecurityRecord.getPosition())) {
                    integratedField.add(new IntegratedField(POSITION_CODE.getName()+sectionId + index, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "position", socialSecurityRecord.getPosition()));
                }
                if (StringUtils.isNotBlank(socialSecurityRecord.getInstitution())) {
                    integratedField.add(new IntegratedField(MUNICIPALITY_CODE.getName()+sectionId, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "institution", socialSecurityRecord.getInstitution()));
                }
                if (StringUtils.isNotBlank(socialSecurityRecord.getReason())) {
                    integratedField.add(new IntegratedField(CONTRIBUTION_REASON.getName()+sectionId + index, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "reason", socialSecurityRecord.getReason()));
                }
                if (socialSecurityRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "debit", socialSecurityRecord.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (socialSecurityRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "credit", socialSecurityRecord.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
                integratedField.addAll(mapPeriod(socialSecurityRecord.getPeriod(), F24_STANDARD + "socialSecurity.socSecRecords[" + i + "]." + "period.", index ,sectionId));
            }
        }
    }

    private void mapInailRecord(SocialSecuritySection socialSecuritySection,String sectionId) {
        if (socialSecuritySection.getRecords() != null || !socialSecuritySection.getRecords().isEmpty()) {
            for (int i = 0; i < socialSecuritySection.getRecords().size(); i++) {
                int index = i + 1;
                InailRecord inailRecord = socialSecuritySection.getRecords().get(i);
                if (StringUtils.isNotBlank(inailRecord.getOffice())) {
                    integratedField.add(new IntegratedField(OFFICE_CODE.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "office", inailRecord.getOffice()));
                }
                if (StringUtils.isNotBlank(inailRecord.getCompany())) {
                    integratedField.add(new IntegratedField(COMPANY_CODE.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "company", inailRecord.getCompany()));
                }
                if (StringUtils.isNotBlank(inailRecord.getControl())) {
                    integratedField.add(new IntegratedField(CONTROL_CODE.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "control", inailRecord.getControl()));
                }
                if (StringUtils.isNotBlank(inailRecord.getRefNumber())) {
                    integratedField.add(new IntegratedField(REFERENCE_NUMBER.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "refNumber", inailRecord.getRefNumber()));
                }
                if (StringUtils.isNotBlank(inailRecord.getReason())) {
                    integratedField.add(new IntegratedField(REASON.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "reason", inailRecord.getReason()));
                }
                if (inailRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "debit", inailRecord.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (inailRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, F24_STANDARD + "socialSecurity.records[" + i + "]." + "credit", inailRecord.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
    }
}

