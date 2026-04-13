package it.pagopa.pn.f24.service.impl;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import it.pagopa.pn.f24.business.F24Converter;
import it.pagopa.pn.f24.business.F24ConverterFactory;
import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataValidator;
import it.pagopa.pn.f24.util.Sha256Handler;
import lombok.extern.slf4j.Slf4j;
import org.f24.dto.form.F24Form;
import org.f24.exception.ResourceException;
import org.f24.service.validator.Validator;
import org.f24.service.validator.ValidatorFactory;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;

@Slf4j
@Service
public class MetadataValidatorImpl implements MetadataValidator {

    private final JsonService jsonService;
    private final F24Config f24Config;

    public MetadataValidatorImpl(JsonService jsonService, F24Config f24Config) {
        this.jsonService = jsonService;
        this.f24Config = f24Config;
    }

    public List<F24MetadataValidationIssue> validateMetadata(MetadataToValidate metadataToValidate) {
        log.info("Starting validation on metadata with pathTokens {}", metadataToValidate.getPathTokensKey());
        List<F24MetadataValidationIssue> f24MetadataValidationIssues = new ArrayList<>();

        F24Metadata f24Metadata = parseJson(metadataToValidate, f24MetadataValidationIssues);
        if (f24Metadata == null) {
            log.debug("Couldn't parse successfully metadata with fileKey: {}, skipping other validations", metadataToValidate.getRef().getFileKey());
            return f24MetadataValidationIssues;
        }

        metadataToValidate.setF24Metadata(f24Metadata);

        checkMetadataSchema(metadataToValidate, f24MetadataValidationIssues);
        if (!f24MetadataValidationIssues.isEmpty()) {
            log.debug("Metadata has syntax errors, skipping other validations.");
            return f24MetadataValidationIssues;
        }

        checkSha256(metadataToValidate, f24MetadataValidationIssues);
        checkMetadataType(metadataToValidate, f24MetadataValidationIssues);
        checkApplyCost(metadataToValidate, f24MetadataValidationIssues);
        checkMetadata(metadataToValidate, f24Config, f24MetadataValidationIssues);
        return f24MetadataValidationIssues;
    }

    private F24Metadata parseJson(MetadataToValidate metadataToValidate, List<F24MetadataValidationIssue> metadataValidationIssues) {
        F24Metadata f24Metadata = null;
        if (metadataToValidate.getMetadataFile().length == 0) {
            metadataValidationIssues.add(createIssue(metadataToValidate, "Metadata not found on safeStorage", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_ERROR));
        } else {
            try {
                f24Metadata = jsonService.parseMetadataFile(metadataToValidate.getMetadataFile());
            } catch (Exception e) {
                metadataValidationIssues.add(createIssue(metadataToValidate, e.getMessage(), PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_PARSING));
            }
        }
        return f24Metadata;
    }

    private void checkMetadataSchema(MetadataToValidate metadataToValidate, List<F24MetadataValidationIssue> metadataValidationIssues) {
        log.info("Start checkF24Schema");
        Set<ConstraintViolation<F24Metadata>> errors = jsonService.validate(metadataToValidate.getF24Metadata());
        log.debug("Found {} errors during schema validation", errors.size());
        errors.forEach(error -> {
            String message = error.getPropertyPath() + " " + error.getMessage();
            metadataValidationIssues.add(createIssue(metadataToValidate, message, PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_SCHEMA));
        });

        log.info("End checkF24Schema");
    }

    private void checkSha256(MetadataToValidate metadataToValidate, List<F24MetadataValidationIssue> metadataValidationIssues) {
        log.info("Start checkSha256");
        String checksum = Sha256Handler.computeSha256(metadataToValidate.getMetadataFile());

        String sha256 = metadataToValidate.getRef().getSha256();

        if (!sha256.equalsIgnoreCase(checksum)) {
            log.debug("Metadata obtained from safestorage has different sha256 from persisted Metadata record");
            metadataValidationIssues.add(createIssue(metadataToValidate, "Invalid sha256", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_DIFFERENT_SHA256));
        }

        log.info("End checkSha256");
    }

    private void checkMetadataType(MetadataToValidate metadataToValidate, List<F24MetadataValidationIssue> metadataValidationIssues) {
        log.info("Start checkMetadataType");
        int typeCount = 0;

        if (metadataToValidate.getF24Metadata().getF24Elid() != null) {
            typeCount++;
        }
        if (metadataToValidate.getF24Metadata().getF24Excise() != null) {
            typeCount++;
        }
        if (metadataToValidate.getF24Metadata().getF24Simplified() != null) {
            typeCount++;
        }
        if (metadataToValidate.getF24Metadata().getF24Standard() != null) {
            typeCount++;
        }

        if (typeCount > 1) {
            log.debug("Multiple metadata type sent");
            metadataValidationIssues.add(createIssue(metadataToValidate, "Multiple metadata type sent", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_MULTI_TYPE));
        }

        log.info("End checkMetadataType");
    }

    private void checkApplyCost(MetadataToValidate metadataToValidate, List<F24MetadataValidationIssue> metadataValidationIssues) {
        log.info("Start checkApplyCost");

        F24MetadataRef f24MetadataRef = metadataToValidate.getRef();

        MetadataInspector inspector = MetadataInspectorFactory.getInspector(getF24TypeFromMetadata(metadataToValidate.getF24Metadata()));
        ApplyCostValidation validation = inspector.checkApplyCost(metadataToValidate.getF24Metadata(), f24MetadataRef.isApplyCost());

        switch (validation) {
            case INVALID_APPLY_COST_GIVEN -> {
                log.debug("Metadata can't have applyCost = true and credit != 0 in a record");
                metadataValidationIssues.add(createIssue(metadataToValidate, "Metadata can't have applyCost = true and credit != 0 in a record", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
            }
            case TOO_MANY_APPLY_COST_GIVEN -> {
                log.debug("Metadata has too many applyCost in records");
                metadataValidationIssues.add(createIssue(metadataToValidate, "Metadata has too many applyCost=true in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
            }
            case NOT_REQUIRED_APPLY_COST_GIVEN -> {
                log.debug("Metadata shouldn't have applyCost in records");
                metadataValidationIssues.add(createIssue(metadataToValidate, "Metadata shouldn't have applyCost=true in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
            }
            case REQUIRED_APPLY_COST_NOT_GIVEN -> {
                log.debug("Metadata requires applyCost=true in records");
                metadataValidationIssues.add(createIssue(metadataToValidate, "Metadata requires applyCost=true in records", PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST));
            }
            case OK -> log.debug("checkApplyCost successfully executed");
        }

        log.info("End checkApplyCost");
    }

    private void checkMetadata(MetadataToValidate metadataToValidate,F24Config f24Config, List<F24MetadataValidationIssue> metadataValidationIssues) {
        log.info("Start checkMetadata");
        F24Converter f24Converter = F24ConverterFactory.getConverter(getF24TypeFromMetadata(metadataToValidate.getF24Metadata()));
        F24Form f24Form = f24Converter.convert(metadataToValidate.getF24Metadata());
        try {
            Validator validator = ValidatorFactory.createValidator(f24Form);
            if (f24Config.getIsEnabledTaxCodeValidation()) {
                validator.validate();
            }
            validator.validateWithoutTaxCode();
        } catch (ResourceException | ProcessingException | IOException | IllegalArgumentException e) {
            metadataValidationIssues.add(createIssue(metadataToValidate, e.getMessage(), PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_VALIDATION_ERROR));
        }
        log.info("End checkMetadata");
    }

    private F24MetadataValidationIssue createIssue(MetadataToValidate metadataToValidate,
                                                   String detail,
                                                   String code) {
        return F24MetadataValidationIssue.builder()
                .element("FileKey:" + metadataToValidate.getRef().getFileKey())
                .detail(detail)
                .code(code)
                .build();
    }
}
