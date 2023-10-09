package it.pagopa.pn.f24.service;

import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;

public interface AuditLogService {
    PnAuditLogEvent buildAuditLogEvent(String setId, PnAuditLogEventType pnAuditLogEventType, String message, Object ... arguments);
    void buildGeneratePdfAuditLogEvent(String setId, String pathTokensInString, Integer cost, String f24FileKey, String metadataFileKey);

}
