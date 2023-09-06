package it.pagopa.pn.f24.middleware.dao.f24metadataset;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;

public interface F24MetadataEntityDao extends KeyValueStore<Key, F24MetadataSetEntity> {

}
