package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import reactor.core.publisher.Mono;

public interface F24Service {
    Mono<RequestAccepted> validate(String cxId, String setId);

    Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> saveF24Request);

    Mono<byte[]> generatePDF(String iun, String recipientIndex, String attachmentIndex);
}
