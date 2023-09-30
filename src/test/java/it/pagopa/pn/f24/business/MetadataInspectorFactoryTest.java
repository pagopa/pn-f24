package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.business.impl.ElidMetadataInspector;
import it.pagopa.pn.f24.business.impl.ExciseMetadataInspector;
import it.pagopa.pn.f24.business.impl.SimplifiedMetadataInspector;
import it.pagopa.pn.f24.business.impl.StandardMetadataInspector;
import it.pagopa.pn.f24.dto.F24Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetadataInspectorFactoryTest {
    @Test
    void testStandardMetadataInspector() {
        Assertions.assertEquals(StandardMetadataInspector.class, MetadataInspectorFactory.getInspector(F24Type.F24_STANDARD).getClass());
    }
    @Test
    void testExciseMetadataInspector() {
        Assertions.assertEquals(ExciseMetadataInspector.class, MetadataInspectorFactory.getInspector(F24Type.F24_EXCISE).getClass());
    }
    @Test
    void testElidMetadataInspector() {
        Assertions.assertEquals(ElidMetadataInspector.class, MetadataInspectorFactory.getInspector(F24Type.F24_ELID).getClass());
    }
    @Test
    void testSimplifiedMetadataInspector() {
        Assertions.assertEquals(SimplifiedMetadataInspector.class, MetadataInspectorFactory.getInspector(F24Type.F24_SIMPLIFIED).getClass());
    }
}