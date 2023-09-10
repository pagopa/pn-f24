package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataValidationEntity;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class F24MetadataSetValidationResultConverter implements AttributeConverter<List<F24MetadataValidationEntity>> {
    @Override
    public AttributeValue transformFrom(List<F24MetadataValidationEntity> input) {
        if (input == null) {
            return EnhancedAttributeValue.nullValue().toAttributeValue();
        }
        return EnhancedAttributeValue.fromListOfAttributeValues(convertEntityListToAttributeValue(input)).toAttributeValue();
    }

    private List<AttributeValue> convertEntityListToAttributeValue(List<F24MetadataValidationEntity> input) {
        return input.stream()
                .map(
                    elem ->
                        EnhancedAttributeValue.fromMap(
                            Map.of(
                                "detail", EnhancedAttributeValue.fromString(elem.getDetail()).toAttributeValue(),
                                "code", EnhancedAttributeValue.fromString(elem.getCode()).toAttributeValue(),
                                "element", EnhancedAttributeValue.fromString(elem.getElement()).toAttributeValue()
                            )
                        )
                        .toAttributeValue()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<F24MetadataValidationEntity> transformTo(AttributeValue input) {
        return convertAttributeValueToEntityList(input);
    }

    private List<F24MetadataValidationEntity> convertAttributeValueToEntityList(AttributeValue input) {
        if(input == null) {
            return null;
        }

        List<AttributeValue> attributeValues = input.l();
        return attributeValues.stream().map(attributeValue -> F24MetadataValidationEntity.builder()
                .code(attributeValue.m().get("code").s())
                .detail(attributeValue.m().get("detail").s())
                .element(attributeValue.m().get("element").s())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public EnhancedType<List<F24MetadataValidationEntity>> type() {
        return EnhancedType.listOf(F24MetadataValidationEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }
}
