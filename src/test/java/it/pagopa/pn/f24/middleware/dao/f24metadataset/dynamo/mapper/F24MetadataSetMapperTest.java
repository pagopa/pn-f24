package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void testEntityToDto() {

        F24MetadataSetEntity f24MetadataSetEntity = new F24MetadataSetEntity();
        f24MetadataSetEntity.setSetId("setId");
        f24MetadataSetEntity.setFileKeys(Map.of("key",new F24MetadataRefEntity()));
        F24MetadataValidationEntity f24MetadataValidationEntity = new F24MetadataValidationEntity();
        f24MetadataValidationEntity.setCode("123");
        f24MetadataValidationEntity.setDetail("details");
        f24MetadataValidationEntity.setElement("element");

        f24MetadataSetEntity.setValidationResult(List.of(f24MetadataValidationEntity));

        F24MetadataSet actualEntityToDtoResult = F24MetadataSetMapper.entityToDto(f24MetadataSetEntity);
        assertNull(actualEntityToDtoResult.getCreated());
        assertNull(actualEntityToDtoResult.getValidationEventSent());
        assertNull(actualEntityToDtoResult.getUpdated());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSha256());
        assertNull(actualEntityToDtoResult.getHaveToSendValidationEvent());
    }

    @Test
    void testDtoToEntity() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        f24MetadataSet.setCreated(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        f24MetadataSet.setFileKeys(new HashMap<>());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setSetId("SetId");
        f24MetadataSet.setCreatorCxId("cxId");
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
        Instant expectedUpdated = actualDtoToEntityResult.getCreated();
        assertSame(expectedUpdated, actualDtoToEntityResult.getUpdated());
        assertTrue(actualDtoToEntityResult.getValidationEventSent());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals(F24MetadataSetStatusEntity.VALIDATION_ENDED, actualDtoToEntityResult.getStatus());
        assertEquals("Sha256", actualDtoToEntityResult.getSha256());
        assertTrue(actualDtoToEntityResult.getHaveToSendValidationEvent());
    }

}

