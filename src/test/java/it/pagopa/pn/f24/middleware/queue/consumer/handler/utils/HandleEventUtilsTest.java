package it.pagopa.pn.f24.middleware.queue.consumer.handler.utils;

import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.api.dto.events.GenericEventHeader.*;
import static it.pagopa.pn.api.dto.events.GenericEventHeader.PN_EVENT_HEADER_PUBLISHER;
import static it.pagopa.pn.api.dto.events.StandardEventHeader.PN_EVENT_HEADER_IUN;

class HandleEventUtilsTest {
    @Test
    void mapGenericEventHeader() {

        GenericEventHeader actual = HandleEventUtils.mapGenericEventHeader(buildMessageHeaders());

        Assertions.assertEquals(buildGenericEventHeader(), actual);
    }

    @Test
    void mapGenericEventHeaderException() {

        PnInternalException pnInternalException = Assertions.assertThrows(PnInternalException.class, () -> {
            HandleEventUtils.mapGenericEventHeader(null);
        });

        String expectErrorMsg = "PN_F24_HANDLEEVENTFAILED";

        Assertions.assertEquals(expectErrorMsg, pnInternalException.getProblem().getErrors().get(0).getCode());
    }


    private MessageHeaders buildMessageHeaders() {
        Map<String, Object> map = new HashMap<>();
        map.put(PN_EVENT_HEADER_EVENT_ID, "001");
        map.put(PN_EVENT_HEADER_IUN, "002");
        map.put(PN_EVENT_HEADER_EVENT_TYPE, "003");
        map.put(PN_EVENT_HEADER_CREATED_AT, "2021-09-16T15:23:00.00Z");
        map.put(PN_EVENT_HEADER_PUBLISHER, "005");
        return new MessageHeaders(map);
    }

    private GenericEventHeader buildGenericEventHeader() {
        Instant instant = Instant.parse("2021-09-16T15:23:00.00Z");

        return GenericEventHeader.builder()
                .eventId("001")
                .eventType("003")
                .createdAt(instant)
                .publisher("005")
                .build();
    }
}