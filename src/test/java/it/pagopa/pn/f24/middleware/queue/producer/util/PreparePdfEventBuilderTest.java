package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreparePdfEventBuilderTest {
    @Test
    void testBuildPreparePdfEvent() {
        GenericEventHeader header = PreparePdfEventBuilder.buildPreparePdfEvent("requestId").getHeader();
        assertEquals(EventPublisher.F24.name(), header.getPublisher());
        assertEquals("PREPARE_PDF", header.getEventType());
        assertEquals("prepare_pdf_request_requestId", header.getEventId());
    }
}