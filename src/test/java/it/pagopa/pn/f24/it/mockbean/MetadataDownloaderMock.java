package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.service.MetadataDownloader;
import reactor.core.publisher.Mono;

public class MetadataDownloaderMock implements MetadataDownloader {
    @Override
    public Mono<F24Metadata> downloadMetadata(String fileKey) {
        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();
        f24Metadata.setF24Standard(f24Standard);
        return Mono.just(f24Metadata);
    }
}
