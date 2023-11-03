package it.pagopa.pn.f24.it.util;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.it.mockbean.F24FileCacheDaoMock;
import it.pagopa.pn.f24.it.mockbean.F24MetadataSetDaoMock;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TestUtils {
//TODO specificare se un metadata ha applycost true/false
    public static final String METADATA_SIMPLIFIED_FILEKEY = "metadataSimplified";
    public static final String METADATA_STANDARD_FILEKEY = "metadataStandard";
    private static final String ALGORITHM = "SHA-256";

    public static Map<String, String> MAP_FILEKEY_PATH = Map.of(
            METADATA_SIMPLIFIED_FILEKEY, "src/test/resources/Simplified.json",
            METADATA_STANDARD_FILEKEY, "src/test/resources/Standard.json"
    );


    public static void initializeAllMockClient(F24FileCacheDaoMock f24FileCacheDaoMock,
                                               F24MetadataSetDaoMock f24MetadataSetDaoMock) {


        ThreadPool.killThreads();

        f24FileCacheDaoMock.clear();
        f24MetadataSetDaoMock.clear();

    }

    public static byte[] getMetadataByFilekey(String fileKey) {

        Resource fileResource = new FileSystemResource(MAP_FILEKEY_PATH.get(fileKey));

        byte[] fileBytes = new byte[0];
        try {
            InputStream inputStream = fileResource.getInputStream();
            fileBytes = StreamUtils.copyToByteArray(inputStream);

        } catch (IOException e) {
        }
        return fileBytes;
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


    public static F24File createF24FileDone(String setId, Integer cost, String pathTokens) {
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setSetId(setId);
        f24File.setCost(cost);
        f24File.setPathTokens(pathTokens);
        f24File.setStatus(F24FileStatus.DONE);
        f24File.setTtl(1L);
        f24File.setCreated(Instant.now());
        f24File.setUpdated(Instant.now());
        return f24File;
    }

    public static F24File createF24FileToProcess(String setId, Integer cost, String pathTokens) {
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setSetId(setId);
        f24File.setCost(cost);
        f24File.setPathTokens(pathTokens);
        f24File.setStatus(F24FileStatus.TO_PROCESS);
        f24File.setTtl(1L);
        f24File.setCreated(Instant.now());
        f24File.setUpdated(Instant.now());
        return f24File;
    }

    public static F24File createF24FileToProcessLate(String setId, Integer cost, String pathTokens) {
        F24File f24File = new F24File();
        f24File.setFileKey("fileKey");
        f24File.setSetId(setId);
        f24File.setCost(cost);
        f24File.setPathTokens(pathTokens);
        f24File.setStatus(F24FileStatus.TO_PROCESS);
        f24File.setTtl(1L);
        f24File.setCreated(Instant.now().minus(Duration.ofMinutes(10)));
        f24File.setUpdated(Instant.now().minus(Duration.ofMinutes(9)));
        return f24File;
    }

    public static F24MetadataSet createF24MetadataSetWithApplyCost(String setId){
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setFileKey(METADATA_SIMPLIFIED_FILEKEY);
        f24MetadataSet.setSetId(setId);
        f24MetadataSet.setCreatorCxId("creatorCxId");
        f24MetadataSet.setValidatorCxId("validatorCxId");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys((Map<String, F24MetadataRef>) Map.of("key", f24MetadataRef));
        f24MetadataSet.setSha256("sha256");
        f24MetadataSet.setHaveToSendValidationEvent(false);
        f24MetadataSet.setValidationEventSent(false);
        f24MetadataSet.setValidationResult(null);
        f24MetadataSet.setCreated(null);
        f24MetadataSet.setUpdated(null);
        f24MetadataSet.setTtl(null);
        return f24MetadataSet;
    }

    public static F24MetadataSet createF24MetadataSetWithoutApplyCost(String setId){
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(false);
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setFileKey(METADATA_SIMPLIFIED_FILEKEY);
        f24MetadataSet.setSetId(setId);
        f24MetadataSet.setCreatorCxId("creatorCxId");
        f24MetadataSet.setValidatorCxId("validatorCxId");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys((Map<String, F24MetadataRef>) Map.of("key", f24MetadataRef));
        f24MetadataSet.setSha256("sha256");
        f24MetadataSet.setHaveToSendValidationEvent(false);
        f24MetadataSet.setValidationEventSent(false);
        f24MetadataSet.setValidationResult(null);
        f24MetadataSet.setCreated(null);
        f24MetadataSet.setUpdated(null);
        f24MetadataSet.setTtl(null);
        return f24MetadataSet;
    }
}
