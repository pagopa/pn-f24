package it.pagopa.pn.f24.middleware.dao.f24file;

import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import reactor.core.publisher.Mono;

public interface F24FileRequestDao {
    Mono<F24Request> getItem(String pk);

    Mono<F24Request> getItem(String pk, boolean isConsistentRead);

    Mono<F24Request> getItem(String cxId, String requestId);

    Mono<F24Request> getItem(String cxId, String requestId, boolean isConsistentRead);

    Mono<Void> putItemIfAbsent(F24Request f24Request);

    Mono<F24Request> updateItem(F24Request f24Request);

    Mono<F24Request> setRequestStatusProcessing(F24Request f24Request);

    Mono<Void> updateRequestAndRelatedFiles(PreparePdfLists preparePdfLists);

    Mono<F24Request> updateRequestFile(F24Request f24Request);
}
