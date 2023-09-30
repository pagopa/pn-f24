package it.pagopa.pn.f24.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.pagopa.pn.f24.business.impl.F24ElidConverter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxRecord;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class F24ConverterTest {
    /**
     * Method under test: {@link F24Converter#convertTreasurySection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection)}
     */
    @Test
    void testConvertTreasurySection() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.TreasurySection actualConvertTreasurySectionResult = f24ElidConverter
                .convertTreasurySection(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection());
        assertNull(actualConvertTreasurySectionResult.getDocumentCode());
        assertNull(actualConvertTreasurySectionResult.getOfficeCode());
    }

    /**
     * Method under test: {@link F24Converter#convertTreasurySection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection)}
     */
    @Test
    void testConvertTreasurySection2() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        ArrayList<it.pagopa.pn.f24.generated.openapi.server.v1.dto.Tax> taxList = new ArrayList<>();
        org.f24.dto.component.TreasurySection actualConvertTreasurySectionResult = f24ElidConverter
                .convertTreasurySection(
                        new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection(taxList, "Office", "Document"));
        assertEquals("Document", actualConvertTreasurySectionResult.getDocumentCode());
        assertEquals(taxList, actualConvertTreasurySectionResult.getTaxList());
        assertEquals("Office", actualConvertTreasurySectionResult.getOfficeCode());
    }

    /**
     * Method under test: {@link F24Converter#convertTreasurySection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection)}
     */
    @Test
    void testConvertTreasurySection3() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();

        it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection treasurySection = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection();
        treasurySection.addRecordsItem(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.Tax());
        org.f24.dto.component.TreasurySection actualConvertTreasurySectionResult = f24ElidConverter
                .convertTreasurySection(treasurySection);
        assertNull(actualConvertTreasurySectionResult.getDocumentCode());
        assertEquals(1, actualConvertTreasurySectionResult.getTaxList().size());
        assertNull(actualConvertTreasurySectionResult.getOfficeCode());
    }

    /**
     * Method under test: {@link F24Converter#convertInpsSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection)}
     */
    @Test
    void testConvertInpsSection() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        ArrayList<it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsRecord> inpsRecordList = new ArrayList<>();
        assertEquals(inpsRecordList,
                f24ElidConverter
                        .convertInpsSection(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection(inpsRecordList))
                        .getInpsRecordList());
    }

    /**
     * Method under test: {@link F24Converter#convertInpsSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection)}
     */
    @Test
    void testConvertInpsSection2() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();

        it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection inpsSection = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection();
        inpsSection.addRecordsItem(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsRecord());
        assertEquals(1, f24ElidConverter.convertInpsSection(inpsSection).getInpsRecordList().size());
    }

    /**
     * Method under test: {@link F24Converter#convertPeriod(it.pagopa.pn.f24.generated.openapi.server.v1.dto.Period)}
     */
    @Test
    void testConvertPeriod() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.Period actualConvertPeriodResult = f24ElidConverter
                .convertPeriod(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.Period("2020-03-01", "2020-03-01"));
        assertEquals("2020-03-01", actualConvertPeriodResult.getEndDate());
        assertEquals("2020-03-01", actualConvertPeriodResult.getStartDate());
    }

    /**
     * Method under test: {@link F24Converter#convertRegionSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection)}
     */
    @Test
    void testConvertRegionSection() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        ArrayList<it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionRecord> regionRecordList = new ArrayList<>();
        assertEquals(regionRecordList,
                f24ElidConverter
                        .convertRegionSection(
                                new it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection(regionRecordList))
                        .getRegionRecordList());
    }

    /**
     * Method under test: {@link F24Converter#convertRegionSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection)}
     */
    @Test
    void testConvertRegionSection2() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();

        it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection regionSection = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection();
        regionSection.addRecordsItem(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionRecord());
        assertEquals(1, f24ElidConverter.convertRegionSection(regionSection).getRegionRecordList().size());
    }

    /**
     * Method under test: {@link F24Converter#convertLocalTaxSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection)}
     */
    @Test
    void testConvertLocalTaxSection() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.LocalTaxSection actualConvertLocalTaxSectionResult = f24ElidConverter
                .convertLocalTaxSection(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection());
        assertNull(actualConvertLocalTaxSectionResult.getDeduction());
        assertNull(actualConvertLocalTaxSectionResult.getOperationId());
    }

    /**
     * Method under test: {@link F24Converter#convertLocalTaxSection(it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection)}
     */
    @Test
    void testConvertLocalTaxSection2() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.LocalTaxSection actualConvertLocalTaxSectionResult = f24ElidConverter
                .convertLocalTaxSection(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection("42",
                        List.of(new LocalTaxRecord()), "Deduction"));
        assertEquals("Deduction", actualConvertLocalTaxSectionResult.getDeduction());
        assertEquals("42", actualConvertLocalTaxSectionResult.getOperationId());

    }

    /**
     * Method under test: {@link F24Converter#convertCompanyData(it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData)}
     */
    @Test
    void testConvertCompanyData() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        assertNull(f24ElidConverter.convertCompanyData(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData())
                .getName());
    }

    /**
     * Method under test: {@link F24Converter#convertCompanyData(it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData)}
     */
    @Test
    void testConvertCompanyData2() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.CompanyData actualConvertCompanyDataResult = f24ElidConverter
                .convertCompanyData(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.CompanyData("Name",
                        new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxAddress("Municipality", "Province",
                                "42 Main St")));
        assertEquals("Name", actualConvertCompanyDataResult.getName());
        org.f24.dto.component.TaxAddress taxAddress = actualConvertCompanyDataResult.getTaxAddress();
        assertEquals("42 Main St", taxAddress.getAddress());
        assertEquals("Province", taxAddress.getProvince());
        assertEquals("Municipality", taxAddress.getMunicipality());
    }

    /**
     * Method under test: {@link F24Converter#convertPersonData(it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonData)}
     */
    @Test
    void testConvertPersonData() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonalData personalData = new it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonalData(
                "Doe", "Name", "2020-03-01", "Sex", "Birth Place", "Birth Province");

        org.f24.dto.component.PersonData actualConvertPersonDataResult = f24ElidConverter
                .convertPersonData(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonData(personalData,
                        new it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxAddress("Municipality", "Province",
                                "42 Main St")));
        org.f24.dto.component.PersonalData personalData1 = actualConvertPersonDataResult.getPersonalData();
        assertEquals("Doe", personalData1.getSurname());
        org.f24.dto.component.TaxAddress taxAddress = actualConvertPersonDataResult.getTaxAddress();
        assertEquals("Province", taxAddress.getProvince());
        assertEquals("Municipality", taxAddress.getMunicipality());
        assertEquals("42 Main St", taxAddress.getAddress());
        assertEquals("Sex", personalData1.getSex());
        assertEquals("Name", personalData1.getName());
        assertEquals("Birth Province", personalData1.getBirthProvince());
        assertEquals("Birth Place", personalData1.getBirthPlace());
        assertEquals("2020-03-01", personalData1.getBirthDate());
    }

    /**
     * Method under test: {@link F24Converter#convertPersonalData(it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonalData)}
     */
    @Test
    void testConvertPersonalData() {
        F24ElidConverter f24ElidConverter = new F24ElidConverter();
        org.f24.dto.component.PersonalData actualConvertPersonalDataResult = f24ElidConverter
                .convertPersonalData(new it.pagopa.pn.f24.generated.openapi.server.v1.dto.PersonalData("Doe", "Name",
                        "2020-03-01", "Sex", "Birth Place", "Birth Province"));
        assertEquals("2020-03-01", actualConvertPersonalDataResult.getBirthDate());
        assertEquals("Doe", actualConvertPersonalDataResult.getSurname());
        assertEquals("Sex", actualConvertPersonalDataResult.getSex());
        assertEquals("Name", actualConvertPersonalDataResult.getName());
        assertEquals("Birth Province", actualConvertPersonalDataResult.getBirthProvince());
        assertEquals("Birth Place", actualConvertPersonalDataResult.getBirthPlace());
    }

}

