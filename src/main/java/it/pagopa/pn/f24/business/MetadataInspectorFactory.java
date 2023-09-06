package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.business.impl.ElidMetadataInspector;
import it.pagopa.pn.f24.business.impl.ExciseMetadataInspector;
import it.pagopa.pn.f24.business.impl.SimplifiedMetadataInspector;
import it.pagopa.pn.f24.business.impl.StandardMetadataInspector;
import it.pagopa.pn.f24.dto.F24Type;
import org.springframework.stereotype.Component;

@Component
public class MetadataInspectorFactory {
    public static MetadataInspector getInspector(F24Type f24Type) {

        switch (f24Type) {
            case F24_STANDARD -> new StandardMetadataInspector();
            case F24_SIMPLIFIED -> new SimplifiedMetadataInspector();
            case F24_ELID -> new ElidMetadataInspector();
            case F24_EXCISE -> new ExciseMetadataInspector();
            default -> throw new RuntimeException("Invalid F24Type");
        }
        return new StandardMetadataInspector();
    }
}
