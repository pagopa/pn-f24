package it.pagopa.pn.f24.middleware.queue.producer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.api.dto.events.EventPublisher;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import org.junit.jupiter.api.Test;

class InternalMetadataEventBuilderTest {
    /**
     * Method under test: {@link InternalMetadataEventBuilder#buildValidateMetadataEvent(String)}
     */
    @Test
    void testBuildValidateMetadataEvent() {
        GenericEventHeader header = InternalMetadataEventBuilder.buildValidateMetadataEvent("42").getHeader();
        assertEquals(EventPublisher.F24.name(), header.getPublisher());
        assertEquals("VALIDATE_METADATA", header.getEventType());
        assertEquals("validate_f24_metadata_42", header.getEventId());
    }
}

