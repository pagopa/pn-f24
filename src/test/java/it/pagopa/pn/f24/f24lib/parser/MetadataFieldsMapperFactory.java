package it.pagopa.pn.f24.f24lib.parser;

import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.f24lib.parser.mapper.*;

public class MetadataFieldsMapperFactory {
    private MetadataFieldsMapperFactory() { }
    public static MetadataFieldsToPdfFieldsMapper getMapper(F24Type f24Type) {

        return switch (f24Type) {
            case F24_STANDARD -> new StandardMetadataFieldsToPdfFieldsMapper();
            case F24_SIMPLIFIED -> new SimplifiedMetadataFieldsToPdfFieldsMapper();
            case F24_ELID -> new ElidMetadataFieldsToPdfFieldsMapper();
            case F24_EXCISE -> new ExciseMetadataFieldsToPdfFieldsMapper();
        };
    }
}
