package it.pagopa.pn.f24.service.impl;

import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.service.JsonService;

import java.util.HashSet;
import java.util.List;

import it.pagopa.pn.f24.util.Sha256Handler;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {MetadataValidatorImpl.class})
@ExtendWith(SpringExtension.class)
public class MetadataValidatorImplTest {
    @MockBean
    private JsonService jsonService;

    @Autowired
    private MetadataValidatorImpl metadataValidatorImpl;

    @Test
    public void testValidateMetadata() {

        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");

        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();

        TaxPayerStandard taxPayerStandard = new TaxPayerStandard();
        f24Standard.setTaxPayer(taxPayerStandard);

        TreasurySection treasurySection = new TreasurySection();
        f24Standard.setTreasury(treasurySection);

        RegionSection regionSection = new RegionSection();
        f24Standard.setRegion(regionSection);

        InpsSection inpsSection = new InpsSection();
        f24Standard.setInps(inpsSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        f24Standard.setLocalTax(localTaxSection);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        f24Standard.setSocialSecurity(socialSecuritySection);


        f24Metadata.setF24Standard(f24Standard);
        f24Metadata.setF24Excise(new F24Excise());
        f24Metadata.setF24Simplified(new F24Simplified());
        f24Metadata.setF24Elid(new F24Elid());

        MetadataToValidate metadataToValidate = new MetadataToValidate();
        metadataToValidate.setMetadataFile("byte".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("ABC123");
        metadataToValidate.setF24Metadata(f24Metadata);

        when(jsonService.stringifyObject(Mockito.<F24Metadata>any()))
                .thenReturn("Stringify Object");
        when(jsonService.validate(Mockito.<F24Metadata>any()))
                .thenReturn(new HashSet<>());
        when(jsonService.parseMetadataFile(Mockito.<byte[]>any()))
                .thenReturn(f24Metadata);

        String checksum = Sha256Handler.computeSha256("Stringify Object");

        f24MetadataRef.setSha256(checksum);

        List <F24MetadataValidationIssue> result = metadataValidatorImpl.validateMetadata(metadataToValidate);

        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    public void testValidateMetadataCheckApplyCostFalse() {

        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");

        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();

        TaxPayerStandard taxPayerStandard = new TaxPayerStandard();
        f24Standard.setTaxPayer(taxPayerStandard);

        TreasurySection treasurySection = new TreasurySection();
        f24Standard.setTreasury(treasurySection);

        RegionSection regionSection = new RegionSection();
        f24Standard.setRegion(regionSection);

        InpsSection inpsSection = new InpsSection();
        f24Standard.setInps(inpsSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        f24Standard.setLocalTax(localTaxSection);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        f24Standard.setSocialSecurity(socialSecuritySection);


        f24Metadata.setF24Standard(f24Standard);
        f24Metadata.setF24Excise(new F24Excise());
        f24Metadata.setF24Simplified(new F24Simplified());
        f24Metadata.setF24Elid(new F24Elid());

        MetadataToValidate metadataToValidate = new MetadataToValidate();
        metadataToValidate.setMetadataFile("byte".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("ABC123");
        metadataToValidate.setF24Metadata(f24Metadata);

        when(jsonService.stringifyObject(Mockito.<F24Metadata>any()))
                .thenReturn("Stringify Object");
        when(jsonService.validate(Mockito.<F24Metadata>any()))
                .thenReturn(new HashSet<>());
        when(jsonService.parseMetadataFile(Mockito.<byte[]>any()))
                .thenReturn(f24Metadata);

        String checksum = Sha256Handler.computeSha256("Stringify Object");

        f24MetadataRef.setSha256(checksum);

        List <F24MetadataValidationIssue> result = metadataValidatorImpl.validateMetadata(metadataToValidate);

        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    public void testValidateMetadataSha256Error() {

        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");

        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();

        TaxPayerStandard taxPayerStandard = new TaxPayerStandard();
        f24Standard.setTaxPayer(taxPayerStandard);

        TreasurySection treasurySection = new TreasurySection();
        f24Standard.setTreasury(treasurySection);

        RegionSection regionSection = new RegionSection();
        f24Standard.setRegion(regionSection);

        InpsSection inpsSection = new InpsSection();
        f24Standard.setInps(inpsSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        f24Standard.setLocalTax(localTaxSection);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        f24Standard.setSocialSecurity(socialSecuritySection);


        f24Metadata.setF24Standard(f24Standard);
        f24Metadata.setF24Excise(new F24Excise());
        f24Metadata.setF24Simplified(new F24Simplified());
        f24Metadata.setF24Elid(new F24Elid());

        MetadataToValidate metadataToValidate = new MetadataToValidate();
        metadataToValidate.setMetadataFile("byte".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("ABC123");
        metadataToValidate.setF24Metadata(f24Metadata);

        when(jsonService.stringifyObject(Mockito.<F24Metadata>any()))
                .thenReturn("Stringify Object");
        when(jsonService.validate(Mockito.<F24Metadata>any()))
                .thenReturn(new HashSet<>());
        when(jsonService.parseMetadataFile(Mockito.<byte[]>any()))
                .thenReturn(f24Metadata);



        f24MetadataRef.setSha256(Sha256Handler.computeSha256("Stringify"));

        List <F24MetadataValidationIssue> result = metadataValidatorImpl.validateMetadata(metadataToValidate);

        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    public void testValidateMetadataCheckApplyCostNoApplyCost() {

        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(false);
        f24MetadataRef.setFileKey("File Key");

        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();

        TaxPayerStandard taxPayerStandard = new TaxPayerStandard();
        f24Standard.setTaxPayer(taxPayerStandard);

        TreasurySection treasurySection = new TreasurySection();
        Tax tax = new Tax();
        tax.setApplyCost(true);
        treasurySection.setRecords(List.of(tax));
        f24Standard.setTreasury(treasurySection);

        RegionSection regionSection = new RegionSection();
        f24Standard.setRegion(regionSection);

        InpsSection inpsSection = new InpsSection();
        f24Standard.setInps(inpsSection);

        LocalTaxSection localTaxSection = new LocalTaxSection();
        f24Standard.setLocalTax(localTaxSection);

        SocialSecuritySection socialSecuritySection = new SocialSecuritySection();
        f24Standard.setSocialSecurity(socialSecuritySection);


        f24Metadata.setF24Standard(f24Standard);
        f24Metadata.setF24Excise(new F24Excise());
        f24Metadata.setF24Simplified(new F24Simplified());
        f24Metadata.setF24Elid(new F24Elid());

        MetadataToValidate metadataToValidate = new MetadataToValidate();
        metadataToValidate.setMetadataFile("byte".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("ABC123");
        metadataToValidate.setF24Metadata(f24Metadata);

        when(jsonService.stringifyObject(Mockito.<F24Metadata>any()))
                .thenReturn("Stringify Object");
        when(jsonService.validate(Mockito.<F24Metadata>any()))
                .thenReturn(new HashSet<>());
        when(jsonService.parseMetadataFile(Mockito.<byte[]>any()))
                .thenReturn(f24Metadata);

        String checksum = Sha256Handler.computeSha256("Stringify Object");

        f24MetadataRef.setSha256(checksum);

        List <F24MetadataValidationIssue> result = metadataValidatorImpl.validateMetadata(metadataToValidate);

        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    public void testValidateMetadataParseJsonError() {

        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");

        MetadataToValidate metadataToValidate = new MetadataToValidate();

        metadataToValidate.setMetadataFile("byte".getBytes());
        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("ABC123");

        when(jsonService.parseMetadataFile(Mockito.<byte[]>any()))
                .thenThrow(RuntimeException.class);

        List <F24MetadataValidationIssue> result = metadataValidatorImpl.validateMetadata(metadataToValidate);

        Assertions.assertFalse(result.isEmpty());

    }


}

