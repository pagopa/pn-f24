package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.middleware.queue.producer.events.PreparePdfEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PreparePdfEventService.class, F24Config.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class PreparePdfEventServiceTest {
    @MockitoBean
    private F24FileCacheDao f24FileCacheDao;
    @MockitoBean
    private F24MetadataSetDao f24MetadataSetDao;
    @MockitoBean
    private F24FileRequestDao f24FileRequestDao;
    @MockitoBean
    private MomProducer<GeneratePdfEvent> generatePdfEventProducer;
    @MockitoBean
    private EventBridgeProducer<PnF24PdfSetReadyEvent> pdfSetReadyEventProducer;
    @Autowired
    PreparePdfEventService preparePdfEventService;

    @Test
    void testPreparePDFFailWhenRequestIsNotFound() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void testPreparePDFStopsWhenRequestIsAlreadyDone() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setStatus(F24RequestStatus.DONE);
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectComplete()
                .verify();
    }

    @Test
    void testPreparePDFFailsWhenMetadataSetIsNotFound() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setSetId("setId");
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void testPreparePDFFailsWhenMetadataSetIsFoundButHasNotRequestedPathTokens() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setSetId("setId");
        f24Request.setPathTokens("notExisting");
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        f24MetadataSet.setFileKeys(new HashMap<>());
        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void testPreparePDFOKWhenAllRequestedFilesAreInCache() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setSetId("setId");
        f24Request.setPathTokens("0_0");
        f24Request.setRecordVersion(0);
        f24Request.setFiles(new HashMap<>());
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("metadataFileKey");
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        Map<String, F24MetadataRef> fileKeys = new HashMap<>();
        fileKeys.put("0_0", f24MetadataRef);
        f24MetadataSet.setFileKeys(fileKeys);
        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        F24File fileInCache = new F24File();
        fileInCache.setPk("CACHE#setId#200#0_0");
        fileInCache.setSetId("setId");
        fileInCache.setCost(200);
        fileInCache.setPathTokens("0_0");
        fileInCache.setFileKey("fileFileKey");
        fileInCache.setStatus(F24FileStatus.DONE);
        fileInCache.setCreated(Instant.now());

        when(f24FileCacheDao.getItem(any(), any(), any()))
                .thenReturn(Mono.just(fileInCache));

        when(pdfSetReadyEventProducer.sendEvent((PnF24PdfSetReadyEvent) any())).thenReturn(Mono.empty());

        when(f24FileRequestDao.setRequestStatusDone(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectComplete()
                .verify();
    }

    @Test
    void testPreparePDFOKWhenRequestedFilesAreNotReady() {
        PreparePdfEvent.Payload payload = new PreparePdfEvent.Payload("requestId");

        F24Request f24Request = new F24Request();
        f24Request.setRequestId("requestId");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setSetId("setId");
        f24Request.setPathTokens("0_0");
        f24Request.setRecordVersion(0);
        when(f24FileRequestDao.getItem(any()))
                .thenReturn(Mono.just(f24Request));

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("setId");
        Map<String, F24MetadataRef> fileKeys = new HashMap<>();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setFileKey("metadataFileKey");
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        fileKeys.put("0_0", f24MetadataRef);
        f24MetadataSet.setFileKeys(fileKeys);
        when(f24MetadataSetDao.getItem(any()))
                .thenReturn(Mono.just(f24MetadataSet));

        F24File fileInCache = new F24File();
        fileInCache.setPk("CACHE#setId#200#0_0");
        fileInCache.setSetId("setId");
        fileInCache.setCost(200);
        fileInCache.setPathTokens("0_0");
        fileInCache.setFileKey("fileFileKey");
        fileInCache.setStatus(F24FileStatus.TO_PROCESS);
        fileInCache.setCreated(Instant.now());

        when(f24FileCacheDao.getItem(any(), any(), any()))
                .thenReturn(Mono.just(fileInCache));

        doNothing().when(generatePdfEventProducer).push((GeneratePdfEvent) any());

        when(f24FileRequestDao.updateRequestAndRelatedFiles(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(preparePdfEventService.preparePdf(payload))
                .expectComplete()
                .verify();
    }
}