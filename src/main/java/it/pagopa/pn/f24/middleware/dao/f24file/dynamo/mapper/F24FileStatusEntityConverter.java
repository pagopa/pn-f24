package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
public class F24FileStatusEntityConverter implements AttributeConverter<F24FileStatusEntity> {

    @Override
    public AttributeValue transformFrom(F24FileStatusEntity input) {
        String value = input != null ? input.getValue() : "";
        return EnhancedAttributeValue.fromString(value).toAttributeValue();
    }

    @Override
    public F24FileStatusEntity transformTo(AttributeValue input) {
        if( input.s() != null ) {
            return F24FileStatusEntity.valueOf(input.s());
        }
        return null;
    }

    @Override
    public EnhancedType<F24FileStatusEntity> type() {
        return EnhancedType.of(F24FileStatusEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}

