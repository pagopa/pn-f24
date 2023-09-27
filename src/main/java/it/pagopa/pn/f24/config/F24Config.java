package it.pagopa.pn.f24.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "pn.f24")
@Data
@Import(SharedAutoConfiguration.class)
public class F24Config {
    private String safeStorageBaseUrl;
    private Integer secIntervalForSafeStoragePolling;
    private Integer safeStorageExecutionLimitMin;
    private Integer retryAfterWhenErrorSafeStorage;
    private Integer secToPollingTimeout;
    private Integer retentionForF24FilesInDays;
    private String safeStorageCxId;
    private String safeStorageMetadataDocType;
    private String safeStorageF24DocType;
    private String metadataSetTableName;
    private String fileTableName;
    private String internalQueueName;
    private String safeStorageQueueName;
    private EventBus eventBus;
    private List<String> corsAllowedDomains;
    private String paperChannelCxId;
    private String deliveryPushCxId;

    @Data
    public static class EventBus {
        private String name;
        private String source;
        private String outcomeEventDetailType;
    }
}
