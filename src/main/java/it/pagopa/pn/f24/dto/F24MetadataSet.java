package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class F24MetadataSet {
    private String setId;
    private String creatorCxId;
    private String validatorCxId;
    private F24MetadataStatus status;
    private Map<String, F24MetadataRef> fileKeys;
    private String sha256;
    private Boolean haveToSendValidationEvent;
    private Boolean validationEventSent;
    private List<F24MetadataValidationIssue> validationResult;
    private Instant created;
    private Instant updated;
    private Long ttl;
}
