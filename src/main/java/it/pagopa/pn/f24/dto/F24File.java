package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class F24File {
    private String pk;
    private String created;
    private String sk;
    private String requestId;
    private String status;
    private String fileKey;
    private Long ttl;
    private LocalDateTime updated;

    //TODO sha256 ?
    //private String sha256;
}
