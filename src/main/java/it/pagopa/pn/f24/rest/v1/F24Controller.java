package it.pagopa.pn.f24.rest.v1;

import it.pagopa.pn.f24.generated.openapi.server.v1.api.F24ControllerApi;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.f24.service.F24Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Slf4j
public class F24Controller implements F24ControllerApi {

    private final F24Service f24Service;

    public F24Controller(F24Service f24Service) {
        this.f24Service = f24Service;
    }

    @Override
    public Mono<ResponseEntity<RequestAccepted>> saveMetadata(String xPagopaF24CxId, String setId, Mono<SaveF24Request> saveF24Request,  final ServerWebExchange exchange) {
        return f24Service.saveMetadata(xPagopaF24CxId, setId, saveF24Request)
                .map(requestAccepted -> ResponseEntity.status(HttpStatus.ACCEPTED).body(requestAccepted));
    }

    @Override
    public Mono<ResponseEntity<RequestAccepted>> validateMetadata(String xPagopaF24CxId, String setId, Mono<ValidateF24Request> validateF24Request, final ServerWebExchange exchange) {
        return f24Service.validate(xPagopaF24CxId, setId)
                .map(requestAccepted -> ResponseEntity.status(HttpStatus.ACCEPTED).body(requestAccepted));
    }

    @Override
    public Mono<ResponseEntity<F24Response>> generatePDF(String xPagopaF24CxId, String setId, List<String> pathTokens, Integer cost,  final ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<RequestAccepted>> preparePDF(String xPagopaF24CxId, String requestId, Mono<PrepareF24Request> prepareF24Request, final ServerWebExchange exchange) {
        return null;
    }
}
