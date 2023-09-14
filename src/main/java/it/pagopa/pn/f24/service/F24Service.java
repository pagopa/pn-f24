package it.pagopa.pn.f24.service;

import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SaveF24Request;
import reactor.core.publisher.Mono;

import java.util.List;

public interface F24Service {
    Mono<RequestAccepted> validate(String cxId, String setId);

    Mono<RequestAccepted> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> saveF24Request);

    Mono<F24Response> generatePDF(String xPagopaF24CxId, String setId, List<String> pathTokens, Integer cost);
}
