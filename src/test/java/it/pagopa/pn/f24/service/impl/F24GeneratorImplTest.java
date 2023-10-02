package it.pagopa.pn.f24.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.pagopa.pn.f24.business.F24Converter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import org.f24.service.pdf.PDFCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {F24GeneratorImpl.class})
@ExtendWith(SpringExtension.class)
class F24GeneratorImplTest {
    @Autowired
    private F24GeneratorImpl f24GeneratorImpl;
    @MockBean
    F24Converter f24Converter;
    @MockBean
    PDFCreator pdfCreator;

    @Test
    void testGenerate() {
        F24Metadata metadata = mock(F24Metadata.class);
        when(metadata.getF24Simplified()).thenReturn(new F24Simplified());
        when(metadata.getF24Standard()).thenReturn(null);
        assertEquals(0, f24GeneratorImpl.generate(metadata).length);
        verify(metadata, atLeast(1)).getF24Simplified();
        verify(metadata).getF24Standard();
    }
}

