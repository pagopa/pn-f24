package it.pagopa.pn.f24.middleware.dao.f24metadatadao;

import it.pagopa.pn.f24.dto.F24Metadata;
import reactor.core.publisher.Mono;

public interface F24MetadataDao {
    Mono<F24Metadata> getItem(String setId, String cxId);

    Mono<F24Metadata> getItem(String setId, String cxId, boolean isConsistentRead);

    Mono<F24Metadata> getItemByFileKey(String fileKey);

    Mono<F24Metadata> getItemByPathToken(String setId, String sk);

    Mono<Void> putItem(F24Metadata f24Metadata);

    Mono<F24Metadata> updateItem(F24Metadata f24Metadata);
}
