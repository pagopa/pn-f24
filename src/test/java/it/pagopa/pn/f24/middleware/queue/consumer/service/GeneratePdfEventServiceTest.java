package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxRecord;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.service.AuditLogService;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {GeneratePdfEventService.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class GeneratePdfEventServiceTest {
    @MockitoBean
    private F24FileCacheDao f24FileCacheDao;
    @MockitoBean
    private MetadataDownloader metadataDownloader;
    @MockitoBean
    private SafeStorageService safeStorageService;
    @MockitoBean
    private F24Generator f24Generator;
    @Autowired
    private GeneratePdfEventService generatePdfEventService;
    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    void generatePdfFailsIfFileIsNotFound() {
        GeneratePdfEvent.Payload payload = new GeneratePdfEvent.Payload("CACHE#setId#200#0_0", "setId", "metadataFileKey");
        when(f24FileCacheDao.getItem(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(generatePdfEventService.generatePdf(payload))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void generatePdfStopsIfFileIsAlreadyDone() {
        GeneratePdfEvent.Payload payload = new GeneratePdfEvent.Payload("CACHE#setId#200#0_0", "setId", "metadataFileKey");

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setStatus(F24FileStatus.DONE);
        when(f24FileCacheDao.getItem(any()))
                .thenReturn(Mono.just(f24File));

        StepVerifier.create(generatePdfEventService.generatePdf(payload))
                .expectComplete()
                .verify();
    }

    @Test
    void generatePdfSuccess() {
        GeneratePdfEvent.Payload payload = new GeneratePdfEvent.Payload("CACHE#setId#200#0_0", "setId", "metadataFileKey");

        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        f24File.setCost(200);
        f24File.setStatus(F24FileStatus.TO_PROCESS);
        when(f24FileCacheDao.getItem(any()))
                .thenReturn(Mono.just(f24File));


        F24Metadata f24Standard = buildF24MetadataWithApplyCost();
        when(metadataDownloader.downloadMetadata(any()))
                .thenReturn(Mono.just(f24Standard));
        when(f24Generator.generate(any()))
                .thenReturn("file".getBytes(StandardCharsets.UTF_8));

        FileCreationResponseInt fileCreationResponseInt = new FileCreationResponseInt();
        fileCreationResponseInt.setKey("f24Key");
        when(safeStorageService.createAndUploadContent(any()))
                .thenReturn(Mono.just(fileCreationResponseInt));

        doNothing().when(auditLogService).buildGeneratePdfAuditLogEvent(any(), any(), any(), any(), any());

        when(f24FileCacheDao.setFileKey(any(), any()))
                .thenReturn(Mono.just(new F24File()));

        StepVerifier.create(generatePdfEventService.generatePdf(payload))
                .expectComplete()
                .verify();
    }

    private F24Metadata buildF24MetadataWithApplyCost() {
        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();
        LocalTaxSection localTaxSection = new LocalTaxSection();
        LocalTaxRecord localTaxRecord = new LocalTaxRecord();
        localTaxRecord.setApplyCost(true);
        localTaxRecord.setDebit(200);
        localTaxSection.setRecords(List.of(localTaxRecord));
        f24Standard.setLocalTax(localTaxSection);
        f24Metadata.setF24Standard(f24Standard);
        return f24Metadata;
    }
}