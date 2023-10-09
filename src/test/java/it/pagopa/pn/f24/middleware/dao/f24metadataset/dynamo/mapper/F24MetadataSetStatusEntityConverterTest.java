package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class F24MetadataSetStatusEntityConverterTest {
    private F24MetadataSetStatusEntityConverter converter;

    @BeforeEach
    public void setup() {
        converter = new F24MetadataSetStatusEntityConverter();
    }

    @Test
    void testTransformFrom() {
        AttributeValue actualTransformFromResult = converter.transformFrom(F24MetadataSetStatusEntity.VALIDATION_ENDED);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("VALIDATION_ENDED", actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#transformFrom(F24MetadataSetStatusEntity)}
     */
    @Test
    void testTransformFrom2() {
        AttributeValue actualTransformFromResult = converter.transformFrom(null);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("", actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#transformFrom(F24MetadataSetStatusEntity)}
     */
    @Test
    void testTransformFrom3() {
        AttributeValue actualTransformFromResult = converter.transformFrom(F24MetadataSetStatusEntity.TO_VALIDATE);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("TO_VALIDATE", actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo() {
        F24MetadataSetStatusEntity f24MetadataSetStatusEntity = converter.transformTo(
                AttributeValue.builder().s("VALIDATION_ENDED").build()
        );
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, f24MetadataSetStatusEntity);
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo2() {
        assertNull(converter.transformTo(AttributeValue.builder().s(null).build()));
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#type()}
     */
    @Test
    void testType() {
        EnhancedType<F24MetadataSetStatusEntity> actualTypeResult = converter.type();
        assertFalse(actualTypeResult.isWildcard());
        assertTrue(actualTypeResult.rawClassParameters().isEmpty());
        Class<F24MetadataSetStatusEntity> expectedRawClassResult = F24MetadataSetStatusEntity.class;
        assertSame(expectedRawClassResult, actualTypeResult.rawClass());
    }

    /**
     * Method under test: {@link F24MetadataSetStatusEntityConverter#attributeValueType()}
     */
    @Test
    void testAttributeValueType() {
        assertEquals(AttributeValueType.S, converter.attributeValueType());
    }
}

