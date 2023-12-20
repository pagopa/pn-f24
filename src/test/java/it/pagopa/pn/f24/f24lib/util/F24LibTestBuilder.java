package it.pagopa.pn.f24.f24lib.util;

import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.f24lib.validator.ExpectedValidationOutcome;
import it.pagopa.pn.f24.f24lib.validator.MetadataToValidateBuilder;
import it.pagopa.pn.f24.f24lib.validator.TestCaseParser;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class F24LibTestBuilder {
    @Autowired
    private MetadataValidator validator;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private F24Generator f24Generator;


    public void execTest(String testCase, boolean shouldHaveApplyCost) {
        try {
            long initTestMs = new Date().getTime();
            log.info("Starting test on testCase : {}", testCase);
            MetadataToValidate metadataToValidate = MetadataToValidateBuilder.metaBuilder(testCase, shouldHaveApplyCost);
            performValidationTest(testCase, metadataToValidate);

            long endTestMs = new Date().getTime();
            log.info("Ended test on testCase : {}, executed in : {} ms", testCase, endTestMs - initTestMs);
        } catch (LibTestException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    private void performValidationTest(String testCase, MetadataToValidate metadataToValidate) {
        List<ExpectedValidationOutcome> expectedValidationOutcomes = TestCaseParser.decodeExpectedOutcomes(testCase);
        log.info("Obtained this expectations from testCase : {}", expectedValidationOutcomes);
        List<F24MetadataValidationIssue> validationIssues = validator.validateMetadata(metadataToValidate);
        printIssue(validationIssues);
        expectedValidationOutcomes.forEach(expectedValidationOutcome -> expectedValidationOutcome.performAssertions(validationIssues));
    }

    private void printIssue(List<F24MetadataValidationIssue> issues) {
        if (issues.isEmpty()) {
            log.info("No issues found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Issue found : \n");
        issues.forEach(issue -> {
            sb.append("Error detail: ").append(issue.getDetail()).append("\n");
            sb.append("Error element: ").append(issue.getElement()).append("\n");
            sb.append("Error code: ").append(issue.getCode()).append("\n");
            sb.append("\n");
        });

        log.info(String.valueOf(sb));
    }
}
