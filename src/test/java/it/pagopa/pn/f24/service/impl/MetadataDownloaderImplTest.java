package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.service.JsonService;
import it.pagopa.pn.f24.service.SafeStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {MetadataDownloaderImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
class MetadataDownloaderImplTest {

    @MockitoBean
    private SafeStorageService safeStorageService;
    @Autowired
    private MetadataDownloaderImpl metadataDownloader;
    @MockitoBean
    private JsonService jsonService;


    @Test
    void testDownloadMetadata() {

        F24Metadata f24Metadata = new F24Metadata();

        String fileKey = "testFileKey";
        byte[] fileContent = "testContent".getBytes();

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        fileDownloadInfoInt.setUrl("url");
        fileDownloadInfoInt.setRetryAfter(BigDecimal.valueOf(200));

        Mockito.when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(safeStorageService.downloadPieceOfContent(any(), any(), anyLong()))
                .thenReturn(Mono.just(fileContent));
        when(jsonService.parseMetadataFile(any()))
                .thenReturn(f24Metadata);

        StepVerifier.create(metadataDownloader.downloadMetadata(fileKey))
                .expectNext(f24Metadata)
                .expectComplete()
                .verify();

    }

    @Test
    void testDownloadMetadataInternalException() {

        String fileKey = "testFileKey";

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        fileDownloadInfoInt.setRetryAfter(BigDecimal.valueOf(2000000));

        Mockito.when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));

        StepVerifier.create(metadataDownloader.downloadMetadata(fileKey))
                .expectError(PnInternalException.class)
                .verify();

    }

    @Test
    void testDownloadMetadataRetry() {

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());

        String fileKey = "testFileKey";

        byte[] fileContent = "testContent".getBytes();

        FileDownloadResponseInt fileDownloadResponseIntRetryAfter = new FileDownloadResponseInt();
        FileDownloadResponseInt fileDownloadResponseIntUrl = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoIntRetryAfter = new FileDownloadInfoInt();
        FileDownloadInfoInt fileDownloadInfoIntUrl = new FileDownloadInfoInt();

        fileDownloadInfoIntRetryAfter.setRetryAfter(BigDecimal.valueOf(20));
        fileDownloadInfoIntUrl.setUrl("url");


        fileDownloadResponseIntRetryAfter.setDownload(fileDownloadInfoIntRetryAfter);
        fileDownloadResponseIntUrl.setDownload(fileDownloadInfoIntUrl);

        when(safeStorageService.downloadPieceOfContent(any(), any(), anyLong()))
                .thenReturn(Mono.just(fileContent));
        Mockito.when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseIntRetryAfter))
                .thenReturn(Mono.just(fileDownloadResponseIntUrl));
        when(jsonService.parseMetadataFile(any()))
                .thenReturn(f24Metadata);

        StepVerifier.create(metadataDownloader.downloadMetadata(fileKey))
                .expectNext(f24Metadata)
                .expectComplete()
                .verify();
    }

    @Test
    void testDownloadMetadataRetryError() {

        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setF24Standard(new F24Standard());

        String fileKey = "testFileKey";

        byte[] fileContent = "testContent".getBytes();

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();

        fileDownloadInfoInt.setRetryAfter(BigDecimal.valueOf(20));
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        when(safeStorageService.downloadPieceOfContent(any(), any(), anyLong()))
                .thenReturn(Mono.just(fileContent));
        Mockito.when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));
        when(jsonService.parseMetadataFile(any()))
                .thenReturn(f24Metadata);

        StepVerifier.create(metadataDownloader.downloadMetadata(fileKey))
                .expectError(PnInternalException.class)
                .verify();

    }
}
