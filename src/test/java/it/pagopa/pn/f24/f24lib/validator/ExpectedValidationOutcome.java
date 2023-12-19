package it.pagopa.pn.f24.f24lib.validator;

import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.*;


@Slf4j
public enum ExpectedValidationOutcome {
    VALID("VALID", (issueList) -> {
        Assertions.assertTrue(issueList.isEmpty());
        log.info("Assertion verified, Metadata is valid");
    }),
    INVALID("INVALID", (issueList) -> {
        Assertions.assertFalse(issueList.isEmpty());
        reportIssues(issueList);
    }),
    INVALID_APPLY_COST("INVALID-APPLY-COST", (issueList) -> {
        Optional<F24MetadataValidationIssue> optIssue = issueList.stream()
                .filter(issue -> issue.getCode().equals(ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST))
                .findFirst();

        Assertions.assertTrue(optIssue.isPresent());

        reportIssues(List.of(optIssue.get()));
    }),
    INVALID_PARSING("INVALID-JSON", (issueList) -> {
        Optional<F24MetadataValidationIssue> optIssue = issueList.stream()
                .filter(issue -> issue.getCode().equals(ERROR_CODE_F24_METADATA_PARSING))
                .findFirst();

        Assertions.assertTrue(optIssue.isPresent());

        reportIssues(List.of(optIssue.get()));
    }),
    INVALID_METADATA("INVALID-METADATA", (issueList) -> {
        Optional<F24MetadataValidationIssue> optIssue = issueList.stream()
                .filter(issue -> issue.getCode().equals(ERROR_CODE_F24_METADATA_VALIDATION_ERROR))
                .findFirst();

        Assertions.assertTrue(optIssue.isPresent());

        reportIssues(List.of(optIssue.get()));
    }),
    INVALID_MULTITYPE("INVALID-MULTITYPE", (issueList) -> {
        Optional<F24MetadataValidationIssue> optIssue = issueList.stream()
                .filter(issue -> issue.getCode().equals(ERROR_CODE_F24_METADATA_VALIDATION_MULTI_TYPE))
                .findFirst();

        Assertions.assertTrue(optIssue.isPresent());

        reportIssues(List.of(optIssue.get()));
    });

    @Getter
    private final String value;
    private final Consumer<List<F24MetadataValidationIssue>> assertionExecution;

    ExpectedValidationOutcome(String value, Consumer<List<F24MetadataValidationIssue>> assertionExecution) {
        this.value = value;
        this.assertionExecution = assertionExecution;
    }

    public void performAssertions(List<F24MetadataValidationIssue> f24MetadataValidationIssues) {
        log.info("Expected Outcome to verify : {}", name());
        this.assertionExecution.accept(f24MetadataValidationIssues);
    }

    private static void reportIssues(List<F24MetadataValidationIssue> f24MetadataValidationIssues) {
        f24MetadataValidationIssues.forEach(
                f24MetadataValidationIssue -> log.info("Assertion verified, Error found: {} on element: {}", f24MetadataValidationIssue.getDetail(), f24MetadataValidationIssue.getElement())
        );
    }

    static boolean isAnExpectedValidationOutcome(String text) {
        try {
            fromValue(text);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    static ExpectedValidationOutcome fromValue(String value) {
        return Arrays.stream(ExpectedValidationOutcome.values())
                .filter(evo -> evo.value.equals(value))
                .findFirst()
                .orElseThrow();
    }

    static List<String> getAcceptedValues() {
        return Arrays.stream(ExpectedValidationOutcome.values())
                .map(ExpectedValidationOutcome::getValue)
                .toList();
    }
}
