package it.pagopa.pn.f24.business;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.extern.slf4j.Slf4j;
import org.f24.dto.form.F24Form;
import org.f24.exception.ResourceException;
import org.f24.service.validator.Validator;
import org.f24.service.validator.ValidatorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;
import static it.pagopa.pn.f24.util.Utility.objectToJsonString;

@Slf4j
public class MetadataValidator {
    private final MetadataToValidate metadataToValidate;
    private List<F24MetadataValidationIssue> f24MetadataValidationIssues;

    public MetadataValidator(MetadataToValidate metadataToValidate) {
        this.metadataToValidate = metadataToValidate;
    }

    public List<F24MetadataValidationIssue> validateMetadata() {
        log.info("Starting validation on metadata with pathTokens {}", this.metadataToValidate.getPathTokensKey());
        this.f24MetadataValidationIssues = new ArrayList<>();
        checkSha256();
        checkMetadataType();
        checkApplyCost();
        checkMetadata();
        return f24MetadataValidationIssues;
    }

    private void checkSha256() {
        log.info("Start CheckSha256");
        String f24MetadataToJson = objectToJsonString(this.metadataToValidate.getMetadata());
        String checksum = Sha256Handler.computeSha256(f24MetadataToJson);

        String sha256 = this.metadataToValidate.getRef().getSha256();

        if(!sha256.equalsIgnoreCase(checksum)) {
            log.warn("Metadata obtained from safestorage has different sha256 from persisted Metadata record");
            createAndAddIssue("Invalid sha256", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_DIFFERENT_SHA256);
        }
    }
    private void checkMetadataType() {
        log.info("Start CheckMetadataType");
        F24Metadata f24Metadata = this.metadataToValidate.getMetadata();
        int typeCount = 0;

        if(f24Metadata.getF24Elid() != null) {
            typeCount ++;
        }
        if(f24Metadata.getF24Excise() != null) {
            typeCount ++;
        }
        if(f24Metadata.getF24Simplified() != null) {
            typeCount ++;
        }

        if(f24Metadata.getF24Standard() != null) {
            typeCount ++;
        }

        if(typeCount > 1) {
            log.warn("Multiple metadata type sent");
            createAndAddIssue("Multiple metadata type sent", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_MULTI_TYPE);
        }
    }

    private void checkApplyCost() {
        log.info("Start CheckApplyCost");
        MetadataInspector inspector = MetadataInspectorFactory.getInspector(getF24TypeFromMetadata(this.metadataToValidate.getMetadata()));
        int applyCostCounter = inspector.countMetadataApplyCost(this.metadataToValidate.getMetadata());
        log.debug("Found {} applyCost flag in Metadata", applyCostCounter);

        F24MetadataRef f24MetadataRef = this.metadataToValidate.getRef();
        if(f24MetadataRef.isApplyCost() && applyCostCounter == 0) {
            log.debug("Metadata hasn't applyCost in records");
            createAndAddIssue("Metadata hasn't applyCost in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST);
        } else if(!f24MetadataRef.isApplyCost() && applyCostCounter > 0) {
            log.debug("Metadata shouldn't have applyCost in records");
            createAndAddIssue("Metadata shouldn't have applyCost in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST);
        } else if(applyCostCounter > 1) {
            log.debug("Metadata has too many applyCost in records");
            createAndAddIssue("Metadata has too many applyCost in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST);
        }
    }

    private void checkMetadata() {
        log.info("Start CheckMetadata");
        F24Converter f24Converter = F24ConverterFactory.getConverter(getF24TypeFromMetadata(this.metadataToValidate.getMetadata()));
        F24Form f24Form = f24Converter.convert(this.metadataToValidate.getMetadata());
        try {
            Validator validator = ValidatorFactory.createValidator(f24Form);
            validator.validate();
        } catch (ResourceException | ProcessingException | IOException e) {
            handleLibValidationException(e);
        }
    }

    private void handleLibValidationException(Throwable t) {
        if(t instanceof ResourceException) {
            createAndAddIssue(t.getMessage(), PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_ERROR);
            return;
        }

        throw new RuntimeException(t);
    }

    private void createAndAddIssue(String detail, String code) {
        this.f24MetadataValidationIssues.add(
                F24MetadataValidationIssue.builder()
                        .element("PathTokens:" + this.metadataToValidate.getPathTokensKey())
                        .detail(detail)
                        .code(code)
                        .build()
        );
    }
}