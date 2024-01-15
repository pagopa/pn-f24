package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.NumberOfPagesResponse;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.service.F24ParserService;
import it.pagopa.pn.f24.service.MetadataDownloader;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Slf4j
public class F24ParserServiceImpl implements F24ParserService {
    private final F24MetadataSetDao f24MetadataSetDao;

    private final MetadataDownloader metadataDownloader;

    public F24ParserServiceImpl(F24MetadataSetDao f24MetadataSetDao, MetadataDownloader metadataDownloader) {
        this.f24MetadataSetDao = f24MetadataSetDao;
        this.metadataDownloader = metadataDownloader;
    }

    @Override
    public Mono<NumberOfPagesResponse> getTotalPagesFromMetadataSet(String setId, List<String> pathTokens) {
        log.info("Starting getTotalPagesFromMetadataSet with setId: {} and pathTokens: {}", setId, pathTokens);
        return getMetadataSet(setId)
                .map(f24MetadataSet -> extractFileKeysToDownload(f24MetadataSet, pathTokens))
                .doOnNext(fileKeys -> log.debug("Extracted {} fileKeys to download", fileKeys.size()))
                .flatMap(this::executeMultiDownloadAndCalculatePages)
                .map(NumberOfPagesResponse::new);
    }

    private Mono<F24MetadataSet> getMetadataSet(String setId) {
        return f24MetadataSetDao.getItem(setId)
                .switchIfEmpty(Mono.error(
                        new PnNotFoundException(
                                "MetadataSet not found",
                                String.format(PnF24ExceptionCodes.ERROR_MESSAGE_F24_METADATA_SET_NOT_FOUND, setId),
                                PnF24ExceptionCodes.ERROR_CODE_F24_METADATA_NOT_FOUND
                        )
                ));
    }

    private List<String> extractFileKeysToDownload(F24MetadataSet f24MetadataSet, List<String> pathTokens) {
        String pathTokensInString = Utility.convertPathTokensList(pathTokens);
        return f24MetadataSet.getFileKeys().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(pathTokensInString))
                .map(entry -> entry.getValue().getFileKey())
                .toList();
    }

    private Mono<Integer> executeMultiDownloadAndCalculatePages(List<String> fileKeys) {
        return Flux.fromIterable(fileKeys)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(fileKey -> metadataDownloader.downloadMetadata(fileKey)
                                .map(this::calculateTotalPages)
                                .doOnNext(totalPage -> log.debug("Calculated {} pages on metadata with fileKey: {}", totalPage, fileKey))
                )
                .reduce(Integer::sum);
    }

    private int calculateTotalPages(F24Metadata f24Metadata) {
        MetadataInspector inspector = MetadataInspectorFactory.getInspector(Utility.getF24TypeFromMetadata(f24Metadata));
        return inspector.getExpectedNumberOfPages(f24Metadata);
    }
}
