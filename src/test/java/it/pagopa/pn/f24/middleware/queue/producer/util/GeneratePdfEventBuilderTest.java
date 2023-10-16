package it.pagopa.pn.f24.middleware.queue.producer.util;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.PreparePdfLists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratePdfEventBuilderTest {
    @Test
    void testBuildGeneratePdfEvent() {
        F24File f24File = new F24File();
        f24File.setPk("CACHE#setId#200#0_0");
        PreparePdfLists.F24FileToCreate f24FileToCreate = new PreparePdfLists.F24FileToCreate(f24File, "metadataFileKey");
        GenericEventHeader header = GeneratePdfEventBuilder.buildGeneratePdfEvent(f24FileToCreate).getHeader();
        assertEquals(EventPublisher.F24.name(), header.getPublisher());
        assertEquals("GENERATE_PDF", header.getEventType());
        assertTrue(header.getEventId().startsWith("generate_pdf_"));
        assertTrue(header.getEventId().endsWith("CACHE_setId_200_0_0"));
    }
}