package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataValidationEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.Test;

class F24MetadataSetMapperTest {
    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto() {
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(new F24MetadataSetEntity());
        assertNull(actualEntityToDtoResult.getCreated());
        assertNull(actualEntityToDtoResult.getValidationResult());
        assertNull(actualEntityToDtoResult.getValidationEventSent());
        assertNull(actualEntityToDtoResult.getUpdated());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSha256());
        assertNull(actualEntityToDtoResult.getPk());
        assertNull(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertNull(actualEntityToDtoResult.getFileKeys());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto2() {
        HashMap<String, F24MetadataRefEntity> fileKeys = new HashMap<>();
        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(
                new F24MetadataSetEntity("Pk", F24MetadataSetStatusEntity.VALIDATION_ENDED, fileKeys, "Sha256", true, true,
                        f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        assertEquals(f24MetadataValidationEntityList, actualEntityToDtoResult.getValidationResult());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        assertTrue(actualEntityToDtoResult.getFileKeys().isEmpty());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto3() {
        HashMap<String, F24MetadataRefEntity> stringF24MetadataRefEntityMap = new HashMap<>();
        stringF24MetadataRefEntityMap.put("Key", new F24MetadataRefEntity("File Key", "Sha256", true));
        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(new F24MetadataSetEntity("Pk",
                F24MetadataSetStatusEntity.VALIDATION_ENDED, stringF24MetadataRefEntityMap, "Sha256", true, true,
                f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        assertEquals(f24MetadataValidationEntityList, actualEntityToDtoResult.getValidationResult());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        Map<String, F24MetadataRef> fileKeys = actualEntityToDtoResult.getFileKeys();
        assertEquals(1, fileKeys.size());
        F24MetadataRef getResult = fileKeys.get("Key");
        assertTrue(getResult.isApplyCost());
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto4() {
        HashMap<String, F24MetadataRefEntity> stringF24MetadataRefEntityMap = new HashMap<>();
        stringF24MetadataRefEntityMap.put("Key", new F24MetadataRefEntity("File Key", "Sha256", false));
        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(new F24MetadataSetEntity("Pk",
                F24MetadataSetStatusEntity.VALIDATION_ENDED, stringF24MetadataRefEntityMap, "Sha256", true, true,
                f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        assertEquals(f24MetadataValidationEntityList, actualEntityToDtoResult.getValidationResult());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        Map<String, F24MetadataRef> fileKeys = actualEntityToDtoResult.getFileKeys();
        assertEquals(1, fileKeys.size());
        F24MetadataRef getResult = fileKeys.get("Key");
        assertFalse(getResult.isApplyCost());
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto5() {
        HashMap<String, F24MetadataRefEntity> stringF24MetadataRefEntityMap = new HashMap<>();
        stringF24MetadataRefEntityMap.put("42", new F24MetadataRefEntity("File Key", "Sha256", true));
        stringF24MetadataRefEntityMap.put("Key", new F24MetadataRefEntity("File Key", "Sha256", true));
        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(new F24MetadataSetEntity("Pk",
                F24MetadataSetStatusEntity.VALIDATION_ENDED, stringF24MetadataRefEntityMap, "Sha256", true, true,
                f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        assertEquals(f24MetadataValidationEntityList, actualEntityToDtoResult.getValidationResult());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        Map<String, F24MetadataRef> fileKeys = actualEntityToDtoResult.getFileKeys();
        assertEquals(2, fileKeys.size());
        F24MetadataRef getResult = fileKeys.get("42");
        assertTrue(getResult.isApplyCost());
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
        F24MetadataRef getResult1 = fileKeys.get("Key");
        assertEquals("Sha256", getResult1.getSha256());
        assertEquals("File Key", getResult1.getFileKey());
        assertTrue(getResult1.isApplyCost());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto6() {
        F24MetadataValidationEntity f24MetadataValidationEntity = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationEntity.getElement()).thenReturn("Element");

        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity);
        HashMap<String, F24MetadataRefEntity> fileKeys = new HashMap<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(
                new F24MetadataSetEntity("Pk", F24MetadataSetStatusEntity.VALIDATION_ENDED, fileKeys, "Sha256", true, true,
                        f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        List<F24MetadataValidationIssue> validationResult = actualEntityToDtoResult.getValidationResult();
        assertEquals(1, validationResult.size());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        assertTrue(actualEntityToDtoResult.getFileKeys().isEmpty());
        F24MetadataValidationIssue getResult = validationResult.get(0);
        assertEquals("Element", getResult.getElement());
        assertEquals("Detail", getResult.getDetail());
        assertEquals("Code", getResult.getCode());
        verify(f24MetadataValidationEntity).getCode();
        verify(f24MetadataValidationEntity).getDetail();
        verify(f24MetadataValidationEntity).getElement();
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#entityToDto(F24MetadataSetEntity)}
     */
    @Test
    void testEntityToDto7() {
        F24MetadataValidationEntity f24MetadataValidationEntity = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationEntity.getElement()).thenReturn("Element");
        F24MetadataValidationEntity f24MetadataValidationEntity1 = mock(F24MetadataValidationEntity.class);
        when(f24MetadataValidationEntity1.getCode()).thenReturn("Code");
        when(f24MetadataValidationEntity1.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationEntity1.getElement()).thenReturn("Element");

        ArrayList<F24MetadataValidationEntity> f24MetadataValidationEntityList = new ArrayList<>();
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity1);
        f24MetadataValidationEntityList.add(f24MetadataValidationEntity);
        HashMap<String, F24MetadataRefEntity> fileKeys = new HashMap<>();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Instant created = atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(
                new F24MetadataSetEntity("Pk", F24MetadataSetStatusEntity.VALIDATION_ENDED, fileKeys, "Sha256", true, true,
                        f24MetadataValidationEntityList, created, atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant(), 1L));
        List<F24MetadataValidationIssue> validationResult = actualEntityToDtoResult.getValidationResult();
        assertEquals(2, validationResult.size());
        assertTrue(actualEntityToDtoResult.getHaveToSendValidationEvent());
        assertEquals(1L, actualEntityToDtoResult.getTtl().longValue());
        Instant expectedUpdated = actualEntityToDtoResult.getCreated();
        assertSame(expectedUpdated, actualEntityToDtoResult.getUpdated());
        assertEquals("Pk", actualEntityToDtoResult.getPk());
        assertTrue(actualEntityToDtoResult.getValidationEventSent());
        assertTrue(actualEntityToDtoResult.getFileKeys().isEmpty());
        assertEquals(F24MetadataStatus.VALIDATION_ENDED, actualEntityToDtoResult.getStatus());
        assertEquals("Sha256", actualEntityToDtoResult.getSha256());
        F24MetadataValidationIssue getResult = validationResult.get(0);
        assertEquals("Detail", getResult.getDetail());
        F24MetadataValidationIssue getResult1 = validationResult.get(1);
        assertEquals("Element", getResult1.getElement());
        assertEquals("Detail", getResult1.getDetail());
        assertEquals("Code", getResult1.getCode());
        assertEquals("Code", getResult.getCode());
        assertEquals("Element", getResult.getElement());
        verify(f24MetadataValidationEntity1).getCode();
        verify(f24MetadataValidationEntity1).getDetail();
        verify(f24MetadataValidationEntity1).getElement();
        verify(f24MetadataValidationEntity).getCode();
        verify(f24MetadataValidationEntity).getDetail();
        verify(f24MetadataValidationEntity).getElement();
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(new HashMap<>());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        assertEquals(f24MetadataValidationIssueList, actualDtoToEntityResult.getValidationResult());
        assertTrue(actualDtoToEntityResult.getFileKeys().isEmpty());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity2() {
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");
        f24MetadataRef.setSha256("Sha256");

        HashMap<String, F24MetadataRef> stringF24MetadataRefMap = new HashMap<>();
        stringF24MetadataRefMap.put("Key", f24MetadataRef);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(stringF24MetadataRefMap);
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        assertEquals(f24MetadataValidationIssueList, actualDtoToEntityResult.getValidationResult());
        Map<String, F24MetadataRefEntity> fileKeys = actualDtoToEntityResult.getFileKeys();
        assertEquals(1, fileKeys.size());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
        F24MetadataRefEntity getResult = fileKeys.get("Key");
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
        assertTrue(getResult.isApplyCost());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity3() {
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(false);
        f24MetadataRef.setFileKey("File Key");
        f24MetadataRef.setSha256("Sha256");

        HashMap<String, F24MetadataRef> stringF24MetadataRefMap = new HashMap<>();
        stringF24MetadataRefMap.put("Key", f24MetadataRef);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(stringF24MetadataRefMap);
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        assertEquals(f24MetadataValidationIssueList, actualDtoToEntityResult.getValidationResult());
        Map<String, F24MetadataRefEntity> fileKeys = actualDtoToEntityResult.getFileKeys();
        assertEquals(1, fileKeys.size());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
        F24MetadataRefEntity getResult = fileKeys.get("Key");
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
        assertFalse(getResult.isApplyCost());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity4() {
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setFileKey("File Key");
        f24MetadataRef.setSha256("Sha256");

        F24MetadataRef f24MetadataRef1 = new F24MetadataRef();
        f24MetadataRef1.setApplyCost(true);
        f24MetadataRef1.setFileKey("File Key");
        f24MetadataRef1.setSha256("Sha256");

        HashMap<String, F24MetadataRef> stringF24MetadataRefMap = new HashMap<>();
        stringF24MetadataRefMap.put("42", f24MetadataRef1);
        stringF24MetadataRefMap.put("Key", f24MetadataRef);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(stringF24MetadataRefMap);
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        assertEquals(f24MetadataValidationIssueList, actualDtoToEntityResult.getValidationResult());
        Map<String, F24MetadataRefEntity> fileKeys = actualDtoToEntityResult.getFileKeys();
        assertEquals(2, fileKeys.size());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        F24MetadataRefEntity getResult = fileKeys.get("Key");
        assertEquals("Sha256", getResult.getSha256());
        assertEquals("File Key", getResult.getFileKey());
        F24MetadataRefEntity getResult1 = fileKeys.get("42");
        assertEquals("Sha256", getResult1.getSha256());
        assertEquals("File Key", getResult1.getFileKey());
        assertTrue(getResult.isApplyCost());
        assertTrue(getResult1.isApplyCost());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity5() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(new HashMap<>());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(null);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        assertEquals(f24MetadataValidationIssueList, actualDtoToEntityResult.getValidationResult());
        assertTrue(actualDtoToEntityResult.getFileKeys().isEmpty());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertNull(actualDtoToEntityResult.getStatus());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity6() {
        F24MetadataValidationIssue f24MetadataValidationIssue = mock(F24MetadataValidationIssue.class);
        when(f24MetadataValidationIssue.getCode()).thenReturn("Code");
        when(f24MetadataValidationIssue.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationIssue.getElement()).thenReturn("Element");

        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataValidationIssueList.add(f24MetadataValidationIssue);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(new HashMap<>());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        List<F24MetadataValidationEntity> validationResult = actualDtoToEntityResult.getValidationResult();
        assertEquals(1, validationResult.size());
        assertTrue(actualDtoToEntityResult.getFileKeys().isEmpty());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
        F24MetadataValidationEntity getResult = validationResult.get(0);
        assertEquals("Element", getResult.getElement());
        assertEquals("Detail", getResult.getDetail());
        assertEquals("Code", getResult.getCode());
        verify(f24MetadataValidationIssue).getCode();
        verify(f24MetadataValidationIssue).getDetail();
        verify(f24MetadataValidationIssue).getElement();
    }

    /**
     * Method under test: {@link F24MetadataSetMapper#dtoToEntity(F24MetadataSet)}
     */
    @Test
    void testDtoToEntity7() {
        F24MetadataValidationIssue f24MetadataValidationIssue = mock(F24MetadataValidationIssue.class);
        when(f24MetadataValidationIssue.getCode()).thenReturn("Code");
        when(f24MetadataValidationIssue.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationIssue.getElement()).thenReturn("Element");
        F24MetadataValidationIssue f24MetadataValidationIssue1 = mock(F24MetadataValidationIssue.class);
        when(f24MetadataValidationIssue1.getCode()).thenReturn("Code");
        when(f24MetadataValidationIssue1.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationIssue1.getElement()).thenReturn("Element");

        ArrayList<F24MetadataValidationIssue> f24MetadataValidationIssueList = new ArrayList<>();
        f24MetadataValidationIssueList.add(f24MetadataValidationIssue1);
        f24MetadataValidationIssueList.add(f24MetadataValidationIssue);

        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(new HashMap<>());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setPk("Pk");
        f24MetadataSet.setSha256("Sha256");
        f24MetadataSet.setStatus(F24MetadataStatus.VALIDATION_ENDED);
        f24MetadataSet.setTtl(1L);
        LocalDateTime atStartOfDayResult1 = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setUpdated(atStartOfDayResult1.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setValidationEventSent(true);
        f24MetadataSet.setValidationResult(f24MetadataValidationIssueList);
        F24MetadataSetEntity actualDtoToEntityResult = F24MetadataSetMapper.dtoToEntity(f24MetadataSet);
        List<F24MetadataValidationEntity> validationResult = actualDtoToEntityResult.getValidationResult();
        assertEquals(2, validationResult.size());
        assertTrue(actualDtoToEntityResult.getFileKeys().isEmpty());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        F24MetadataValidationEntity getResult = validationResult.get(0);
        assertEquals("Detail", getResult.getDetail());
        F24MetadataValidationEntity getResult1 = validationResult.get(1);
        assertEquals("Element", getResult1.getElement());
        assertEquals("Detail", getResult1.getDetail());
        assertEquals("Code", getResult1.getCode());
        assertEquals("Code", getResult.getCode());
        assertEquals("Element", getResult.getElement());
        verify(f24MetadataValidationIssue1).getCode();
        verify(f24MetadataValidationIssue1).getDetail();
        verify(f24MetadataValidationIssue1).getElement();
        verify(f24MetadataValidationIssue).getCode();
        verify(f24MetadataValidationIssue).getDetail();
        verify(f24MetadataValidationIssue).getElement();
    }
}

