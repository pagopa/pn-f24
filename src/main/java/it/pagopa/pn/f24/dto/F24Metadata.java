package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class F24Metadata {
    private String setId;
    private String cxId;
    private F24MetadataStatus status;
    private String fileKey;
    private String sha256;
    private Boolean haveToSendValidationEvent;
    private Boolean validationEventSent;
    private Instant created;
    private Instant updated;
}
