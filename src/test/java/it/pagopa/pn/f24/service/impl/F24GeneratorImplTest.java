package it.pagopa.pn.f24.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import it.pagopa.pn.f24.business.F24Converter;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Elid;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Excise;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.InpsSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.LocalTaxSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RegionSection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SocialSecuritySection;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TaxPayerStandard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection;
import org.f24.dto.form.F24Form;
import org.f24.exception.ResourceException;
import org.f24.service.pdf.PDFCreator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {F24GeneratorImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class F24GeneratorImplTest {
    @Autowired
    private F24GeneratorImpl f24GeneratorImpl;
    @MockBean
    F24Converter f24Converter;
    @MockBean
    PDFCreator pdfCreator;

    @Test
    public void testGenerate() {
        F24Metadata metadata = mock(F24Metadata.class);
        when(metadata.getF24Simplified()).thenReturn(new F24Simplified());
        when(metadata.getF24Standard()).thenReturn(null);
        assertEquals(0, f24GeneratorImpl.generate(metadata).length);
        verify(metadata, atLeast(1)).getF24Simplified();
        verify(metadata).getF24Standard();
    }
}

