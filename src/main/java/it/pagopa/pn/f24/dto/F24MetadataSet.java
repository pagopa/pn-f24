package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class F24MetadataSet {
    private String pk;
    private F24MetadataStatus status;
    private Map<String, F24MetadataRef> fileKeys;
    private String sha256;
    private Boolean haveToSendValidationEvent;
    private Boolean validationEventSent;
    private Instant created;
    private Instant updated;
}
