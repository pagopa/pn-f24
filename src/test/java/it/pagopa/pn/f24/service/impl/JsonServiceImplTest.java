package it.pagopa.pn.f24.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {JsonServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class JsonServiceImplTest {
    @Autowired
    private JsonServiceImpl jsonServiceImpl;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private Validator validator;


    @Test
    public void testParseMetadataFile() throws IOException {
        F24Metadata f24Metadata = new F24Metadata();
        when(objectMapper.readValue(Mockito.<byte[]>any(), Mockito.<Class<F24Metadata>>any())).thenReturn(f24Metadata);
        assertSame(f24Metadata, jsonServiceImpl.parseMetadataFile("AXAXAXAX".getBytes(StandardCharsets.UTF_8)));
        verify(objectMapper).readValue(Mockito.<byte[]>any(), Mockito.<Class<F24Metadata>>any());
    }

    @Test
    public void testParseMetadataFile2() throws IOException {
        when(objectMapper.readValue(Mockito.<byte[]>any(), Mockito.<Class<F24Metadata>>any()))
                .thenThrow(new IOException("foo"));
        byte[] jsonMetadata = "AXAXAXAX".getBytes(StandardCharsets.UTF_8);
        assertThrows(PnInternalException.class, () -> jsonServiceImpl.parseMetadataFile(jsonMetadata));
        verify(objectMapper).readValue(Mockito.<byte[]>any(), Mockito.<Class<F24Metadata>>any());
    }

    @Test
    public void testStringifyObject() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("42");
        assertEquals("42", jsonServiceImpl.stringifyObject("Object"));
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
    }

    @Test
    public void stringifyObjectError() throws JsonProcessingException {


        // Configura il comportamento del mock per generare una JsonProcessingException
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(JsonProcessingException.class);

        // Chiamata al metodo stringifyObject che dovrebbe generare una PnInternalException
        Object test = new Object();
        assertThrows(PnInternalException.class, () -> jsonServiceImpl.stringifyObject(test));

        // Verifica che objectMapper.writeValueAsString sia stato chiamato esattamente una volta
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    public void testValidate() {
        HashSet<ConstraintViolation<String>> constraintViolationSet = new HashSet<>();
        when(validator.validate(Mockito.<String>any(), (Class[]) any())).thenReturn(constraintViolationSet);
        Set<ConstraintViolation<Object>> actualValidateResult = jsonServiceImpl.validate("Object");
        assertSame(constraintViolationSet, actualValidateResult);
        assertTrue(actualValidateResult.isEmpty());
        verify(validator).validate(Mockito.<String>any(), (Class[]) any());
    }

}

