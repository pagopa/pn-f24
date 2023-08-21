package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataStatusEntity;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
public class F24MetadataStatusEntityConverter implements AttributeConverter<F24MetadataStatusEntity> {

    @Override
    public AttributeValue transformFrom(F24MetadataStatusEntity input) {
        String value = input != null ? input.getValue() : "";
        return EnhancedAttributeValue.fromString(value).toAttributeValue();
    }

    @Override
    public F24MetadataStatusEntity transformTo(AttributeValue input) {
        if( input.s() != null ) {
            return F24MetadataStatusEntity.valueOf(input.s());
        }
        return null;
    }

    @Override
    public EnhancedType<F24MetadataStatusEntity> type() {
        return EnhancedType.of(F24MetadataStatusEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}

