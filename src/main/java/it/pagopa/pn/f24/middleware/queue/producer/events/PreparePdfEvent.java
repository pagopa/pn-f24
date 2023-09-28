package it.pagopa.pn.f24.middleware.queue.producer.events;

import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class PreparePdfEvent implements GenericEvent<GenericEventHeader, PreparePdfEvent.Payload> {
    private GenericEventHeader header;

    private Payload payload;
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    @ToString
    @EqualsAndHashCode
    public static class Payload {
        private String requestId;
    }
}
