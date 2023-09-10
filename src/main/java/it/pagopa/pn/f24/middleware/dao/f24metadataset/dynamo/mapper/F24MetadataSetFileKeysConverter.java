package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.MapAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.string.StringStringConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class F24MetadataSetFileKeysConverter implements AttributeConverter<Map<String, F24MetadataRefEntity>> {
    private static AttributeConverter<Map<String, F24MetadataRefEntity>> mapConverter;
    public F24MetadataSetFileKeysConverter() {
        mapConverter =
                MapAttributeConverter.builder(EnhancedType.mapOf(String.class, F24MetadataRefEntity.class))
                        .mapConstructor(HashMap::new)
                        .keyConverter(StringStringConverter.create())
                        .valueConverter(new F24MetadataRefEntityConverter())
                        .build();
    }
    @Override
    public AttributeValue transformFrom(Map<String, F24MetadataRefEntity> input) {
        return mapConverter.transformFrom(input);
    }

    @Override
    public Map<String, F24MetadataRefEntity> transformTo(AttributeValue input) {
        return mapConverter.transformTo(input);
    }

    @Override
    public EnhancedType<Map<String, F24MetadataRefEntity>> type() {
        return mapConverter.type();
    }

    @Override
    public AttributeValueType attributeValueType() {
        return mapConverter.attributeValueType();
    }
}
