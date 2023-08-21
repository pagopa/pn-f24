package it.pagopa.pn.f24.middleware.queue.producer;

import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.api.dto.events.GenericEventHeader;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Item;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class InternalMetadataEvent implements GenericEvent<GenericEventHeader, InternalMetadataEvent.Payload> {
    private GenericEventHeader header;

    private Payload payload;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    @ToString
    @EqualsAndHashCode
    public static class Payload {
        private String setId;
        private String cxId;
        private List<F24Item> f24Item;
    }
}
