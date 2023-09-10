package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.business.impl.F24ElidConverter;
import it.pagopa.pn.f24.business.impl.F24ExciseConverter;
import it.pagopa.pn.f24.business.impl.F24SimplifiedConverter;
import it.pagopa.pn.f24.business.impl.F24StandardConverter;
import it.pagopa.pn.f24.dto.F24Type;
import org.springframework.stereotype.Component;

@Component
public class F24ConverterFactory {
    public static F24Converter getConverter(F24Type f24Type) {

        return switch (f24Type) {
            case F24_STANDARD -> new F24StandardConverter();
            case F24_SIMPLIFIED -> new F24SimplifiedConverter();
            case F24_ELID -> new F24ElidConverter();
            case F24_EXCISE -> new F24ExciseConverter();
        };
    }
}
