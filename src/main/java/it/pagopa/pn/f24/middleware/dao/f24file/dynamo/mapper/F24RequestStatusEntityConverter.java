package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class F24RequestStatusEntityConverter implements AttributeConverter<F24RequestStatusEntity> {
    @Override
    public AttributeValue transformFrom(F24RequestStatusEntity input) {
        String value = input != null ? input.getValue() : "";
        return EnhancedAttributeValue.fromString(value).toAttributeValue();
    }

    @Override
    public F24RequestStatusEntity transformTo(AttributeValue input) {
        if( input.s() != null ) {
            return F24RequestStatusEntity.valueOf(input.s());
        }
        return null;
    }

    @Override
    public EnhancedType<F24RequestStatusEntity> type() {
        return EnhancedType.of(F24RequestStatusEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
