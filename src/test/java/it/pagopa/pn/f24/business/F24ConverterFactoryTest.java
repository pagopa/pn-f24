package it.pagopa.pn.f24.business;


import it.pagopa.pn.f24.business.impl.F24ElidConverter;
import it.pagopa.pn.f24.business.impl.F24ExciseConverter;
import it.pagopa.pn.f24.business.impl.F24SimplifiedConverter;
import it.pagopa.pn.f24.business.impl.F24StandardConverter;
import it.pagopa.pn.f24.dto.F24Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class F24ConverterFactoryTest {
    @Test
    void testGetElidConverter() {
        Assertions.assertEquals(F24ElidConverter.class, F24ConverterFactory.getConverter(F24Type.F24_ELID).getClass());
    }
    @Test
    void testGetStandardConverter() {
        Assertions.assertEquals(F24StandardConverter.class, F24ConverterFactory.getConverter(F24Type.F24_STANDARD).getClass());
    }
    @Test
    void testGetExciseConverter() {
        Assertions.assertEquals(F24ExciseConverter.class, F24ConverterFactory.getConverter(F24Type.F24_EXCISE).getClass());
    }
    @Test
    void testGetSimplifiedConverter() {
        Assertions.assertEquals(F24SimplifiedConverter.class, F24ConverterFactory.getConverter(F24Type.F24_SIMPLIFIED).getClass());
    }
}