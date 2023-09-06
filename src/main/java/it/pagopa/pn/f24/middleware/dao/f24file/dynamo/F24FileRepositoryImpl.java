package it.pagopa.pn.f24.middleware.dao.f24file.dynamo;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.middleware.dao.f24file.F24FileDao;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

import static it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileEntity.SK_PK_GSI;

@Component
@Slf4j
public class F24FileRepositoryImpl implements F24FileDao {
    private static final String DEFAULT_SEPARATOR = "#";
    private static final String NO_FEE = "NO_FEE";
    private final DynamoDbAsyncTable<F24FileEntity> table;

    public F24FileRepositoryImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, F24Config f24Config) {
        this.table = dynamoDbEnhancedClient.table(f24Config.getFileDao().getTableName(), TableSchema.fromBean(F24FileEntity.class));
    }

    @Override
    public Mono<F24File> getItem(String setId, String cxId, String created) {
        return getItem(setId, cxId, created, false);
    }

    @Override
    public Mono<F24File> getItem(String setId, String cxId, String created, boolean isConsistentRead) {
        String partitionKey = cxId+DEFAULT_SEPARATOR+setId;
        Key pk = Key.builder().partitionValue(partitionKey).sortValue(created).build();

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest.builder()
                .key(pk)
                .consistentRead(isConsistentRead)
                .build();

        return Mono.fromFuture(table.getItem(getItemEnhancedRequest)).map(F24FileMapper::entityToDto);
    }

    @Override
    public Mono<F24File> getItemByPathTokens(String setId, String cxId, List<String> pathTokens, String cost) {
        String partitionKey = buildPathTokensPartitionKey(cost, pathTokens);
        String sortKey = cxId + DEFAULT_SEPARATOR + setId;
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(partitionKey)
                .sortValue(sortKey)
                .build());

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(false)
                .build();
        return Mono.from(table.index(SK_PK_GSI).query(queryEnhancedRequest))
                .map(f24FileEntityPage -> F24FileMapper.entityToDto(f24FileEntityPage.items().get(0)));
    }

    private String buildPathTokensPartitionKey(String cost, List<String> pathTokens) {
        String partitionKey = "";
        if(cost != null && !cost.equalsIgnoreCase("")) {
            partitionKey = cost;
        } else {
            partitionKey = NO_FEE;
        }


        String pathTokensInString = String.join(DEFAULT_SEPARATOR, pathTokens);

        partitionKey += DEFAULT_SEPARATOR + pathTokensInString;

        return partitionKey;
    }


}
