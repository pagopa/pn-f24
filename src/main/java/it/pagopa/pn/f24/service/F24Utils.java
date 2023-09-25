package it.pagopa.pn.f24.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.exception.PnF24ExceptionCodes;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

public class F24Utils {

    public static F24Metadata validateF24Metadata(String json) {

        //todo: check if f24Metadata is valid(Emanuele)
        return jsonStringToObject(json);
    }

    private static F24Metadata jsonStringToObject(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, F24Metadata.class);

        } catch (JsonProcessingException e) {
            throw new PnBadRequestException("error while parsing json", "error while parsing json", PnF24ExceptionCodes.ERROR_CODE_F24_READ_FILE_ERROR);
        }
    }
}
