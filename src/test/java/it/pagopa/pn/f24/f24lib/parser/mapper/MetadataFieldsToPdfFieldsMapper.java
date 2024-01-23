package it.pagopa.pn.f24.f24lib.parser.mapper;

import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.PdfFieldFormatter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    protected List<IntegratedField> mapPeriod(Period period, String prefix,int index,String sectionId)  {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (period != null) {
            if (StringUtils.isNotBlank(period.getStartDate())) {
                integratedField.addAll(setMultiDate(START_DATE.getName(), sectionId, index, period.getStartDate(),prefix + "startDate"));
            }
            if (StringUtils.isNotBlank(period.getEndDate())) {
                integratedField.addAll(setMultiDate(END_DATE.getName(), sectionId, index, period.getEndDate(),prefix + "endDate"));

            }
        }
        return integratedField;
    }
    protected List<IntegratedField> setMultiDate(String fieldName, String sectionId, int index, String date,String metadataFieldName) {
        String monthPart = date.substring(0, 2);
        String yearPart = date.substring(2);


        List<IntegratedField> integratedField = new ArrayList<>();

        integratedField.add(new IntegratedField(fieldName + "Month" + sectionId+ index, metadataFieldName,monthPart));
        integratedField.add(new IntegratedField(fieldName + "Year" + sectionId + index, metadataFieldName,yearPart));

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

    protected List<IntegratedField> mapTax(TreasurySection treasurySection, String prefix,String sectionId) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (treasurySection.getRecords() != null || !treasurySection.getRecords().isEmpty()) {
            for (int i = 0; i < treasurySection.getRecords().size(); i++) {
                Tax tax = treasurySection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(tax.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "taxType", tax.getTaxType()));
                }
                if (StringUtils.isNotBlank(tax.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName()+sectionId +index, prefix + "[" + i + "]." + "installment", tax.getInstallment()));
                }
                if (StringUtils.isNotBlank(tax.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName()+sectionId + index, prefix + "[" + i + "]." + "year", tax.getYear()));
                }
                if (tax.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "debit", tax.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (tax.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "credit", tax.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapRegionSection(RegionSection regionSection, String prefix, String sectionId) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (regionSection.getRecords() != null || !regionSection.getRecords().isEmpty()) {
            for (int i = 0; i < regionSection.getRecords().size(); i++) {
                RegionRecord regionRecord = regionSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(regionRecord.getRegion())) {
                    integratedField.add(new IntegratedField(REGION_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "region", regionRecord.getRegion()));
                }
                if (StringUtils.isNotBlank(regionRecord.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "taxType", regionRecord.getTaxType()));
                }
                if (StringUtils.isNotBlank(regionRecord.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName()+sectionId + index, prefix + "[" + i + "]." + "installment", regionRecord.getInstallment()));
                }
                if (StringUtils.isNotBlank(regionRecord.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName()+sectionId + index, prefix + "[" + i + "]." + "year", regionRecord.getYear()));
                }
                if (regionRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "debit", regionRecord.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (regionRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "credit", regionRecord.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> mapInps(InpsSection inpsSection, String prefix,String sectionId) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (inpsSection.getRecords() != null || !inpsSection.getRecords().isEmpty()) {
            for (int i = 0; i < inpsSection.getRecords().size(); i++) {
                InpsRecord inpsRecord = inpsSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(inpsRecord.getInps())) {
                    integratedField.add(new IntegratedField(INPS_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "inps", inpsRecord.getInps()));
                }

                integratedField.addAll(mapPeriod(inpsRecord.getPeriod(), prefix + "period[" + i + "].", index ,sectionId));

                if (StringUtils.isNotBlank(inpsRecord.getOffice())) {
                    integratedField.add(new IntegratedField(OFFICE_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "office", inpsRecord.getOffice()));
                }
                if (StringUtils.isNotBlank(inpsRecord.getReason())) {
                    integratedField.add(new IntegratedField(CONTRIBUTION_REASON.getName()+sectionId + index, prefix + "[" + i + "]." + "reason", inpsRecord.getReason()));
                }
                if (inpsRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "debit", inpsRecord.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (inpsRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "credit", inpsRecord.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
        return integratedField;
    }

    protected List<IntegratedField> setMultiField(String fieldName, Double sourceRecord,String metadataFieldName){
        List<IntegratedField> integratedField = new ArrayList<>();
        String[] splittedAmount = splitField(sourceRecord);
        integratedField.add(new IntegratedField(fieldName + "Int", metadataFieldName, splittedAmount[0]));
        integratedField.add(new IntegratedField(fieldName + "Dec", metadataFieldName, splittedAmount[1]));
        return integratedField;
    }
    protected String[] splitField(double input) {
        input = Math.round(input * 100.0) / 100.0;
        int integerPart = (int) input;
        double decimalPart = input - integerPart;
        return new String[]{Integer.toString(integerPart),
                String.format(Locale.ROOT, "%.2f", decimalPart).split("\\.")[1]};
    }

    protected List<IntegratedField> mapLocalTaxSection(LocalTaxSection localTaxSection, String prefix,String sectionId) {
        List<IntegratedField> integratedField = new ArrayList<>();
        if (localTaxSection.getRecords() != null || !localTaxSection.getRecords().isEmpty()) {
            for (int i = 0; i < localTaxSection.getRecords().size(); i++) {
                LocalTaxRecord localTaxRecord = localTaxSection.getRecords().get(i);
                int index = i + 1;
                if (StringUtils.isNotBlank(localTaxRecord.getMunicipality())) {
                    integratedField.add(new IntegratedField(MUNICIPALITY_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "municipality", localTaxRecord.getMunicipality()));
                }
                if (Boolean.TRUE.equals(localTaxRecord.getReconsideration())) {
                    integratedField.add(new IntegratedField(RECONSIDERATION.getName() + index, prefix + "[" + i + "]." + "reconsideration", convertBooleanToString(localTaxRecord.getReconsideration())));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getNumberOfProperties())) {
                    integratedField.add(new IntegratedField(NUMBER_OF_PROPERTIES.getName() + index, prefix + "[" + i + "]." + "numberOfProperties", localTaxRecord.getNumberOfProperties()));
                }
                if (Boolean.TRUE.equals(localTaxRecord.getPropertiesChanges())) {
                    integratedField.add(new IntegratedField(PROPERTIES_CHANGED.getName() + index, prefix + "[" + i + "]." + "propertiesChanges", convertBooleanToString(localTaxRecord.getPropertiesChanges())));
                }
                if (Boolean.TRUE.equals(localTaxRecord.getAdvancePayment())) {
                    integratedField.add(new IntegratedField(ADVANCE_PAYMENT.getName() + index, prefix + "[" + i + "]." + "advancePayment", convertBooleanToString(localTaxRecord.getAdvancePayment())));
                }
                if (Boolean.TRUE.equals(localTaxRecord.getFullPayment())) {
                    integratedField.add(new IntegratedField(FULL_PAYMENT.getName() + index, prefix + "[" + i + "]." + "fullPayment", convertBooleanToString(localTaxRecord.getFullPayment())));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getTaxType())) {
                    integratedField.add(new IntegratedField(TAX_TYPE_CODE.getName()+sectionId + index, prefix + "[" + i + "]." + "taxType", localTaxRecord.getTaxType()));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getInstallment())) {
                    integratedField.add(new IntegratedField(INSTALLMENT.getName()+sectionId + index, prefix + "[" + i + "]." + "installment", localTaxRecord.getInstallment()));
                }
                if (StringUtils.isNotBlank(localTaxRecord.getYear())) {
                    integratedField.add(new IntegratedField(YEAR.getName()+sectionId + index, prefix + "[" + i + "]." + "year", localTaxRecord.getYear()));
                }
                if (localTaxRecord.getDebit() != null) {
                    integratedField.add(new IntegratedField(DEBIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "debit", localTaxRecord.getDebit().toString(), PdfFieldFormatter.IMPORT));
                }
                if (localTaxRecord.getCredit() != null) {
                    integratedField.add(new IntegratedField(CREDIT_AMOUNT.getName()+sectionId + index, prefix + "[" + i + "]." + "credit", localTaxRecord.getCredit().toString(), PdfFieldFormatter.IMPORT));
                }
            }
        }
        return integratedField;
    }
}

