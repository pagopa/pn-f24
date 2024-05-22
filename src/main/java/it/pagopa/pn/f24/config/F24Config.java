package it.pagopa.pn.f24.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "pn.f24")
@Data
@Import(SharedAutoConfiguration.class)
public class F24Config {
    private String safeStorageBaseUrl;
    private Integer pollingIntervalSec;
    private Integer safeStorageExecutionLimitMin;
    private Integer defaultRetryAfterMilliSec;
    private Integer pollingTimeoutSec;
    private String safeStorageCxId;
    private String safeStorageF24DocType;
    private String metadataSetTableName;
    private String fileTableName;
    private String internalQueueName;
    private String safeStorageQueueName;
    private EventBus eventBus;
    private Integer retentionForF24RequestsInDays;
    private Integer retentionForF24FilesInDays;
    private Integer metadataSetTtlInDaysUntilValidation;
    @Data
    public static class EventBus {
        private String name;
        private String source;
        private String outcomeEventDetailType;
    }
}
