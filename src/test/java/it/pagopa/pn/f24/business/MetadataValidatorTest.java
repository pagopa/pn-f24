package it.pagopa.pn.f24.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class MetadataValidatorTest {
    /**
     * Method under test: {@link MetadataValidator#validateMetadata()}
     */
    @Test
    void testValidateMetadata() {
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");
        f24MetadataRef.setSha256("Sha256");

        MetadataToValidate metadataToValidate = mock(MetadataToValidate.class);
        when(metadataToValidate.getRef()).thenReturn(f24MetadataRef);
        when(metadataToValidate.getMetadataFile()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
        when(metadataToValidate.getPathTokensKey()).thenReturn("ABC123");

        List<F24MetadataValidationIssue> actualValidateMetadataResult = new MetadataValidator(metadataToValidate).validateMetadata();
        assertEquals(4, actualValidateMetadataResult.size());
        F24MetadataValidationIssue getResult = actualValidateMetadataResult.get(1);
        assertEquals("PathTokens:ABC123", getResult.getElement());
        F24MetadataValidationIssue getResult1 = actualValidateMetadataResult.get(2);
        assertEquals("PathTokens:ABC123", getResult1.getElement());
        assertEquals("Metadata hasn't applyCost in records", getResult1.getDetail());
        assertEquals("PN_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST", getResult1.getCode());
        assertEquals("Multiple metadata type sent", getResult.getDetail());
        assertEquals("PN_F24_METADATA_VALIDATION_MULTI_TYPE", getResult.getCode());
        F24MetadataValidationIssue getResult2 = actualValidateMetadataResult.get(3);
        assertEquals("Field inpsSection is required.", getResult2.getDetail());
        assertEquals("PN_F24_METADATA_VALIDATION_ERROR", getResult2.getCode());
        F24MetadataValidationIssue getResult3 = actualValidateMetadataResult.get(0);
        assertEquals("Invalid sha256", getResult3.getDetail());
        assertEquals("PN_F24_METADATA_VALIDATION_DIFFERENT_SHA256", getResult3.getCode());
        assertEquals("PathTokens:ABC123", getResult2.getElement());
        assertEquals("PathTokens:ABC123", getResult3.getElement());
        verify(metadataToValidate, atLeast(1)).getRef();
        verify(metadataToValidate, atLeast(1)).getMetadataFile();
        verify(metadataToValidate, atLeast(1)).getPathTokensKey();
    }
}

