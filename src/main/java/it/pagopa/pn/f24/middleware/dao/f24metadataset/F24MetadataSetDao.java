package it.pagopa.pn.f24.middleware.dao.f24metadataset;

import it.pagopa.pn.f24.dto.F24MetadataSet;
import reactor.core.publisher.Mono;

public interface F24MetadataSetDao {
    Mono<F24MetadataSet> getItem(String setId);
    Mono<F24MetadataSet> getItem(String setId, boolean isConsistentRead);
    Mono<Void> putItemIfAbsent(F24MetadataSet f24MetadataSet);
    Mono<F24MetadataSet> updateItem(F24MetadataSet f24MetadataSet);
    Mono<F24MetadataSet> setF24MetadataSetStatusValidationEnded(F24MetadataSet f24MetadataSet);
}
