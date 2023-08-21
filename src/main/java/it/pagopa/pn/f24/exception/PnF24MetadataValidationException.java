package it.pagopa.pn.f24.exception;

import it.pagopa.pn.commons.exceptions.PnValidationException;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;

import java.util.List;

public class PnF24MetadataValidationException extends PnValidationException {

        public PnF24MetadataValidationException(String code, String detail, String element, Throwable ex) {
            super( detail ,
                    List.of(
                            ProblemError.builder()
                                    .code(code)
                                    .detail(detail)
                                    .element(element)
                                    .build()
                    ),
                    ex
            );
        }
}
