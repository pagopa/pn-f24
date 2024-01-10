package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.f24.service.pdf.util.FieldEnum.*;


public class ElidMetadataFieldsToPdfFieldsMapper extends MetadataFieldsToPdfFieldsMapper {
    private final List<IntegratedField> integratedField = new ArrayList<>();
    private final String F24_ELID = "f24Elid.";

    @Override
    public List<IntegratedField> connectMetadataFieldsToPdfFields(F24Metadata f24Metadata) {
        F24Elid f24Elid = f24Metadata.getF24Elid();
        if (f24Elid == null) {
            return integratedField;
        }
        mapTaxPayerElid(f24Elid);
        mapTreasuryAndOtherSection(f24Elid);

        return integratedField;
    }

    private void mapTaxPayerElid(F24Elid f24Elid) {
        TaxPayerElide taxPayerElide = f24Elid.getTaxPayer();
        String TAX_PAYER = "taxPayer.";
        if (StringUtils.isNotBlank(taxPayerElide.getTaxCode())) {
            integratedField.add(new IntegratedField(TAX_CODE.getName(), F24_ELID + TAX_PAYER + "taxCode", f24Elid.getTaxPayer().getTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerElide.getRelativePersonTaxCode())) {
            integratedField.add(new IntegratedField(RELATIVE_PERSON_TAX_CODE.getName(), F24_ELID + TAX_PAYER + "corporateName", f24Elid.getTaxPayer().getRelativePersonTaxCode()));
        }
        if (StringUtils.isNotBlank(taxPayerElide.getId())) {
            integratedField.add(new IntegratedField(ID_CODE.getName(), F24_ELID + TAX_PAYER + "id", f24Elid.getTaxPayer().getId()));
        }
        if (taxPayerElide.getPerson() != null) {
            integratedField.addAll(mapPersonData(taxPayerElide.getPerson(), F24_ELID + TAX_PAYER + "person"));
        }
        if (taxPayerElide.getCompany() != null) {
            integratedField.addAll(mapCompanyData(taxPayerElide.getCompany(), F24_ELID + TAX_PAYER + "company"));
        }
    }

    private void mapTreasuryAndOtherSection(F24Elid f24Elid) {
        TreasuryAndOtherSection treasuryAndOtherSection = f24Elid.getTreasury();
        if (StringUtils.isNotBlank(treasuryAndOtherSection.getOffice())) {
            integratedField.add(new IntegratedField(OFFICE_CODE.getName(), F24_ELID + "treasury", f24Elid.getTreasury().getOffice()));
        }
        if (StringUtils.isNotBlank(treasuryAndOtherSection.getDocument())) {
            integratedField.add(new IntegratedField(DOCUMENT_CODE.getName(), F24_ELID + "treasury", f24Elid.getTreasury().getDocument()));
        }
        if (!treasuryAndOtherSection.getRecords().isEmpty()) {
            for (int i = 0; i < treasuryAndOtherSection.getRecords().size(); i++) {
                TreasuryRecord record = treasuryAndOtherSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(record.getType())) {
                    integratedField.add(new IntegratedField(TYPE.getName() + index, F24_ELID + "treasury["+i+"].type", record.getType()));
                }
                if (StringUtils.isNotBlank(record.getId())) {
                    integratedField.add(new IntegratedField(ID_ELEMENT.getName() + index, F24_ELID + "treasury["+i+"].id", record.getId()));
                }
                if (StringUtils.isNotBlank(record.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, F24_ELID + "treasury["+i+"].taxType", record.getTaxType()));
                }
                if (StringUtils.isNotBlank(record.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, F24_ELID + "treasury["+i+"].year", record.getYear()));
                }
                if (record.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, F24_ELID + "treasury["+i+"].debit", record.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
    }
}
