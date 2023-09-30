package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.F24RequestStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.FileRefEntity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class F24FileRequestMapperTest {

    @Test
    public void testEntityToDto() {
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity( "42");
        f24FileRequestEntity.setFiles(new HashMap<>());
        F24Request actualEntityToDtoResult = F24FileRequestMapper.entityToDto(f24FileRequestEntity);
        assertNull(actualEntityToDtoResult.getCost());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSetId());
        assertEquals("42", actualEntityToDtoResult.getRequestId());
        assertEquals("REQUEST#42", actualEntityToDtoResult.getPk());
        assertNull(actualEntityToDtoResult.getPathTokens());
        assertTrue(actualEntityToDtoResult.getFiles().isEmpty());
    }

    @Test
    public void testEntityToDto7() {
        HashMap<String, FileRefEntity> files = new HashMap<>();
        files.put("#", new FileRefEntity("#"));

        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity( "42");
        f24FileRequestEntity.setFiles(files);
        F24Request actualEntityToDtoResult = F24FileRequestMapper.entityToDto(f24FileRequestEntity);
        assertNull(actualEntityToDtoResult.getCost());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSetId());
        assertEquals("42", actualEntityToDtoResult.getRequestId());
        assertEquals("REQUEST#42", actualEntityToDtoResult.getPk());
        assertNull(actualEntityToDtoResult.getPathTokens());
        Map<String, F24Request.FileRef> files2 = actualEntityToDtoResult.getFiles();
        assertEquals(1, files2.size());
        assertEquals("#", files2.get("#").getFileKey());
    }

    @Test
    public void testDtoToEntity() {
        F24Request f24Request = new F24Request();
        f24Request.setCost(1);
        f24Request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        f24Request.setCxId("42");
        f24Request.setFiles(new HashMap<>());
        f24Request.setPathTokens("ABC123");
        f24Request.setRequestId("42");
        f24Request.setSetId("42");
        f24Request.setStatus(F24RequestStatus.TO_PROCESS);
        f24Request.setTtl(1L);
        f24Request.setUpdated(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        F24FileRequestEntity actualDtoToEntityResult = F24FileRequestMapper.dtoToEntity(f24Request);
        assertEquals(1, actualDtoToEntityResult.getCost().intValue());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertTrue(actualDtoToEntityResult.getFiles().isEmpty());
        assertEquals(F24RequestStatusEntity.TO_PROCESS, actualDtoToEntityResult.getStatus());
        assertEquals("42", actualDtoToEntityResult.getSetId());
        assertEquals("REQUEST#42", actualDtoToEntityResult.getPk());
        assertEquals("ABC123", actualDtoToEntityResult.getPathTokens());
    }

}

