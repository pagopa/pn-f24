package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.dto.*;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class F24MetadataSetDaoMock implements F24MetadataSetDao, ClearableMock {

    private List<F24MetadataSet> f24MetadataSetList = new CopyOnWriteArrayList<>();

    public void clear() {
        f24MetadataSetList = new CopyOnWriteArrayList<>();
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId) {
        return getItem(setId, false);
    }

    @Override
    public Mono<F24MetadataSet> getItem(String setId, boolean isConsistentRead) {
        Optional<F24MetadataSet> item =
                f24MetadataSetList.stream()
                        .filter(f24MetadataSet -> Objects.equals(f24MetadataSet.getSetId(), setId))
                        .findFirst();
        return item.map(Mono::just).orElseGet(Mono::empty);
    }

    @Override
    public Mono<Void> putItemIfAbsent(F24MetadataSet f24MetadataSet) {
        f24MetadataSetList.add(f24MetadataSet);
        return Mono.empty();
    }

    @Override
    public Mono<F24MetadataSet> updateItem(F24MetadataSet f24MetadataSet) {
        for (int i = 0; i < f24MetadataSetList.size(); i++) {
            if (Objects.equals(f24MetadataSetList.get(i).getSetId(), f24MetadataSet.getSetId())) {
                f24MetadataSetList.set(i, f24MetadataSet);
                return Mono.just(f24MetadataSet);
            }
        }
        return null;
    }

    @Override
    public Mono<F24MetadataSet> setF24MetadataSetStatusValidationEnded(F24MetadataSet f24MetadataSet) {
        for (int i = 0; i < f24MetadataSetList.size(); i++) {
            if (Objects.equals(f24MetadataSetList.get(i).getSetId(), f24MetadataSet.getSetId())) {
           /*     if (f24MetadataSetList.get(i).getStatus() != F24MetadataStatus.TO_VALIDATE) {
                    throw new PnDbConflictException("Invalid status");
                }*/
                f24MetadataSetList.set(i, f24MetadataSet);
                return Mono.just(f24MetadataSet);
            }
        }
        return null;
    }
}
