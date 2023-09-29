package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import it.pagopa.pn.f24.util.Utility;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Service
@CustomLog
@AllArgsConstructor
public class GeneratePdfEventService {

    private static final String SAVED = "SAVED";
    private F24Config f24Config;
    private F24FileCacheDao f24FileCacheDao;
    private MetadataDownloader metadataDownloader;
    private SafeStorageService safeStorageService;
    private F24Generator f24Generator;

    public Mono<Void> generatePdf(GeneratePdfEvent.Payload payload) {

        log.info(
                "generate f24 pdf file for metadata with setId: {} and fileKey:{} ",
                payload.getSetId(), payload.getMetadataFileKey()
        );
        final String processName = "PREPARE PDF HANDLER";
        log.logStartingProcess(processName);

        return getF24File(payload.getFilePk())
                .flatMap(this::checkF24FileStatus)
                .flatMap(f24File -> generateF24Pdf(payload.getMetadataFileKey(), f24File))
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()));
    }

    private Mono<F24File> getF24File(String filePk) {
        return f24FileCacheDao.getItem(filePk)
                .switchIfEmpty(
                        Mono.defer(
                                () -> {
                                    log.warn("F24File with pk {} not found", filePk);
                                    return Mono.error(
                                            new PnNotFoundException(
                                                    "F24File not found",
                                                    String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_FILE_NOT_FOUND, filePk),
                                                    PnF24ExceptionCodes.ERROR_CODE_F24_FILE_NOT_FOUND
                                            )
                                    );
                                }
                        )
                );
    }

    private Mono<F24File> checkF24FileStatus(F24File f24File) {
        if(f24File.getStatus() == F24FileStatus.GENERATED || f24File.getStatus() == F24FileStatus.DONE) {
            log.warn("File with pk: {} is already in status: {}", f24File.getPk(), f24File.getStatus().getValue());
            return Mono.empty();
        }

        return Mono.just(f24File);
    }

    private Mono<Void> generateF24Pdf(String metadataFileKey, F24File f24File) {
        return metadataDownloader.downloadMetadata(metadataFileKey)
                .map(f24Metadata -> applyCost(f24Metadata, f24File))
                .map(f24Generator::generate)
                .doOnError(throwable -> log.warn("Error generating pdf for file with pk: {}", f24File.getPk(), throwable))
                .map(this::buildFileCreationRequest)
                .flatMap(safeStorageService::createAndUploadContent)
                .doOnError(throwable -> log.warn("Couldn't upload F24File with pk {} on safe storage", f24File.getPk(), throwable))
                .flatMap(fileCreationResponseInt -> setFileKeyToF24File(fileCreationResponseInt, f24File));
    }

    private F24Metadata applyCost(F24Metadata f24Metadata, F24File f24File) {
        if(f24File.getCost() != null && f24File.getCost() > 0) {
            log.debug("Applying {} euro-cents to F24File with pk: {}", f24File.getCost(), f24File.getPk());
            F24Type f24Type = Utility.getF24TypeFromMetadata(f24Metadata);
            MetadataInspector metadataInspector = MetadataInspectorFactory.getInspector(f24Type);
            metadataInspector.addCostToDebit(f24Metadata, f24File.getCost());
        }

        return f24Metadata;
    }

    private FileCreationWithContentRequest buildFileCreationRequest(byte[] pdfContent) {
        log.info("Building FileCreationRequest to upload generated pdf on safestorage");
        FileCreationWithContentRequest fileCreationWithContentRequest = new FileCreationWithContentRequest();
        fileCreationWithContentRequest.setContentType(MediaType.APPLICATION_PDF_VALUE);
        fileCreationWithContentRequest.setDocumentType(f24Config.getSafeStorageF24DocType());
        fileCreationWithContentRequest.setStatus(SAVED);
        fileCreationWithContentRequest.setContent(pdfContent);
        return fileCreationWithContentRequest;
    }

    private Mono<Void> setFileKeyToF24File(FileCreationResponseInt fileCreationResponseInt, F24File f24File) {
        log.debug("Updating F24File with pk:{}, setting fileKey: {} and status: GENERATED", f24File.getPk(), fileCreationResponseInt.getKey());

        return f24FileCacheDao.setFileKey(f24File, fileCreationResponseInt.getKey())
                .doOnError(throwable -> log.warn("Error updating record f24File with pk {}", f24File.getPk()))
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.debug("f24File with pk {} already in status GENERATED", f24File.getPk());
                    return Mono.empty();
                })
                .then();
    }
}
