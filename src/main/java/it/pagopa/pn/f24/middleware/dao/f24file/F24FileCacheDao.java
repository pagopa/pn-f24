package it.pagopa.pn.f24.middleware.dao.f24file;

import it.pagopa.pn.f24.dto.F24File;
import reactor.core.publisher.Mono;

public interface F24FileCacheDao {
    Mono<F24File> getItem(String pk);
    Mono<F24File> getItem(String pk, boolean isConsistentRead);
    Mono<F24File> getItem(String setId, Integer cost, String pathTokens);
    Mono<F24File> getItem(String setId, Integer cost, String pathTokens, boolean isConsistentRead);
    Mono<F24File> updateItem(F24File f24File);
    Mono<F24File> setFileKey(F24File f24File, String fileKey);
    Mono<F24File> setStatusDone(F24File f24File);
    Mono<F24File> putItemIfAbsent(F24File f24File);
    Mono<F24File> getItemByFileKey(String fileKey);
}
