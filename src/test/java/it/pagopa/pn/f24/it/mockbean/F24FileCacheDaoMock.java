package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.exception.PnDbConflictException;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileCacheDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Setter
public class F24FileCacheDaoMock implements F24FileCacheDao {

    private List<F24File> f24FileList = new ArrayList<>();

    public void clear() {
        f24FileList = new ArrayList<>();
    }

    @Override
    public Mono<F24File> getItem(String pk) {
        return getItem(pk, false);
    }

    @Override
    public Mono<F24File> getItem(String pk, boolean isConsistentRead) {
        Optional<F24File> item =
                f24FileList.stream()
                        .filter(f24File -> Objects.equals(f24File.getPk(), pk))
                        .findFirst();
        return item.map(Mono::just).orElseGet(Mono::empty);
    }

    @Override
    public Mono<F24File> getItem(String setId, Integer cost, String pathTokens) {
        String pk = new F24FileCacheEntity(setId, cost, pathTokens).getPk();
        return getItem(pk, false);
    }

    @Override
    public Mono<F24File> getItem(String setId, Integer cost, String pathTokens, boolean isConsistentRead) {
        String pk = new F24FileCacheEntity(setId, cost, pathTokens).getPk();
        return getItem(pk, false);
    }

    @Override
    public Mono<F24File> updateItem(F24File f24File) {
        for (int i = 0; i < f24FileList.size(); i++) {
            if (Objects.equals(f24FileList.get(i).getPk(), f24File.getPk())) {
                f24FileList.set(i, f24File);
                return Mono.just(f24File);
            }
        }
        return null;
    }

    @Override
    public Mono<F24File> setFileKey(F24File f24File, String fileKey) {
        for (int i = 0; i < f24FileList.size(); i++) {
            if (Objects.equals(f24FileList.get(i).getPk(), f24File.getPk())) {
                f24File.setFileKey(fileKey);
                f24FileList.set(i, f24File);
                return Mono.just(f24File);
            }
        }
        return null;
    }

    @Override
    public Mono<F24File> setStatusDone(F24File f24File) {
        for (int i = 0; i < f24FileList.size(); i++) {
            if (Objects.equals(f24FileList.get(i).getPk(), f24File.getPk())) {
                f24File.setStatus(F24FileStatus.DONE);
                f24FileList.set(i, f24File);
                return Mono.just(f24File);
            }
        }
        return null;
    }

    @Override
    public Mono<F24File> putItemIfAbsent(F24File f24File) {
        String pk = new F24FileCacheEntity(f24File.getSetId(), f24File.getCost(), f24File.getPathTokens()).getPk();

        f24FileList.stream()
                .filter(f24File1 -> Objects.equals(f24File1.getPk(), pk))
                .findFirst()
                .ifPresent(f24File1 -> {
                    throw new PnDbConflictException("Item already present");
                });

        f24File.setPk(pk);
        f24FileList.add(f24File);
        return Mono.just(f24File);
    }

    @Override
    public Mono<F24File> getItemByFileKey(String fileKey) {
        Optional<F24File> item =
                f24FileList.stream()
                        .filter(f24File -> Objects.equals(f24File.getFileKey(), fileKey))
                        .findFirst();
        return item.map(Mono::just).orElseGet(Mono::empty);
    }
}
