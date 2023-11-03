package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationResponse;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;;
import it.pagopa.pn.f24.it.util.TestUtils;
import it.pagopa.pn.f24.it.util.ThreadPool;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.consumer.service.SafeStorageEventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Slf4j
public class PnSafeStorageClientMock implements PnSafeStorageClient {

    private final SafeStorageEventService safeStorageEventService;

    private final F24FileCacheDaoMock f24FileCacheDaoMock;

    public PnSafeStorageClientMock(SafeStorageEventService safeStorageEventService, F24FileCacheDaoMock f24FileCacheDaoMock) {
        this.safeStorageEventService = safeStorageEventService;
        this.f24FileCacheDaoMock = f24FileCacheDaoMock;
    }

    @Override
    public Mono<FileDownloadResponse> getFile(String fileKey, boolean metadataOnly) {
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        fileDownloadResponse.setKey("key");
        fileDownloadResponse.setChecksum("checksum");
        fileDownloadResponse.setContentLength(null);
        fileDownloadResponse.setContentType("contentType");
        fileDownloadResponse.setDocumentType("documentType");

        FileDownloadInfo fileDownloadInfo = new FileDownloadInfo();
        fileDownloadInfo.setUrl(fileKey);
        fileDownloadResponse.setDownload(fileDownloadInfo);

        return Mono.just(fileDownloadResponse);
    }

    @Override
    public Mono<FileCreationResponse> createFile(FileCreationWithContentRequest fileCreationRequest, String sha256) {

        String key = sha256;
        FileCreationResponse fileCreationResponse = new FileCreationResponse();
        fileCreationResponse.setKey(key);
        fileCreationResponse.setSecret("secret");
        fileCreationResponse.setUploadUrl("uploadUrl");

        ThreadPool.start(new Thread(() -> {
            Assertions.assertDoesNotThrow(() -> {
                log.info("[TEST] Start wait for createFile documentType={}", fileCreationRequest.getDocumentType());
                try {
                    await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
                        F24File f24File = f24FileCacheDaoMock.getItemByFileKey(key).block();
                        log.info("[TEST] Start assertion for createFile f24File={}", f24File);
                        Assertions.assertNotNull(f24File);
                    });
                    FileDownloadResponse mockedResponse = new FileDownloadResponse();
                    mockedResponse.setDocumentType(fileCreationRequest.getDocumentType());
                    log.info("documentType: {}", fileCreationRequest.getDocumentType());
                    mockedResponse.setDocumentStatus(fileCreationRequest.getStatus());
                    mockedResponse.setKey(key);
                    mockedResponse.setChecksum(sha256);
                    safeStorageEventService.handleSafeStorageResponse(mockedResponse).block();
                } catch (org.awaitility.core.ConditionTimeoutException ex) {
                    throw ex;
                }
            });
        }));
        return Mono.just(fileCreationResponse);
    }

    @Override
    public void uploadContent(FileCreationWithContentRequest fileCreationRequest, FileCreationResponse fileCreationResponse, String sha256) {

       log.info("[TEST] uploadContent documentType={}", fileCreationRequest.getDocumentType());
    }

    @Override
    public byte[] downloadPieceOfContent(String filekey, long maxSize) {
        return TestUtils.getMetadataByFilekey(filekey);
    }
}
