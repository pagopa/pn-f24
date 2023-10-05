package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.f24.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {
    @Override
    public PnAuditLogEvent buildAuditLogEvent(String setId, PnAuditLogEventType pnAuditLogEventType, String message, Object ... arguments) {
        String logMessage = MessageFormatter.arrayFormat(message, arguments).getMessage();
        PnAuditLogBuilder auditLogBuilder = new PnAuditLogBuilder();
        PnAuditLogEvent logEvent;
        logEvent = auditLogBuilder.before(pnAuditLogEventType, "{} - setId={}", logMessage, setId)
                .iun(setId)
                .build();
        logEvent.log();
        return logEvent;
    }

}
