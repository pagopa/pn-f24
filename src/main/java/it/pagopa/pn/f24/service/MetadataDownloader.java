package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import reactor.core.publisher.Mono;

public interface MetadataDownloader {
    Mono<F24Metadata> downloadMetadata(String fileKey);
}
