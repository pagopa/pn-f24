package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.NumberOfPagesResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface F24ParserService {
    Mono<NumberOfPagesResponse> getTotalPagesFromMetadataSet(String setId, List<String> pathTokens);
}
