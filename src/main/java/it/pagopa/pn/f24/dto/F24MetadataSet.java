package it.pagopa.pn.f24.dto;

import it.pagopa.pn.f24.middleware.dao.util.EntityKeysUtils;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class F24MetadataSet {
    private String pk;
    private F24MetadataStatus status;
    private Map<String, F24MetadataRef> fileKeys;
    private String sha256;
    private Boolean haveToSendValidationEvent;
    private Boolean validationEventSent;
    private List<F24MetadataValidationIssue> validationResult;
    private Instant created;
    private Instant updated;
    private Long ttl;

    public String getSetId() {
        return EntityKeysUtils.F24MetadataSet.getSetId(this.pk);
    }

    public String getCxId() {
        return EntityKeysUtils.F24MetadataSet.getCxId(this.pk);
    }
}
