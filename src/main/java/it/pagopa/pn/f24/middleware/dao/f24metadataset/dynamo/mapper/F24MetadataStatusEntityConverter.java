package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
public class F24MetadataStatusEntityConverter implements AttributeConverter<F24MetadataSetStatusEntity> {

    @Override
    public AttributeValue transformFrom(F24MetadataSetStatusEntity input) {
        String value = input != null ? input.getValue() : "";
        return EnhancedAttributeValue.fromString(value).toAttributeValue();
    }

    @Override
    public F24MetadataSetStatusEntity transformTo(AttributeValue input) {
        if( input.s() != null ) {
            return F24MetadataSetStatusEntity.valueOf(input.s());
        }
        return null;
    }

    @Override
    public EnhancedType<F24MetadataSetStatusEntity> type() {
        return EnhancedType.of(F24MetadataSetStatusEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}

