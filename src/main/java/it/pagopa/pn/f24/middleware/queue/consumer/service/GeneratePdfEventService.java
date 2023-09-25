package it.pagopa.pn.f24.middleware.queue.consumer.service;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.safestorage.FileCreationResponseInt;
import it.pagopa.pn.f24.dto.safestorage.FileCreationWithContentRequest;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.queue.producer.events.GeneratePdfEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.service.SafeStorageService;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Instant;

@Service
@CustomLog
@AllArgsConstructor
public class GeneratePdfEventService {

    private static final String SAVED = "SAVED";
    private F24Config f24Config;
    private F24MetadataSetDao f24MetadataSetDao;

    private F24FileCacheDao f24FileCacheDao;
    private MetadataDownloader metadataDownloader;
    private SafeStorageService safeStorageService;
    private F24Generator f24Generator;

    public Mono<Void> generatePdf(GeneratePdfEvent.Payload payload) {

        log.info(
                "generate f24 pdf file for metadata with cxId: {}, setId: {} and pathTokens:{} ",
                payload.getCxId(), payload.getSetId(), payload.getPathTokens()
        );
        final String processName = "PREPARE PDF HANDLER";
        log.logStartingProcess(processName);

        return getF24File(payload.getFilePk())
                .flatMap(this::checkF24FileStatus)
                .zipWith(getMetadataSet(payload.getSetId(), payload.getCxId()))
                .flatMap(tuple -> generateF24Pdf(tuple.getT2(), tuple.getT1()))
                .doOnNext(unused -> log.logEndingProcess(processName))
                .doOnError(throwable -> log.logEndingProcess(processName, false, throwable.getMessage()));
    }

    private Mono<F24File> getF24File(String filePk) {
        return f24FileCacheDao.getItem(filePk)
                .switchIfEmpty(
                        Mono.defer(
                                () -> {
                                    log.warn("F24File with ok {} not found", filePk);
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


    private Mono<F24MetadataSet> getMetadataSet(String setId, String cxId) {
        return f24MetadataSetDao.getItem(setId, cxId)
                .doOnError(t -> log.info("Error",t))
                .switchIfEmpty(Mono.error(
                        new PnNotFoundException(
                                "MetadataSet not found",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId, cxId),
                                PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                        )
                ));
    }

    private Mono<Void> generateF24Pdf(F24MetadataSet f24MetadataSet, F24File f24File) {
        String metadataFileKey = f24MetadataSet.getFileKeys()
                .get(f24File.getPathTokens())
                .getFileKey();

        return metadataDownloader.downloadMetadata(metadataFileKey)
                .map(f24Generator::generate)
                .doOnError(throwable -> log.warn("Error generating pdf for file with pk: {}", f24File.getPk(), throwable))
                .map(this::buildFileCreationRequest)
                .flatMap(safeStorageService::createAndUploadContent)
                .doOnError(throwable -> log.warn("Couldn't upload F24File with pk {} on safe storage", f24File.getPk(), throwable))
                .flatMap(fileCreationResponseInt -> setFileKeyToF24File(fileCreationResponseInt, f24File));
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
        f24File.setFileKey(fileCreationResponseInt.getKey());
        f24File.setUpdated(Instant.now());
        f24File.setStatus(F24FileStatus.GENERATED);

        return f24FileCacheDao.setFileKeyToF24File(f24File)
                .doOnError(throwable -> log.warn("Error updating record f24File with pk {}", f24File.getPk()))
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.debug("f24File with pk {} already in status GENERATED", f24File.getPk());
                    return Mono.empty();
                })
                .then();
    }
}
