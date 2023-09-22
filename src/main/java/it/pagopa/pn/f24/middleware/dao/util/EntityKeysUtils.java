package it.pagopa.pn.f24.middleware.dao.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityKeysUtils {
    public static final String DEFAULT_DELIMITER_CHAR = "#";

    public static class F24MetadataSet {
        private static final int CX_ID_SECTION_INDEX = 0;
        private static final int SET_ID_SECTION_INDEX = 1;

        public static String getSetId(String pk) {
            return getSectionFromComposedPk(pk, SET_ID_SECTION_INDEX);
        }

        public static String getCxId(String pk) {
            return getSectionFromComposedPk(pk, CX_ID_SECTION_INDEX);
        }

        public static String createPk(String cxId, String setId) {
            return cxId + DEFAULT_DELIMITER_CHAR + setId;
        }
    }

    public static class F24File {
        private static final int CX_ID_SECTION_INDEX = 0;
        private static final int SET_ID_SECTION_INDEX = 1;
        private static final int NOTIFICATION_COST_SECTION_INDEX = 2;
        private static final int PATH_TOKENS_SECTION_INDEX = 3;

        public static final String NOTIFICATION_COST_NOT_GIVEN = "NO_FEE";

        public static String getSetId(String pk) {
            return getSectionFromComposedPk(pk, SET_ID_SECTION_INDEX);
        }
        public static String getCxId(String pk) {
            return getSectionFromComposedPk(pk, CX_ID_SECTION_INDEX);
        }
        public static String getNotificationCost(String pk) {
            return getSectionFromComposedPk(pk, NOTIFICATION_COST_SECTION_INDEX);
        }
        public static String getPathTokens(String pk) {
            return getSubstringFromPk(pk, PATH_TOKENS_SECTION_INDEX);
        }

        public static String createPk(String cxId, String setId, Integer notificationCost, List<String> pathTokens) {
            String pathTokensInString = getPathTokensInString(pathTokens);
            return createPk(cxId, setId, notificationCost, pathTokensInString);
        }
        public static String createPk(String cxId, String setId, Integer notificationCost, String pathTokens) {
            String processedCost = notificationCost == null ? NOTIFICATION_COST_NOT_GIVEN : String.valueOf(notificationCost);
            return cxId + DEFAULT_DELIMITER_CHAR + setId + DEFAULT_DELIMITER_CHAR + processedCost + DEFAULT_DELIMITER_CHAR + pathTokens;
        }
    }

    public static class F24Request {
        private static final int CX_ID_SECTION_INDEX = 0;
        private static final int REQUEST_ID_SECTION_INDEX = 2;

        public static String getRequestId(String pk) {
            return getSectionFromComposedPk(pk, REQUEST_ID_SECTION_INDEX);
        }

        public static String getCxId(String pk) {
            return getSectionFromComposedPk(pk, CX_ID_SECTION_INDEX);
        }

        public static String createPk(String cxId, String requestId) {
            return cxId + DEFAULT_DELIMITER_CHAR + requestId;
        }
    }

    /**
     * Composed partition keys are made up of sections of string joined by the delimiter char '#'.
     * The method allows to retrieve a single section of the key.
     * <p>
     * Example :
     * <p>
     * String pk = 'pn-delivery#IUN_001';
     * <p>
     * String section = getSectionFromComposedPk(pk, 0);
     * <p>
     * AssertEquals(section, 'pn-delivery') //true
     * @param pk composed partition key
     * @param sectionIndex desired sectionIndex (as array indexes starts from 0)
     * @return section of the key
     */
    private static String getSectionFromComposedPk(String pk, int sectionIndex) {
        return pk.split(DEFAULT_DELIMITER_CHAR)[sectionIndex];
    }

    /**
     * Composed partition keys are made up of sections of string joined by the delimiter char '#'.
     * The method allows to retrieve multiple sections of the key from the given index to the end of the key.
     * <p>
     * Example :
     * <p>
     * String pk = 'pn-delivery#IUN_001#cost#rec0#paym0';
     * <p>
     * String section = getSubstringFromPk(pk, 2);
     * <p>
     * AssertEquals(section, ''cost#rec0#paym0'); //true
     * @param pk composed partition key
     * @param startSectionIndex desired index to start the substring (as array indexes starts from 0)
     * @return substring of the key
     */
    private static String getSubstringFromPk(String pk, int startSectionIndex) {
        String[] splitPk = pk.split(DEFAULT_DELIMITER_CHAR);
        return Arrays.stream(splitPk, startSectionIndex, splitPk.length).collect(Collectors.joining(DEFAULT_DELIMITER_CHAR));
    }

    private static String getPathTokensInString(List<String> pathTokens) {
        return String.join(DEFAULT_DELIMITER_CHAR, pathTokens);
    }
}
