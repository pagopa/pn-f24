package it.pagopa.pn.f24.config.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.eventbridge.AmazonEventBridgeAsync;
import com.amazonaws.services.eventbridge.AmazonEventBridgeAsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@Slf4j
public class AwsServicesClientsConfig {

    private final AwsConfigs configs;

    public AwsServicesClientsConfig(AwsConfigs configs) {
        this.configs = configs;
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient( dynamoDbAsyncClient() )
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return configureBuilder( SqsClient.builder() );
    }

    @Bean
    public AmazonEventBridgeAsync amazonEventBridgeAsync() {
        return AmazonEventBridgeAsyncClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                //.withRegion(configs.getRegionCode())
                .build();
    }

    private <C> C configureBuilder(AwsClientBuilder<?, C> builder) {
        if( configs != null ) {

            String profileName = configs.getProfileName();
            if( StringUtils.isNotBlank( profileName ) ) {
                builder.credentialsProvider( ProfileCredentialsProvider.create( profileName ));
            }

            String regionCode = configs.getRegionCode();
            if( StringUtils.isNotBlank( regionCode )) {
                builder.region( Region.of( regionCode ));
            }

            String endpointUrl = configs.getEndpointUrl();
            if( StringUtils.isNotBlank( endpointUrl )) {
                builder.endpointOverride( URI.create( endpointUrl ));
            }

        }

        return builder.build();
    }

    private DynamoDbAsyncClient dynamoDbAsyncClient() {
        return this.configureBuilder( DynamoDbAsyncClient.builder() );
    }

}
