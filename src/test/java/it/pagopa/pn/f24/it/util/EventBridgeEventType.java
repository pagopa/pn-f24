package it.pagopa.pn.f24.it.util;

public enum EventBridgeEventType {
    METADATA_VALIDATION("metadataValidationEnd"),
    PDF_READY("pdfSetReady");

    private final String eventFieldInDetailObject;

    EventBridgeEventType(String eventFieldInDetailObject) {
        this.eventFieldInDetailObject = eventFieldInDetailObject;
    }

    public String getEventFieldInDetailObject() {
        return eventFieldInDetailObject;
    }
}
