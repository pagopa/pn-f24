package it.pagopa.pn.f24.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.exception.PnF24RuntimeException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.JsonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class JsonServiceImpl implements JsonService {
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public F24Metadata parseMetadataFile(byte[] json) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            return objectMapper.readValue(json, F24Metadata.class);
        } catch (IOException e) {
            String message = e.getMessage();

            if(e instanceof UnrecognizedPropertyException upEx) {
                message = upEx.getOriginalMessage();
            }
            throw new PnF24RuntimeException(message, "Metadata parsing error", "JSON_PARSING");
        }
    }

    public <T> String stringifyObject(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new PnInternalException("Couldn't convert object in JSON String", "JSON_STRINGIFY");
        }
    }

    public <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

}
