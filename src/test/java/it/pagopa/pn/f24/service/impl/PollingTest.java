package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.service.SafeStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PollingTest {
    @MockBean
    private SafeStorageService safeStorageService;


    @Test
    public void generatePDFFromMetadata() {


        //mock for SafeStorageService.getFile
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);


        // when(safeStorageService.getFile(anyString(), eq(false)))
        //       .thenThrow(new RuntimeException("File not found"));

        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(new FileDownloadResponseInt()));

        when(safeStorageService.getFile(anyString(), eq(false)))
                .thenReturn(Mono.just(fileDownloadResponseInt));

        // Assert
        StepVerifier.create(pollingTest())
                .expectNextMatches(f24Response -> f24Response.getDownload() != null)
                .expectComplete()
                .verify();

    }

    @Test
    public Mono<FileDownloadResponseInt> pollingTest() {

        return Flux.interval(Duration.ofSeconds(2))
                .flatMap(i -> getFile("key", i)
                        .onErrorResume(WebClientException.class, e -> Mono.empty()))
                .doOnNext(response -> {
                    if (response.getDownload() == null)
                        System.out.println("response has download null");
                    if (response.getDownload() != null)
                        System.out.println("response has download not null");
                })
                .doOnError(e -> System.out.println("error polling safeStorage: " + e.getMessage()))
                .takeUntil(response -> response.getDownload() != null)
                .take(Duration.ofSeconds(5))
                .last()
                .onErrorResume(NoSuchElementException.class, e -> Mono.just((buildRetryAfterResponse())))
                .publishOn(Schedulers.boundedElastic());

    }

    private Mono<FileDownloadResponseInt> getFile(String key, Long tick) {

        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setUrl("url");
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);

        if (tick >= 3) {
            System.out.println("tick: " + tick + " ritorno oggetto ok");
            return Mono.just(fileDownloadResponseInt);
        }
        System.out.println("tick: " + tick + " ritorno oggetto vuoto");
        return Mono.error(new RuntimeException("File not found"));


    }

    private FileDownloadResponseInt buildRetryAfterResponse() {
        FileDownloadResponseInt fileDownloadResponseInt = new FileDownloadResponseInt();
        FileDownloadInfoInt fileDownloadInfoInt = new FileDownloadInfoInt();
        fileDownloadInfoInt.setRetryAfter(BigDecimal.valueOf(50));
        fileDownloadResponseInt.setDownload(fileDownloadInfoInt);
        return fileDownloadResponseInt;
    }
}
