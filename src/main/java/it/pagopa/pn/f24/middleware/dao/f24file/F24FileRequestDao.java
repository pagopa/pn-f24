package it.pagopa.pn.f24.middleware.dao.f24file;

import it.pagopa.pn.f24.dto.F24Request;
import reactor.core.publisher.Mono;

public interface F24FileRequestDao {
    Mono<F24Request> getItem(String requestId);

    Mono<F24Request> getItem(String requestId, boolean isConsistentRead);

    Mono<Void> putItemIfAbsent(F24Request f24Request);

    Mono<F24Request> updateItem(F24Request f24Request);
}
