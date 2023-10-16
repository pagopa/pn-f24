package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreparePdfEventBuilderTest {
    @Test
    void testBuildPreparePdfEvent() {
        GenericEventHeader header = PreparePdfEventBuilder.buildPreparePdfEvent("requestId.123").getHeader();
        assertEquals(EventPublisher.F24.name(), header.getPublisher());
        assertEquals("PREPARE_PDF", header.getEventType());
        assertTrue(header.getEventId().startsWith("prepare_pdf_"));
        assertTrue(header.getEventId().endsWith("requestId_123"));
    }
}