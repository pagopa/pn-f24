package it.pagopa.pn.f24.util;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha256Handler {
    private final static String ALGORITHM = "SHA-256";
    public static String computeSha256(String content) {
        return computeSha256(content.getBytes(StandardCharsets.UTF_8));
    }

    public static String computeSha256(byte[] content) {
        try {
            byte[] hash = MessageDigest.getInstance(ALGORITHM)
                    .digest(content);
            return bytesToBase64(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new PnInternalException("Error computing sha256", PnF24ExceptionCodes.ERROR_CODE_F24_ERRORCOMPUTECHECKSUM);
        }
    }

    private static String bytesToBase64(byte[] hash) { return Base64.getEncoder().encodeToString(hash); }

}
