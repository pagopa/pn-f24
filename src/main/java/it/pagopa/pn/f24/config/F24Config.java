package it.pagopa.pn.f24.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationProperties( prefix = "pn.f24")
@Data
@Import(SharedAutoConfiguration.class)
public class F24Config {
    private String safeStorageBaseUrl;
    private String msToWaitForSafeStorage;
    private String safeStorageCxId;
    private String safeStorageMetadataDocType;
    private String safeStorageF24DocType;
    private F24MetadataDao metadataDao;
    private F24FileDao fileDao;
    private Queue internalQueue;
    private Queue safeStorageQueue;
    private EventBus eventBus;

    @Data
    public static class Queue {
        private String name;
    }

    @Data
    public static class EventBus {
        private String name;
        private String source;
        private EventBusEvent outcomeEvent;
    }

    @Data
    public static class EventBusEvent {
        private String detailType;
    }

    @Data
    public static class F24MetadataDao {
        private String tableName;
    }

    @Data
    public static class F24FileDao {
        private String tableName;
    }
}
