package it.pagopa.pn.f24.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class F24File {
    private String pk;
    private String created;
    private String sk;
    private String requestId;
    private F24FileStatus status;
    private String fileKey;
    private Long ttl;
    private LocalDateTime updated;

}
