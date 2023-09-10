package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class F24MetadataRefEntityConverterTest {

    private F24MetadataRefEntityConverter converter;

    @BeforeEach
    public void setup() {
        converter = new F24MetadataRefEntityConverter();
    }

    /**
     * Method under test: {@link F24MetadataRefEntityConverter#transformFrom(F24MetadataRefEntity)}
     */
    @Test
    void testTransformFrom() {
        AttributeValue actualTransformFromResult = converter
                .transformFrom(new F24MetadataRefEntity("File Key", "Sha256", true));
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
     * Method under test: {@link F24MetadataRefEntityConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo() {
        assertNotNull(converter.transformTo(AttributeValue.builder().m(buildMap()).build()));
    }

    private Map<String, AttributeValue> buildMap() {
        return Map.of(
                "fileKey", AttributeValue.builder().s("fileKey").build(),
                "applyCost", AttributeValue.builder().bool(true).build(),
                "sha256", AttributeValue.builder().s("test").build()
        );
    }

    /**
     * Method under test: {@link F24MetadataRefEntityConverter#type()}
     */
    @Test
    void testType() {
        EnhancedType<F24MetadataRefEntity> actualTypeResult = converter.type();
        assertFalse(actualTypeResult.isWildcard());
        assertTrue(actualTypeResult.rawClassParameters().isEmpty());
        Class<F24MetadataRefEntity> expectedRawClassResult = F24MetadataRefEntity.class;
        assertSame(expectedRawClassResult, actualTypeResult.rawClass());
    }

    /**
     * Method under test: {@link F24MetadataRefEntityConverter#attributeValueType()}
     */
    @Test
    void testAttributeValueType() {
        assertEquals(AttributeValueType.M, (new F24MetadataRefEntityConverter()).attributeValueType());
    }
}

