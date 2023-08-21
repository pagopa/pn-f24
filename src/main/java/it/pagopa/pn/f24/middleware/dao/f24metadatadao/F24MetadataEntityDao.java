package it.pagopa.pn.f24.middleware.dao.f24metadatadao;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataEntity;

public interface F24MetadataEntityDao extends KeyValueStore<Key, F24MetadataEntity> {

}
