package it.pagopa.pn.f24.middleware.eventbus;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.eventbridge.AmazonEventBridgeAsync;
import com.amazonaws.services.eventbridge.model.PutEventsRequest;
import com.amazonaws.services.eventbridge.model.PutEventsRequestEntry;
import com.amazonaws.services.eventbridge.model.PutEventsResult;
import it.pagopa.pn.api.dto.events.GenericEventBridgeEvent;
import it.pagopa.pn.f24.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public abstract class AbstractEventBridgeProducer<T extends GenericEventBridgeEvent> implements EventBridgeProducer<T> {

    private final AmazonEventBridgeAsync amazonEventBridge;
    private final String eventBusName;
    private final String eventBusDetailType;
    private final String eventBusSource;

    protected AbstractEventBridgeProducer(AmazonEventBridgeAsync amazonEventBridge, String eventBusSource, String detailType, String name) {
        this.amazonEventBridge = amazonEventBridge;
        this.eventBusSource = eventBusSource;
        this.eventBusName = name;
        this.eventBusDetailType = detailType;
    }

    private PutEventsRequest putEventsRequestBuilder(T event) {
        PutEventsRequest putEventsRequest = new PutEventsRequest();
        List<PutEventsRequestEntry> entries = new ArrayList<>();
        PutEventsRequestEntry entryObj = new PutEventsRequestEntry();
        entryObj.setDetail(Utility.objectToJsonString(event.getDetail()));
        entryObj.setDetailType(eventBusDetailType);
        entryObj.setEventBusName(eventBusName);
        entryObj.setSource(eventBusSource);
        entries.add(entryObj);
        putEventsRequest.setEntries(entries);
        log.debug("PutEventsRequest: {}", putEventsRequest);
        return putEventsRequest;
    }

    @Override
    public void sendEvent(T event) {
        amazonEventBridge.putEventsAsync(putEventsRequestBuilder(event),
                new AsyncHandler<>() {
                    @Override
                    public void onError(Exception e) {
                        log.error("Send event failed", e);
                    }

                    @Override
                    public void onSuccess(PutEventsRequest request, PutEventsResult putEventsResult) {
                        log.info("Event sent successfully");
                        log.debug("Sent event result: {}", putEventsResult.getEntries());
                    }
                });
    }
}