package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import reactor.core.publisher.Mono;

import java.util.List;

public class F24FileRequestDaoMock implements F24FileRequestDao {
    @Override
    public Mono<F24Request> getItem(String requestId) {
        return null;
    }

    @Override
    public Mono<F24Request> getItem(String requestId, boolean isConsistentRead) {
        return null;
    }

    @Override
    public Mono<Void> putItemIfAbsent(F24Request f24Request) {
        return null;
    }

    @Override
    public Mono<F24Request> updateItem(F24Request f24Request) {
        return null;
    }

    @Override
    public Mono<F24Request> setRequestStatusDone(F24Request f24Request) {
        return null;
    }

    @Override
    public Mono<Void> updateRequestAndRelatedFiles(PreparePdfLists preparePdfLists) {
        return null;
    }

    @Override
    public Mono<Void> updateTransactionalFileAndRequests(List<F24Request> f24Requests, F24File f24File) {
        return null;
    }
}
