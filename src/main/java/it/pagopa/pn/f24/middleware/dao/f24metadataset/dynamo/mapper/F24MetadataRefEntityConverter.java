package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class F24MetadataRefEntityConverter implements AttributeConverter<F24MetadataRefEntity> {
    @Override
    public AttributeValue transformFrom(F24MetadataRefEntity input) {
        return EnhancedAttributeValue.fromMap(createMap(input)).toAttributeValue();
    }

    private Map<String, AttributeValue> createMap(F24MetadataRefEntity input) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("applyCost", EnhancedAttributeValue.fromBoolean(input.isApplyCost()).toAttributeValue());
        map.put("fileKey", EnhancedAttributeValue.fromString(input.getFileKey()).toAttributeValue());
        map.put("sha256", EnhancedAttributeValue.fromString(input.getSha256()).toAttributeValue());
        return map;
    }

    @Override
    public F24MetadataRefEntity transformTo(AttributeValue input) {
        Map<String, AttributeValue> attributeValueMap = input.m();
        return F24MetadataRefEntity.builder()
                .applyCost(attributeValueMap.get("applyCost").bool())
                .sha256(attributeValueMap.get("sha256").s())
                .fileKey(attributeValueMap.get("fileKey").s())
                .build();
    }

    @Override
    public EnhancedType<F24MetadataRefEntity> type() {
        return EnhancedType.of(F24MetadataRefEntity.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }
}
