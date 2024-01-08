package it.pagopa.pn.f24.f24lib.util;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.f24lib.exception.LibTestException;
import it.pagopa.pn.f24.f24lib.parser.IntegratedField;
import it.pagopa.pn.f24.f24lib.parser.MetadataFieldsMapperFactory;
import it.pagopa.pn.f24.f24lib.parser.PdfParser;
import it.pagopa.pn.f24.f24lib.parser.PdfParserReporter;
import it.pagopa.pn.f24.f24lib.parser.mapper.MetadataFieldsToPdfFieldsMapper;
import it.pagopa.pn.f24.f24lib.validator.ExpectedValidationOutcome;
import it.pagopa.pn.f24.f24lib.validator.MetadataToValidateBuilder;
import it.pagopa.pn.f24.f24lib.validator.TestCaseParser;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.MetadataValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;

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
        execTest(testCase, shouldHaveApplyCost, false);
    }

    public void execTest(String testCase, boolean shouldHaveApplyCost, boolean shouldParsePdf) {
        try {
            long initTestMs = new Date().getTime();
            log.info("Starting test on testCase : {}", testCase);
            MetadataToValidate metadataToValidate = MetadataToValidateBuilder.metaBuilder(testCase, shouldHaveApplyCost);
            boolean isValid = performValidationTest(testCase, metadataToValidate);

            if (isValid && shouldParsePdf) {
                performPdfParsing(metadataToValidate.getMetadataFile());
            }

            long endTestMs = new Date().getTime();
            log.info("Ended test on testCase : {}, executed in : {} ms", testCase, endTestMs - initTestMs);
        } catch (LibTestException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    private boolean performValidationTest(String testCase, MetadataToValidate metadataToValidate) {
        List<ExpectedValidationOutcome> expectedValidationOutcomes = TestCaseParser.decodeExpectedOutcomes(testCase);
        log.info("Obtained this expectations from testCase : {}", expectedValidationOutcomes);
        List<F24MetadataValidationIssue> validationIssues = validator.validateMetadata(metadataToValidate);
        printIssue(validationIssues);
        expectedValidationOutcomes.forEach(expectedValidationOutcome -> expectedValidationOutcome.performAssertions(validationIssues));

        return validationIssues.isEmpty();
    }

    private void performPdfParsing(byte[] metadataFile) {
        F24Metadata f24Metadata = jsonService.parseMetadataFile(metadataFile);

        byte[] generatedPdf = f24Generator.generate(f24Metadata);
        PdfParser parser = new PdfParser(generatedPdf);

        int nPages = parser.numberOfPages();
        F24Type f24Type = getF24TypeFromMetadata(f24Metadata);
        if (pdfHasCopies(nPages, f24Type)) {
            log.info("The generated PDF has copies therefore the analysis will not be performed.");
            return;
        }

        try {
            log.info("Starting pdf parsing");
            MetadataFieldsToPdfFieldsMapper metadataFieldsToPdfFieldsMapper = MetadataFieldsMapperFactory.getMapper(f24Type);
            List<IntegratedField> integratedField = metadataFieldsToPdfFieldsMapper.connectMetadataFieldsToPdfFields(f24Metadata);
            PdfParserReporter pdfParserReporter = new PdfParserReporter(parser, integratedField);
            pdfParserReporter.executeAnalysisAndProduceReport();

            MetadataInspector metadataInspector = MetadataInspectorFactory.getInspector(f24Type);
            double totalMetadataDebit = metadataInspector.getTotalAmount(f24Metadata);
            double totalAmountPdf = parser.getTotalAmountPdf();

            if (totalMetadataDebit != totalAmountPdf) {
                throw new LibTestException("Total amount in metadata: " + totalMetadataDebit + " is different from total amount in pdf: " + totalAmountPdf);
            }
            log.info("The total debt defined in the metadata is congruent with the total debt set on the pdf. Debt: {}", totalAmountPdf);
        } finally {
            parser.closeDocument();
        }
    }

    private boolean pdfHasCopies(int nPages, F24Type f24Type) {
        switch(f24Type) {
            case F24_ELID, F24_STANDARD, F24_EXCISE -> {
                return nPages > 3;
            }
            case F24_SIMPLIFIED -> {
                return nPages > 1;
            }
        }

        return true;
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
