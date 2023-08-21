package it.pagopa.pn.f24.service.factory;

import it.pagopa.pn.f24.dto.F24Type;
import org.springframework.stereotype.Component;

@Component
public class F24ConverterFactory {
    public F24Converter getConverter(F24Type f24Type) {

        switch (f24Type) {
            case F24_STANDARD -> new F24StandardConverter();
            case F24_SIMPLIFIED -> new F24SimplifiedConverter();
            case F24_ELID -> new F24ElidConverter();
            case F24_EXCISE -> new F24ExciseConverter();
            default -> throw new RuntimeException("Invalid F24Type");
        }

        return null;
    }
}
