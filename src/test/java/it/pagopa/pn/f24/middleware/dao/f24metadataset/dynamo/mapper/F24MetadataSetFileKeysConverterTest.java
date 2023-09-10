package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static org.junit.jupiter.api.Assertions.*;

class F24MetadataSetFileKeysConverterTest {
    private F24MetadataSetFileKeysConverter converter;

    @BeforeEach
    public void setup() {
        converter = new F24MetadataSetFileKeysConverter();
    }
    /**
     * Method under test: default or parameterless constructor of {@link F24MetadataSetFileKeysConverter}
     */
    @Test
    void testConstructor() {
        EnhancedType<Map<String, F24MetadataRefEntity>> typeResult = converter.type();
        assertFalse(typeResult.isWildcard());
        List<EnhancedType<?>> rawClassParametersResult = typeResult.rawClassParameters();
        assertEquals(2, rawClassParametersResult.size());
        Class<Map> expectedRawClassResult = Map.class;
        assertSame(expectedRawClassResult, typeResult.rawClass());
        EnhancedType<?> getResult = rawClassParametersResult.get(1);
        assertTrue(getResult.rawClassParameters().isEmpty());
        Class<String> expectedRawClassResult1 = String.class;
        EnhancedType<?> getResult1 = rawClassParametersResult.get(0);
        assertSame(expectedRawClassResult1, getResult1.rawClass());
        assertFalse(getResult1.isWildcard());
        Class<F24MetadataRefEntity> expectedRawClassResult2 = F24MetadataRefEntity.class;
        assertSame(expectedRawClassResult2, getResult.rawClass());
        assertFalse(getResult.isWildcard());
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#transformFrom(Map)}
     */
    @Test
    void testTransformFrom() {
        AttributeValue actualTransformFromResult = converter.transformFrom(new HashMap<>());
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.M, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertTrue(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#transformFrom(Map)}
     */
    @Test
    void testTransformFrom2() {

        HashMap<String, F24MetadataRefEntity> stringF24MetadataRefEntityMap = new HashMap<>();
        stringF24MetadataRefEntityMap.put("Key", new F24MetadataRefEntity("File Key", "Sha256", true));
        AttributeValue actualTransformFromResult = converter.transformFrom(stringF24MetadataRefEntityMap);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.M, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertTrue(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#transformFrom(Map)}
     */
    @Test
    void testTransformFrom3() {
        HashMap<String, F24MetadataRefEntity> stringF24MetadataRefEntityMap = new HashMap<>();
        stringF24MetadataRefEntityMap.put("applyCost", new F24MetadataRefEntity("applyCost", "applyCost", true));
        stringF24MetadataRefEntityMap.put("Key", new F24MetadataRefEntity("File Key", "Sha256", true));
        AttributeValue actualTransformFromResult = converter.transformFrom(stringF24MetadataRefEntityMap);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.M, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertTrue(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo() {
        assertNotNull(converter.transformTo(buildAttributeValue()));
    }

    private AttributeValue buildAttributeValue() {
        return AttributeValue.builder()
                .m(Map.of("test", buildMetadataRefAttribute()))
                .build();
    }

    private AttributeValue buildMetadataRefAttribute() {
        return AttributeValue.builder().m(
                Map.of(
                    "fileKey", AttributeValue.builder().s("fileKey").build(),
                    "applyCost", AttributeValue.builder().bool(true).build(),
                    "sha256", AttributeValue.builder().s("test").build()
                )
            )
            .build();
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#type()}
     */
    @Test
    void testType() {
        EnhancedType<Map<String, F24MetadataRefEntity>> actualTypeResult = converter.type();
        assertSame(actualTypeResult.rawClass(), Map.class);
    }

    /**
     * Method under test: {@link F24MetadataSetFileKeysConverter#attributeValueType()}
     */
    @Test
    void testAttributeValueType() {
        assertEquals(AttributeValueType.M, converter.attributeValueType());
    }
}

