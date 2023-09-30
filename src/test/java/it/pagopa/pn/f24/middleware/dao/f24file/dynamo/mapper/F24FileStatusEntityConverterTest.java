package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class F24FileStatusEntityConverterTest {

    private F24FileStatusEntityConverter converter;

    @BeforeEach
    void setup() {
        converter = new F24FileStatusEntityConverter();
    }
    @Test
    void testTransformFrom() {
        AttributeValue actualTransformFromResult = converter.transformFrom(F24FileStatusEntity.PROCESSING);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("PROCESSING", actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

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
    @Test
    void testTransformFrom3() {
        AttributeValue actualTransformFromResult = converter.transformFrom(F24FileStatusEntity.PROCESSING);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("PROCESSING", actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    @Test
    void testTransformTo() {
        F24FileStatusEntity f24FileStatusEntity = converter.transformTo(
                AttributeValue.builder().s("PROCESSING").build()
        );
        assertEquals(F24FileStatusEntity.PROCESSING, f24FileStatusEntity);
    }

    @Test
    void testTransformTo2() {
        assertNull(converter.transformTo(AttributeValue.builder().s(null).build()));
    }

    @Test
    void testType() {
        EnhancedType<F24FileStatusEntity> actualTypeResult = converter.type();
        assertFalse(actualTypeResult.isWildcard());
        assertTrue(actualTypeResult.rawClassParameters().isEmpty());
        Class<F24FileStatusEntity> expectedRawClassResult = F24FileStatusEntity.class;
        assertSame(expectedRawClassResult, actualTypeResult.rawClass());
    }

    @Test
    void testAttributeValueType() {
        assertEquals(AttributeValueType.S, converter.attributeValueType());
    }
}
