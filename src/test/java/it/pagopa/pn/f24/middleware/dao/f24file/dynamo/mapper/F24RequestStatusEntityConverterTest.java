package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class F24RequestStatusEntityConverterTest {

    private final F24RequestStatusEntityConverter converter = new F24RequestStatusEntityConverter();


    @Test
    public void testTransformFrom() {
        AttributeValue actualTransformFromResult = (new F24RequestStatusEntityConverter())
                .transformFrom(F24RequestStatusEntity.TO_PROCESS);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("TO_PROCESS", actualTransformFromResult.s());
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
    public void testTransformFrom2() {
        AttributeValue actualTransformFromResult = (new F24RequestStatusEntityConverter())
                .transformFrom(null);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.S, actualTransformFromResult.type());
        assertEquals("", actualTransformFromResult.s());
    }

    @Test
    public void testType() {
        EnhancedType<F24RequestStatusEntity> actualTypeResult = (new F24RequestStatusEntityConverter()).type();
        assertFalse(actualTypeResult.isWildcard());
        assertTrue(actualTypeResult.rawClassParameters().isEmpty());
        Class<F24RequestStatusEntity> expectedRawClassResult = F24RequestStatusEntity.class;
        assertSame(expectedRawClassResult, actualTypeResult.rawClass());
    }

    @Test
    public void testAttributeValueType() {
        assertEquals(AttributeValueType.S, (new F24RequestStatusEntityConverter()).attributeValueType());
    }

    @Test
    public void testTransformTo() {
        F24RequestStatusEntity f24RequestStatusEntity = converter.transformTo(
                AttributeValue.builder().s("PROCESSING").build()
        );
        Assertions.assertEquals(F24RequestStatusEntity.PROCESSING, f24RequestStatusEntity);
    }

    @Test
    public void testTransformTo2() {
        Assertions.assertNull(converter.transformTo(AttributeValue.builder().s(null).build()));
    }

}

