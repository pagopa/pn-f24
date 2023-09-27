package it.pagopa.pn.f24.exception;

public class PnF24ExceptionCodes {
    public static final String ERROR_CODE_F24_PATH_TOKENS_NOT_ALLOWED = "PN_F24_PATH_TOKENS_NOT_ALLOWED";
    public static final String ERROR_CODE_F24_SET_ID_INCONGRUENT = "PN_F24_SET_ID_INCONGRUENT";
    public static final String ERROR_CODE_F24_UPLOADFILEERROR = "PN_F24_UPLOADFILEERROR";
    public static final String ERROR_CODE_F24_METADATA_VALIDATION_DIFFERENT_SHA256 = "PN_F24_METADATA_VALIDATION_DIFFERENT_SHA256";
    public static final String ERROR_CODE_F24_METADATA_VALIDATION_MULTI_TYPE = "PN_F24_METADATA_VALIDATION_MULTI_TYPE";
    public static final String ERROR_CODE_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST = "PN_F24_METADATA_VALIDATION_INCONSISTENT_APPLY_COST";
    public static final String ERROR_CODE_F24_METADATA_VALIDATION_ERROR = "PN_F24_METADATA_VALIDATION_ERROR";

    public static final String ERROR_CODE_F24_METADATA_VALIDATION_SCHEMA = "PN_F24_METADATA_VALIDATION_SCHEMA";
    public static final String ERROR_CODE_F24_ERRORCOMPUTECHECKSUM = "PN_F24_ERRORCOMPUTECHECKSUM";
    public static final String ERROR_CODE_F24_EVENTTYPENOTSUPPORTED = "PN_F24_EVENTTYPENOTSUPPORTED";
    public static final String ERROR_CODE_F24_HANDLEEVENTFAILED = "PN_F24_HANDLEEVENTFAILED";
    public static final String ERROR_CODE_F24_SAVE_METADATA_CONFLICT = "PN_F24_SAVEMETADATACONFLICT";
    public static final String ERROR_CODE_F24_METADATA_NOT_FOUND = "PN_F24_METADATANOTFOUND";
    public static final String ERROR_CODE_F24_READ_FILE_ERROR = "PN_F24_READ_FILE_ERROR";
    public static final String ERROR_CODE_F24_METADATA_PARSING = "PN_F24_METADATA_PARSING_ERROR";
    public static final String ERROR_CODE_F24_FILE_GENERATION_IN_PROGRESS = "PN_F24_FILE_GENERATION_ERROR";
    public static final String ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND = "Metadata Set with setId %s and cxId %s not found";
    public static final String ERROR_MESSAGE_F24_REQUEST_NOT_FOUND = "Request with requestId %s not found";
    public static final String ERROR_MESSAGE_F24_PATH_TOKENS_DIMENSION_NOT_ALLOWED = "pathTokens can have only %s elements";
    public static final String ERROR_MESSAGE_F24_SET_ID_INCONGRUENT = "setId given in request body is different from setId given in path";
    public static final String ERROR_MESSAGE_F24_METADATA_NOT_FOUND = "Metadata with pathTokens %s not found in Set with setId %s and cxId %s";

}
