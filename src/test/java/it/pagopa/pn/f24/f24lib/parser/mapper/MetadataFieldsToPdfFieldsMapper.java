package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.f24.service.pdf.util.FieldEnum.*;

public abstract class MetadataFieldsToPdfFieldsMapper {
    public abstract List<IntegratedField> connectMetadataFieldsToPdfFields(F24Metadata f24Metadata);

    protected String convertBooleanToString(Boolean value) {
        return value ? "X" : "";
    }

    protected List<IntegratedField> mapPersonalData(PersonalData personalData, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (personalData != null) {
            if (StringUtils.isNotBlank(personalData.getName())) {
                integratedField.add(new IntegratedField(NAME.getName(), prefix + "name", personalData.getName()));
            }
            if (StringUtils.isNotBlank(personalData.getSurname())) {
                integratedField.add(new IntegratedField(CORPORATE_NAME.getName(), prefix + "surname", personalData.getSurname()));
            }
            if (StringUtils.isNotBlank(personalData.getBirthDate())) {
                integratedField.add(new IntegratedField(BIRTH_DATE.getName(), prefix + "birthDate", personalData.getBirthDate(), PdfFieldFormatter.DATE));
            }
            if (StringUtils.isNotBlank(personalData.getBirthPlace())) {
                integratedField.add(new IntegratedField(BIRTH_PLACE.getName(), prefix + "birthPlace", personalData.getBirthPlace()));
            }
            if (StringUtils.isNotBlank(personalData.getBirthProvince())) {
                integratedField.add(new IntegratedField(BIRTH_PROVINCE.getName(), prefix + "birthProvince", personalData.getBirthProvince()));
            }
            if (StringUtils.isNotBlank(personalData.getSex())) {
                integratedField.add(new IntegratedField(SEX.getName(), prefix + "sex", personalData.getSex()));
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapPeriod(Period period, String prefix, int index) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (period != null) {
            if (StringUtils.isNotBlank(period.getStartDate())) {
                integratedField.add(new IntegratedField(START_DATE.getName() + index, prefix + "startDate", period.getStartDate()));
            }
            if (StringUtils.isNotBlank(period.getEndDate())) {
                integratedField.add(new IntegratedField(END_DATE.getName() + index, prefix + "endDate", period.getEndDate()));
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapTaxAddress(TaxAddress taxAddress, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (taxAddress != null) {
            if (StringUtils.isNotBlank(taxAddress.getAddress())) {
                integratedField.add(new IntegratedField(ADDRESS.getName(), prefix + "address", taxAddress.getAddress()));
            }
            if (StringUtils.isNotBlank(taxAddress.getMunicipality())) {
                integratedField.add(new IntegratedField(MUNICIPALITY.getName(), prefix + "municipality", taxAddress.getMunicipality()));
            }
            if (StringUtils.isNotBlank(taxAddress.getProvince())) {
                integratedField.add(new IntegratedField(TAX_PROVINCE.getName(), prefix + "taxProvince", taxAddress.getProvince()));
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapCompanyData(CompanyData companyData, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (companyData != null) {
            if (StringUtils.isNotBlank(companyData.getName())) {
                integratedField.add(new IntegratedField(CORPORATE_NAME.getName(), prefix + "companyName", companyData.getName()));
            }

            integratedField.addAll(mapTaxAddress(companyData.getTaxAddress(), prefix + "taxAddress."));
        }
        return integratedField;
    }

    protected List<IntegratedField> mapPersonData(PersonData personData, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (personData != null) {
            integratedField.addAll(mapPersonalData(personData.getPersonalData(), prefix + "personalData."));
            integratedField.addAll(mapTaxAddress(personData.getTaxAddress(), prefix + "taxAddress."));
        }
        return integratedField;
    }

    protected List<IntegratedField> mapTax(TreasurySection treasurySection, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (treasurySection.getRecords() != null || !treasurySection.getRecords().isEmpty()) {
            for (int i = 0; i < treasurySection.getRecords().size(); i++) {
                Tax tax = treasurySection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(tax.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, prefix + "[" + i + "]." + "taxType", tax.getTaxType()));
                }
                if (StringUtils.isNotBlank(tax.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName() +index, prefix + "[" + i + "]." + "installment", tax.getInstallment()));
                }
                if (StringUtils.isNotBlank(tax.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, prefix + "[" + i + "]." + "year", tax.getYear()));
                }
                if (tax.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "debit", tax.getDebit().toString()));
                }
                if (tax.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "credit", tax.getCredit().toString()));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapRegionSection(RegionSection regionSection, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (regionSection.getRecords() != null || !regionSection.getRecords().isEmpty()) {
            for (int i = 0; i < regionSection.getRecords().size(); i++) {
                RegionRecord regionRecord = regionSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(regionRecord.getRegion())) {
                    integratedField.add(new IntegratedField(REGION_CODE.getName() + index, prefix + "[" + i + "]." + "region", regionRecord.getRegion()));
                }
                if (StringUtils.isNotBlank(regionRecord.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, prefix + "[" + i + "]." + "taxType", regionRecord.getTaxType()));
                }
                if (StringUtils.isNotBlank(regionRecord.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName() + index, prefix + "[" + i + "]." + "installment", regionRecord.getInstallment()));
                }
                if (StringUtils.isNotBlank(regionRecord.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, prefix + "[" + i + "]." + "year", regionRecord.getYear()));
                }
                if (regionRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "debit", regionRecord.getDebit().toString()));
                }
                if (regionRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "credit", regionRecord.getCredit().toString()));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapInps(InpsSection inpsSection, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (inpsSection.getRecords() != null || !inpsSection.getRecords().isEmpty()) {
            for (int i = 0; i < inpsSection.getRecords().size(); i++) {
                InpsRecord inpsRecord = inpsSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(inpsRecord.getInps())) {
                    integratedField.add(new IntegratedField(INPS_CODE.getName() + index, prefix + "[" + i + "]." + "inps", inpsRecord.getInps()));
                }

                integratedField.addAll(mapPeriod(inpsRecord.getPeriod(), prefix + "period[" + i + "].", i));

                if (StringUtils.isNotBlank(inpsRecord.getOffice())) {
                    integratedField.add(new IntegratedField(OFFICE_CODE.getName() + index, prefix + "[" + i + "]." + "office", inpsRecord.getOffice()));
                }
                if (StringUtils.isNotBlank(inpsRecord.getReason())) {
                    integratedField.add(new IntegratedField(REASON.getName() + index, prefix + "[" + i + "]." + "reason", inpsRecord.getReason()));
                }
                if (inpsRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "debit", inpsRecord.getDebit().toString()));
                }
                if (inpsRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "credit", inpsRecord.getCredit().toString()));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapLocalTaxSection(LocalTaxSection localTaxSection, String prefix) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (localTaxSection.getRecords() != null || !localTaxSection.getRecords().isEmpty()) {
            for (int i = 0; i < localTaxSection.getRecords().size(); i++) {
                LocalTaxRecord localTaxRecord = localTaxSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(localTaxRecord.getMunicipality())) {
                    integratedField.add(new IntegratedField(MUNICIPALITY.getName() + index, prefix + "[" + i + "]." + "municipality", localTaxRecord.getMunicipality()));
                }
                if (!localTaxRecord.getReconsideration()) {
                    integratedField.add(new IntegratedField(RECONSIDERATION.getName() + index, prefix + "[" + i + "]." + "reconsideration", convertBooleanToString(localTaxRecord.getReconsideration())));
                }
                if (!localTaxRecord.getPropertiesChanges()) {
                    integratedField.add(new IntegratedField(PROPERTIES_CHANGED.getName() + index, prefix + "[" + i + "]." + "propertiesChanges", convertBooleanToString(localTaxRecord.getPropertiesChanges())));
                }
                if (!localTaxRecord.getAdvancePayment()) {
                    integratedField.add(new IntegratedField(ADVANCE_PAYMENT.getName() + index, prefix + "[" + i + "]." + "advancePayment", convertBooleanToString(localTaxRecord.getAdvancePayment())));
                }
                if (!localTaxRecord.getFullPayment()) {
                    integratedField.add(new IntegratedField(FULL_PAYMENT.getName() + index, prefix + "[" + i + "]." + "fullPayment", convertBooleanToString(localTaxRecord.getFullPayment())));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName() + index, prefix + "[" + i + "]." + "taxType", localTaxRecord.getTaxType()));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName() + index, prefix + "[" + i + "]." + "installment", localTaxRecord.getInstallment()));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName() + index, prefix + "[" + i + "]." + "year", localTaxRecord.getYear()));
                }
                if (localTaxRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "debit", localTaxRecord.getDebit().toString()));
                }
                if (localTaxRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName() + index, prefix + "[" + i + "]." + "credit", localTaxRecord.getCredit().toString()));
                }
            }
        }
        return integratedField;
    }
}

