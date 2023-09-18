package it.pagopa.pn.f24.middleware.dao.f24file;

import it.pagopa.pn.f24.dto.F24File;
import reactor.core.publisher.Mono;

import java.util.List;

public interface F24FileDao {
    Mono<F24File> getItem(String setId, String cxId, String created);

    Mono<F24File> getItem(String setId, String cxId, String created, boolean isConsistentRead);

    Mono<F24File> getItemByPathTokens(String setId, String cxId, List<String> pathTokens, String cost);

    Mono<F24File> updateItem(F24File f24File);

    Mono<F24File> putItem(F24File f24File);
}
