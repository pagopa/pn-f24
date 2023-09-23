package it.pagopa.pn.f24.middleware.queue.producer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import org.junit.jupiter.api.Test;

class ValidateMetadataSetEventBuilderTest {
    /**
     * Method under test: {@link InternalMetadataEventBuilder#buildValidateMetadataEvent(String, String)}
     */
    @Test
    void testBuildValidateMetadataEvent() {
        GenericEventHeader header = InternalMetadataEventBuilder.buildValidateMetadataEvent("42", "42").getHeader();
        assertEquals("pn-f24", header.getPublisher());
        assertEquals("VALIDATE_METADATA", header.getEventType());
        assertEquals("42_validate_f24_metadata_42", header.getEventId());
    }
}

