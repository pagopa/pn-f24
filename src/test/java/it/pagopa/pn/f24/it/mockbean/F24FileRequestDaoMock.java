package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileRequestDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class F24FileRequestDaoMock implements F24FileRequestDao, ClearableMock {
    private List<F24Request> f24Requests = new CopyOnWriteArrayList<>();

    private F24FileCacheDaoMock f24FileCacheDaoMock;

    public F24FileRequestDaoMock( F24FileCacheDaoMock f24FileCacheDaoMock) {
        this.f24FileCacheDaoMock = f24FileCacheDaoMock;
    }

    @Override
    public Mono<F24Request> getItem(String requestId) {
        return getItem(requestId, false);
    }

    @Override
    public Mono<F24Request> getItem(String requestId, boolean isConsistentRead) {
        String pk = new F24FileRequestEntity(requestId).getPk();
        Optional<F24Request> item = f24Requests.stream().filter(f24Request -> f24Request.getPk().equalsIgnoreCase(pk)).findFirst();
        return item.map(Mono::just).orElseGet(Mono::empty);
    }

    @Override
    public Mono<Void> putItemIfAbsent(F24Request f24RequestToInsert) {
        String pk = new F24FileRequestEntity(f24RequestToInsert.getRequestId()).getPk();

        f24Requests.stream()
                .filter(f24Request -> Objects.equals(f24Request.getPk(), pk))
                .findFirst()
                .ifPresent(f24Request -> {
                    throw new PnDbConflictException("Item already present");
                });

        f24RequestToInsert.setPk(pk);
        f24Requests.add(f24RequestToInsert);
        return Mono.empty();
    }

    @Override
    public Mono<F24Request> updateItem(F24Request f24Request) {
        for (int i = 0; i < f24Requests.size(); i++) {
            if (Objects.equals(f24Requests.get(i).getPk(), f24Request.getPk())) {
                f24Requests.set(i, f24Request);
                return Mono.just(f24Request);
            }
        }
        return null;
    }

    @Override
    public Mono<F24Request> setRequestStatusDone(F24Request f24Request) {
        return updateItem(f24Request);
    }

    @Override
    public Mono<Void> updateRequestAndRelatedFiles(PreparePdfLists preparePdfLists) {
        updateItem(preparePdfLists.getF24Request());
        preparePdfLists.getFilesToCreate().forEach(fileToCreate -> f24FileCacheDaoMock.putItemIfAbsent(fileToCreate.getFile()));
        preparePdfLists.getFilesReady().forEach(f24FileCacheDaoMock::updateItem);
        preparePdfLists.getFilesNotReady().forEach(f24FileCacheDaoMock::updateItem);
        return Mono.empty();
    }

    @Override
    public Mono<Void> updateTransactionalFileAndRequests(List<F24Request> f24Requests, F24File f24File) {
        f24Requests.forEach(this::updateItem);
        f24FileCacheDaoMock.updateItem(f24File);
        return Mono.empty();
    }

    @Override
    public void clear() {
        this.f24Requests = new CopyOnWriteArrayList<>();
    }
}
