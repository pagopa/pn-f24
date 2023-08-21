package it.pagopa.pn.f24.middleware.eventbus.abstraction;

import it.pagopa.pn.api.dto.events.GenericEventBridgeEvent;

public interface PnF24GenericEvent<T> extends GenericEventBridgeEvent<T> {
    String getCxId();
}
