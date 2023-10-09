package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import java.util.List;

public interface MetadataValidator {
    List<F24MetadataValidationIssue> validateMetadata(MetadataToValidate metadataToValidate);
}
