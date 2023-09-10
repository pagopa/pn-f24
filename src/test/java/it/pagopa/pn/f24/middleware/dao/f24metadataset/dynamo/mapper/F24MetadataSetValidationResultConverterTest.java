package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataValidationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class F24MetadataSetValidationResultConverterTest {
    private F24MetadataSetValidationResultConverter converter;

    @BeforeEach
    public void setup() {
        converter = new F24MetadataSetValidationResultConverter();
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformFrom(List)}
     */
    @Test
    void testTransformFrom() {
        AttributeValue actualTransformFromResult = converter.transformFrom(new ArrayList<>());
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.L, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertTrue(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformFrom(List)}
     */
    @Test
    void testTransformFrom2() {
        AttributeValue actualTransformFromResult = converter.transformFrom(null);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.NUL, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertTrue(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertFalse(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformFrom(List)}
     */
    @Test
    void testTransformFrom3() {
        F24MetadataValidationEntity f24MetadataValidationEntity = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity.getElement()).thenReturn("Element");
        when(f24MetadataValidationEntity.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity.getDetail()).thenReturn("Detail");

        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity);
        AttributeValue actualTransformFromResult = converter.transformFrom(f24MetadataValidationEntityList);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.L, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertTrue(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
        verify(f24MetadataValidationEntity).getCode();
        verify(f24MetadataValidationEntity).getDetail();
        verify(f24MetadataValidationEntity).getElement();
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformFrom(List)}
     */
    @Test
    void testTransformFrom4() {
        F24MetadataValidationEntity f24MetadataValidationEntity = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity.getElement()).thenReturn("Element");
        when(f24MetadataValidationEntity.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity.getDetail()).thenReturn("Detail");
        F24MetadataValidationEntity f24MetadataValidationEntity1 = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity1.getElement()).thenReturn("Element");
        when(f24MetadataValidationEntity1.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity1.getDetail()).thenReturn("Detail");

        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity1);
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity);
        AttributeValue actualTransformFromResult = converter.transformFrom(f24MetadataValidationEntityList);
        assertNull(actualTransformFromResult.b());
        assertEquals(AttributeValue.Type.L, actualTransformFromResult.type());
        assertNull(actualTransformFromResult.s());
        assertNull(actualTransformFromResult.nul());
        assertNull(actualTransformFromResult.n());
        assertFalse(actualTransformFromResult.hasSs());
        assertFalse(actualTransformFromResult.hasNs());
        assertFalse(actualTransformFromResult.hasM());
        assertTrue(actualTransformFromResult.hasL());
        assertFalse(actualTransformFromResult.hasBs());
        assertNull(actualTransformFromResult.bool());
        verify(f24MetadataValidationEntity1).getCode();
        verify(f24MetadataValidationEntity1).getDetail();
        verify(f24MetadataValidationEntity1).getElement();
        verify(f24MetadataValidationEntity).getCode();
        verify(f24MetadataValidationEntity).getDetail();
        verify(f24MetadataValidationEntity).getElement();
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo() {
        assertNull(converter.transformTo(null));
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#transformTo(AttributeValue)}
     */
    @Test
    void testTransformTo2() {
        assertNotNull(converter.transformTo(buildAttributeValue()));
    }

    private AttributeValue buildAttributeValue() {
        return AttributeValue.builder()
                .l(AttributeValue.builder()
                        .m(
                            Map.of(
                                "code", AttributeValue.fromS("test"),
                                "detail", AttributeValue.fromS("test"),
                                    "element", AttributeValue.fromS("test")
                            )
                        )
                        .build())
                .build();
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#type()}
     */
    @Test
    void testType() {
        EnhancedType<List<F24MetadataValidationEntity>> actualTypeResult = converter.type();
        assertFalse(actualTypeResult.isWildcard());
        List<EnhancedType<?>> rawClassParametersResult = actualTypeResult.rawClassParameters();
        assertEquals(1, rawClassParametersResult.size());
        Class<List> expectedRawClassResult = List.class;
        assertSame(expectedRawClassResult, actualTypeResult.rawClass());
        EnhancedType<?> getResult = rawClassParametersResult.get(0);
        assertFalse(getResult.isWildcard());
        assertTrue(getResult.rawClassParameters().isEmpty());
        Class<F24MetadataValidationEntity> expectedRawClassResult1 = F24MetadataValidationEntity.class;
        assertSame(expectedRawClassResult1, getResult.rawClass());
    }

    /**
     * Method under test: {@link F24MetadataSetValidationResultConverter#attributeValueType()}
     */
    @Test
    void testAttributeValueType() {
        assertEquals(AttributeValueType.L, converter.attributeValueType());
    }
}

