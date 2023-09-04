package it.pagopa.pn.f24.service.factory;

import it.pagopa.pn.f24.dto.metadata.*;
import org.f24.dto.component.TaxPayer;

import java.util.stream.Collectors;

public class F24StandardConverter extends F24Converter {

    @Override
    public org.f24.dto.form.F24Standard convert(F24Item f24Item) {
        org.f24.dto.form.F24Standard formF24Standard = new org.f24.dto.form.F24Standard();
        F24Standard f24ItemStandard = f24Item.getF24Standard();

        if(f24ItemStandard == null) {
            return formF24Standard;
        }

        //TODO formF24Standard.setIbanCode(f24ItemStandard.get);
        //TODO formF24Standard.setHeader();
        if(f24ItemStandard.getTaxPayer() != null) {
            formF24Standard.setTaxPayer(this.convertTaxPayer(f24ItemStandard.getTaxPayer()));
        }
        if(f24ItemStandard.getTreasury() != null) {
            formF24Standard.setTreasurySection(this.convertTreasurySection(f24ItemStandard.getTreasury()));
        }

        if(f24ItemStandard.getInps() != null) {
            formF24Standard.setInpsSection(this.convertInpsSection(f24ItemStandard.getInps()));
        }

        if(f24ItemStandard.getLocalTax() != null) {
            formF24Standard.setLocalTaxSection(this.convertLocalTaxSection(f24ItemStandard.getLocalTax()));
        }

        if(f24ItemStandard.getRegion() != null) {
            formF24Standard.setRegionSection(this.convertRegionSection(f24ItemStandard.getRegion()));
        }

        if(f24ItemStandard.getSocialSecurity() != null) {
            formF24Standard.setSocialSecuritySection(convertSocialSecuritySection(f24ItemStandard.getSocialSecurity()));
        }
        return formF24Standard;
    }

    private TaxPayer convertTaxPayer(TaxPayerStandard dvTaxPayer) {
        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setTaxCode(dvTaxPayer.getTaxCode());
        taxPayer.setIdCode(dvTaxPayer.getId());
        taxPayer.setRelativePersonTaxCode(dvTaxPayer.getRelativePersonTaxCode());
        taxPayer.setIsNotTaxYear(Boolean.TRUE.equals(dvTaxPayer.getIsNotTaxYear()));
        //TODO ? taxPayer.setDocumentCode(dv);
        //TODO ? taxPayer.setOfficeCode(dv);
        if(dvTaxPayer.getCompany() != null) {
            taxPayer.setCompanyData(this.convertCompanyData(dvTaxPayer.getCompany()));
        }
        if(dvTaxPayer.getPerson() != null) {
            taxPayer.setPersonData(this.convertPersonData(dvTaxPayer.getPerson()));
        }
        return taxPayer;
    }

    public org.f24.dto.component.SocialSecuritySection convertSocialSecuritySection(SocialSecuritySection socialSecuritySection) {
        org.f24.dto.component.SocialSecuritySection outputSocialSecuritySection = new org.f24.dto.component.SocialSecuritySection();

        if(socialSecuritySection.getSocSecRecords() != null) {
            outputSocialSecuritySection.setSocialSecurityRecordList(
                    socialSecuritySection.getSocSecRecords()
                            .stream()
                            .map(this::convertSocialSecurityRecord)
                            .collect(Collectors.toList())
            );
        }

        if(socialSecuritySection.getRecords() != null) {
            outputSocialSecuritySection.setInailRecords(
                    socialSecuritySection.getRecords()
                            .stream()
                            .map(this::convertInailRecord)
                            .collect(Collectors.toList())
            );
        }

        //TODO Ignorare? outputSocialSecuritySection.setRecordList();

        return outputSocialSecuritySection;
    }

    private org.f24.dto.component.InailRecord convertInailRecord(InailRecord inailRecord) {
        org.f24.dto.component.InailRecord outputRecord = new org.f24.dto.component.InailRecord();
        outputRecord.setCreditAmount(inailRecord.getCredit());
        outputRecord.setDebitAmount(inailRecord.getDebit());
        outputRecord.setOfficeCode(inailRecord.getOffice());
        outputRecord.setReason(inailRecord.getReason());
        outputRecord.setControlCode(inailRecord.getControl());
        outputRecord.setReferenceNumber(inailRecord.getRefNumber());
        outputRecord.setCompanyCode(inailRecord.getCompany());
        //TODO outputRecord.setDeduction(inailRecord.get);
        return outputRecord;
    }

    public org.f24.dto.component.SocialSecurityRecord convertSocialSecurityRecord(SocialSecurityRecord socialSecurityRecord) {
        org.f24.dto.component.SocialSecurityRecord outputSocialSecurityRecord = new org.f24.dto.component.SocialSecurityRecord();
        //TODO outputSocialSecurityRecord.setMunicipalityCode(socialSecurityRecord.get);
        outputSocialSecurityRecord.setOfficeCode(socialSecurityRecord.getOffice());
        outputSocialSecurityRecord.setContributionReason(socialSecurityRecord.getReason());
        outputSocialSecurityRecord.setPositionCode(socialSecurityRecord.getPosition());
        outputSocialSecurityRecord.setPeriod(convertPeriod(socialSecurityRecord.getPeriod()));
        outputSocialSecurityRecord.setDebitAmount(socialSecurityRecord.getDebit());
        outputSocialSecurityRecord.setCreditAmount(socialSecurityRecord.getCredit());
        //TODO outputSocialSecurityRecord.setDeduction(socialSecurityRecord.get);
        return outputSocialSecurityRecord;
    }

}
