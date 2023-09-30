package it.pagopa.pn.f24.middleware.queue.consumer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.F24RequestStatus;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {SafeStorageEventService.class, F24Config.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class SafeStorageEventServiceTest {
    @Autowired
    private F24Config f24Config;
    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;
    @MockBean
    private EventBridgeProducer<PnF24PdfSetReadyEvent> pdfSetReadyEventProducer;
    @Autowired
    private SafeStorageEventService safeStorageEventService;
    @MockBean
    private F24FileCacheDao f24FileCacheDao;
    @MockBean
    private F24FileRequestDao f24FileRequestDao;

    @Test
    void testHandleSafeStorageResponseOkForFileNotRelatedToPdfMultiRequest() {
        FileDownloadResponse response = new FileDownloadResponse();
        response.setKey("key_0_test");
        response.setDocumentType(f24Config.getSafeStorageF24DocType());

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setFileKey("key_0_test");
        f24File.setStatus(F24FileStatus.GENERATED);
        f24File.setRequestIds(new ArrayList<>());
        when(f24FileCacheDao.getItemByFileKey(any()))
                .thenReturn(Mono.just(f24File));
        when(f24FileCacheDao.setStatusDone(any()))
                .thenReturn(Mono.just(f24File));

        StepVerifier.create(safeStorageEventService.handleSafeStorageResponse(response))
                .expectComplete()
                .verify();
    }

    @Test
    void testHandleSafeStorageResponseOkForFileRelatedToPdfMultiRequest() {
        FileDownloadResponse response = new FileDownloadResponse();
        response.setKey("key_0_test");
        response.setDocumentType(f24Config.getSafeStorageF24DocType());

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setFileKey("key_0_test");
        f24File.setStatus(F24FileStatus.GENERATED);
        List<String> requestIds = new ArrayList<>();
        requestIds.add("REQUEST#request0");
        f24File.setRequestIds(requestIds);
        when(f24FileCacheDao.getItemByFileKey(any()))
                .thenReturn(Mono.just(f24File));

        F24Request f24Request = new F24Request();
        f24Request.setPk("REQUEST#request0");
        f24Request.setRequestId("request0");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        Map<String, F24Request.FileRef> fileRefMap = new HashMap<>();
        fileRefMap.put("CACHE#setId#200#0_0", new F24Request.FileRef(""));
        f24Request.setFiles(fileRefMap);
        f24Request.setRecordVersion(1);
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        when(f24FileRequestDao.updateTransactionalFileAndRequests(any(), any()))
                .thenReturn(Mono.empty());

        F24Request updatedF24Request = new F24Request();
        updatedF24Request.setPk("REQUEST#request0");
        updatedF24Request.setRequestId("request0");
        updatedF24Request.setStatus(F24RequestStatus.TO_PROCESS);
        Map<String, F24Request.FileRef> fileRefMapUpdated = new HashMap<>();
        fileRefMapUpdated.put("CACHE#setId#200#0_0", new F24Request.FileRef("key_0_test"));
        updatedF24Request.setFiles(fileRefMapUpdated);
        updatedF24Request.setRecordVersion(0);
        /*
         */
        when(f24FileRequestDao.getItem(any(), anyBoolean()))
                .thenReturn(Mono.just(updatedF24Request));

        doNothing().when(pdfSetReadyEventProducer).sendEvent(any());

        f24Request.setStatus(F24RequestStatus.DONE);
        when(f24FileRequestDao.setRequestStatusDone(any()))
                .thenReturn(Mono.just(f24Request));

        StepVerifier.create(safeStorageEventService.handleSafeStorageResponse(response))
                .expectComplete()
                .verify();
    }

}

