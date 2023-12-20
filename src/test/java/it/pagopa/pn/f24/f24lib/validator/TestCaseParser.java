package it.pagopa.pn.f24.f24lib.validator;

import it.pagopa.pn.f24.f24lib.util.LibTestException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TestCaseParser {
    private TestCaseParser() {
    }

    private static final String TOKEN_DIVIDER = "_";

    public static List<ExpectedValidationOutcome> decodeExpectedOutcomes(String testCase) {
        List<ExpectedValidationOutcome> expectedOutcomes = extractTokens(testCase);

        checkConsistencyOfTokens(expectedOutcomes);

        return expectedOutcomes;
    }

    private static List<ExpectedValidationOutcome> extractTokens(String testCase) {
        List<String> tokens = new ArrayList<>(Arrays.stream(testCase.split(TOKEN_DIVIDER)).toList());

        if (tokens.size() < 2) {
            log.info("no token found");
            return List.of(ExpectedValidationOutcome.VALID);
        }

        checkFirstItemIsNotValidationOutcome(tokens.get(0));

        tokens.remove(0);
        //Rimuovo l'estensione del file dall'ultimo elemento dei token.
        tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 1).replace(".json", ""));

        return tokens.stream().map(token -> {
            try {
                return ExpectedValidationOutcome.fromValue(token);
            } catch (NoSuchElementException ex) {
                testFail(String.format("Couldn't get an expected validation outcome from this part of the testCase: %s, accepted values: %s", token, ExpectedValidationOutcome.getAcceptedValues()));
            }
            return null;
        }).toList();
    }

    private static void checkFirstItemIsNotValidationOutcome(String token) {
        if (ExpectedValidationOutcome.isAnExpectedValidationOutcome(token)) {
            testFail("First element of test case can't be an expected validation outcome");
        }
    }

    private static void checkConsistencyOfTokens(List<ExpectedValidationOutcome> expectedOutcomes) {
        checkDuplicates(expectedOutcomes);

        if (expectedOutcomes.contains(ExpectedValidationOutcome.VALID) && expectedOutcomes.size() > 1) {
            testFail("TestCase inconsistent outcomes: Can't ask for VALID and INVALID outcome at same time.");
        }

        if (expectedOutcomes.contains(ExpectedValidationOutcome.INVALID_PARSING) && expectedOutcomes.size() > 1) {
            testFail("TestCase inconsistent outcomes: If there is an error parsing the metadata json, other validations won't be executed.");
        }

    }

    private static void checkDuplicates(List<ExpectedValidationOutcome> expectedOutcomes) {
        Set<ExpectedValidationOutcome> expectedOutcomesSet = new HashSet<>();

        expectedOutcomes.forEach(expectedOutcome -> {
            if (!expectedOutcomesSet.add(expectedOutcome)) {
                testFail("TestCase has duplicate for this ExpectedOutcome: " + expectedOutcome);
            }
        });

    }

    private static void testFail(String message) {
        throw new LibTestException(message);
    }
}
