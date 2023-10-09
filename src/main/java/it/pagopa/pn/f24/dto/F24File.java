package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
public class F24File {
    private String pk;
    private String setId;
    private Integer cost;
    private String pathTokens;
    private F24FileStatus status;
    private String fileKey;
    private List<String> requestIds;
    private Long ttl;
    private Instant created;
    private Instant updated;
}
