package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.BaseEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.junit.Ignore;

import org.junit.Test;

public class F24FileCacheMapperTest {
    @Test
    public void testEntityToDto() {
        F24File actualEntityToDtoResult = F24FileCacheMapper.entityToDto(F24FileCacheMapper.dtoToEntity(new F24File()));
        assertNull(actualEntityToDtoResult.getCost());
        assertNull(actualEntityToDtoResult.getUpdated());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getRequestIds());
        assertEquals("CACHE#null#null#NO_COST#null", actualEntityToDtoResult.getPk());
        assertEquals("null", actualEntityToDtoResult.getPathTokens());
        assertNull(actualEntityToDtoResult.getFileKey());
        assertEquals("null", actualEntityToDtoResult.getCxId());
        assertNull(actualEntityToDtoResult.getCreated());
    }

    @Test
    public void testDtoToEntity() {
        F24File f24File = new F24File();
        f24File.setCost(1);
        f24File.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        f24File.setCxId("42");
        f24File.setFileKey("File Key");
        f24File.setPathTokens("ABC123");
        f24File.setPk("Pk");
        f24File.setRequestIds(new ArrayList<>());
        f24File.setSetId("42");
        f24File.setStatus(F24FileStatus.PROCESSING);
        f24File.setTtl(1L);
        f24File.setUpdated(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        F24FileCacheEntity actualDtoToEntityResult = F24FileCacheMapper.dtoToEntity(f24File);
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        Instant expectedCreated = actualDtoToEntityResult.getUpdated();
        assertSame(expectedCreated, actualDtoToEntityResult.getCreated());
        assertEquals("File Key", actualDtoToEntityResult.getFileKey());
        assertEquals(F24FileStatusEntity.PROCESSING, actualDtoToEntityResult.getStatus());
        assertTrue(actualDtoToEntityResult.getRequestIds().isEmpty());
        assertEquals("CACHE#42#42#1#ABC123", actualDtoToEntityResult.getPk());
    }

}
