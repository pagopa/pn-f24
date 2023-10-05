package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditLogServiceImplTest {

    AuditLogServiceImpl auditLogService;

    @BeforeEach
    void beforeEach(){
        auditLogService = new AuditLogServiceImpl();
    }


    @Test
    void buildAuditLogEvent() {
        PnAuditLogEvent event = auditLogService.buildAuditLogEvent("iun1", PnAuditLogEventType.AUD_DD_SEND,"messaggio par1={} par2={}", "parametro1", 2);
        assertNotNull(event);
        event.generateSuccess().log();
    }

}