package it.pagopa.pn.f24.it.util;

import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.it.mockbean.*;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import it.pagopa.pn.f24.util.Sha256Handler;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static final String METADATA_SIMPLIFIED_WITH_COST_FILEKEY = "metadataSimplifiedWithCost";
    public static final String INVALID_METADATA_SIMPLIFIED_WITH_COST_FILEKEY = "invalidMetadataSimplifiedWithCost";
    public static final String METADATA_SIMPLIFIED_WITHOUT_COST_FILEKEY = "metadataSimplifiedWithoutCost";

    public static Map<String, String> MAP_FILEKEY_PATH = Map.of(
            METADATA_SIMPLIFIED_WITH_COST_FILEKEY, "src/test/resources/metadata/SimplifiedWithApplyCost.json",
            METADATA_SIMPLIFIED_WITHOUT_COST_FILEKEY, "src/test/resources/metadata/SimplifiedWithoutApplyCost.json",
            INVALID_METADATA_SIMPLIFIED_WITH_COST_FILEKEY, "src/test/resources/metadata/InvalidSimplifiedWithApplyCost.json"
    );


    public static void initializeAllMockClient(List<ClearableMock> mocks) {
        ThreadPool.killThreads();
        mocks.forEach(ClearableMock::clear);
    }

    public static byte[] getMetadataByFilekey(String fileKey) {

        Resource fileResource = new FileSystemResource(MAP_FILEKEY_PATH.get(fileKey));

        byte[] fileBytes = new byte[0];
        try {
            InputStream inputStream = fileResource.getInputStream();
            fileBytes = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            return fileBytes;
        }
        return fileBytes;
    }

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
    public static F24MetadataSet createF24MetadataSetByTestCases(String setId, List<TestCase> testCases){
        Map<String, F24MetadataRef> fileKeys = buildFileKeysByTestCases(testCases);
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId(setId);
        f24MetadataSet.setCreatorCxId("creatorCxId");
        f24MetadataSet.setValidatorCxId("validatorCxId");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setFileKeys(fileKeys);
        f24MetadataSet.setSha256("sha256");
        f24MetadataSet.setCreated(Instant.now());
        return f24MetadataSet;
    }
    private static Map<String,F24MetadataRef> buildFileKeysByTestCases(List<TestCase> testCases) {
        Map<String, F24MetadataRef> fileKeys = new HashMap<>();
        for(int i=0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            F24MetadataRef f24MetadataRef = new F24MetadataRef();
            f24MetadataRef.setApplyCost(testCase.getApplyCost());
            f24MetadataRef.setFileKey(testCase.getFileKey());
            f24MetadataRef.setSha256(Sha256Handler.computeSha256(getMetadataByFilekey(testCase.getFileKey())));
            fileKeys.put("0_" + i, f24MetadataRef);
        }

        return fileKeys;
    }

    public static F24MetadataSet createF24MetadataSetWithApplyCost(String setId){
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setFileKey(METADATA_SIMPLIFIED_WITH_COST_FILEKEY);
        f24MetadataSet.setSetId(setId);
        f24MetadataSet.setCreatorCxId("creatorCxId");
        f24MetadataSet.setValidatorCxId("validatorCxId");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));
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
        f24MetadataRef.setFileKey(METADATA_SIMPLIFIED_WITHOUT_COST_FILEKEY);
        f24MetadataSet.setSetId(setId);
        f24MetadataSet.setCreatorCxId("creatorCxId");
        f24MetadataSet.setValidatorCxId("validatorCxId");
        f24MetadataSet.setStatus(F24MetadataStatus.TO_VALIDATE);
        f24MetadataSet.setFileKeys(Map.of("key", f24MetadataRef));
        f24MetadataSet.setSha256("sha256");
        f24MetadataSet.setHaveToSendValidationEvent(false);
        f24MetadataSet.setValidationEventSent(false);
        f24MetadataSet.setValidationResult(null);
        f24MetadataSet.setCreated(null);
        f24MetadataSet.setUpdated(null);
        f24MetadataSet.setTtl(null);
        return f24MetadataSet;
    }

    public static boolean checkMetadataSetIsValid(String setId, F24MetadataSetDaoMock f24MetadataSetDaoMock) {
        F24MetadataSet metadataSet = f24MetadataSetDaoMock.getItem(setId).block();
        return metadataSet != null && metadataSet.getStatus() == F24MetadataStatus.VALIDATION_ENDED && CollectionUtils.isEmpty(metadataSet.getValidationResult());
    }

    public static boolean checkMetadataSetValidationEventIsSent(String setId, F24MetadataSetDaoMock f24MetadataSetDaoMock) {
        F24MetadataSet metadataSet = f24MetadataSetDaoMock.getItem(setId).block();
        return metadataSet != null && metadataSet.getStatus() == F24MetadataStatus.VALIDATION_ENDED && metadataSet.getValidationEventSent();
    }

    public static boolean checkMetadataSetValidationEnded(String setId, F24MetadataSetDaoMock f24MetadataSetDaoMock) {
        F24MetadataSet metadataSet = f24MetadataSetDaoMock.getItem(setId).block();
        return metadataSet != null && metadataSet.getStatus() == F24MetadataStatus.VALIDATION_ENDED;
    }

    public static void checkSuccessfulF24Generation(String setId, List<String> pathTokens, Integer cost, F24FileCacheDaoMock f24FileCacheDaoMock, SafeStorageEventService safeStorageEventService) {
        F24File f24File = f24FileCacheDaoMock.getItem(setId, cost, String.join("_", pathTokens)).block();
        Assertions.assertTrue(f24File != null && f24File.getStatus() == F24FileStatus.DONE);
        Mockito.verify(safeStorageEventService, Mockito.times(1)).handleSafeStorageResponse(Mockito.any());
    }

    public static void checkPrepareRequestEndedSuccessfully(String requestId, F24FileRequestDaoMock f24FileRequestDaoMock) {
        F24Request f24Request = f24FileRequestDaoMock.getItem(requestId).block();
        Assertions.assertTrue(f24Request != null && f24Request.getStatus() == F24RequestStatus.DONE);
    }

    public static void checkEventSentOnEventBridge(boolean statusOk, EventBridgeEventType eventBridgeType, EventBridgeAsyncClientMock eventBridgeAsyncClientMock) {
        String statusToCheck = statusOk ? "OK" : "KO";
        ArgumentCaptor<PutEventsRequest> argumentCaptor = ArgumentCaptor.forClass(PutEventsRequest.class);
        Mockito.verify(eventBridgeAsyncClientMock).putEvents(argumentCaptor.capture());
        String jsonDetailSent = argumentCaptor.getValue().entries().get(0).detail();
        Assertions.assertTrue(jsonDetailSent.contains(eventBridgeType.getEventFieldInDetailObject()));
        Assertions.assertTrue(jsonDetailSent.contains("\"status\":\"" +statusToCheck+ "\""));
    }
}
