package it.pagopa.pn.f24.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class F24Request {
    private String pk;
    private String cxId;
    private String requestId;
    private Map<String, FileRef> files;
    private String setId;
    private String pathTokens;
    private Integer cost;
    private F24RequestStatus status;
    private Integer recordVersion;
    private Instant created;
    private Instant updated;
    private Long ttl;

    @Data
    @AllArgsConstructor
    public static class FileRef {
        private String fileKey;
        private F24FileStatus status;
    }
}
