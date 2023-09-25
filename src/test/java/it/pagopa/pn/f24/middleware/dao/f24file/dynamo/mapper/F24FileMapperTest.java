package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;

import java.time.LocalDate;

import org.junit.Ignore;

import org.junit.Test;

public class F24FileMapperTest {
    /**
     * Method under test: {@link F24FileMapper#entityToDto(F24FileEntity)}
     */
    @Test
    public void testEntityToDto() {
        F24File actualEntityToDtoResult = F24FileMapper.entityToDto(new F24FileEntity());
        assertNull(actualEntityToDtoResult.getCreated());
        assertNull(actualEntityToDtoResult.getUpdated());
        assertNull(actualEntityToDtoResult.getTtl());
        assertNull(actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSk());
        assertNull(actualEntityToDtoResult.getRequestId());
        assertNull(actualEntityToDtoResult.getPk());
        assertNull(actualEntityToDtoResult.getFileKey());
    }

    /**
     * Method under test: {@link F24FileMapper#entityToDto(F24FileEntity)}
     */
    @Test
    public void testEntityToDto2() {
        F24FileEntity f24FileEntity = new F24FileEntity();
        f24FileEntity.setStatus(F24FileStatusEntity.PROCESSING);
        F24File actualEntityToDtoResult = F24FileMapper.entityToDto(f24FileEntity);
        assertNull(actualEntityToDtoResult.getCreated());
        assertNull(actualEntityToDtoResult.getUpdated());
        assertNull(actualEntityToDtoResult.getTtl());
        assertEquals(F24FileStatus.PROCESSING, actualEntityToDtoResult.getStatus());
        assertNull(actualEntityToDtoResult.getSk());
        assertNull(actualEntityToDtoResult.getRequestId());
        assertNull(actualEntityToDtoResult.getPk());
        assertNull(actualEntityToDtoResult.getFileKey());
    }

    /**
     * Method under test: {@link F24FileMapper#entityToDto(F24FileEntity)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testEntityToDto3() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileEntity.getPk()" because "f24FileEntity" is null
        //       at it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper.F24FileMapper.entityToDto(F24FileMapper.java:14)
        //   See https://diff.blue/R013 to resolve this issue.

        F24FileMapper.entityToDto(null);
    }

    /**
     * Method under test: {@link F24FileMapper#dtoToEntity(F24File)}
     */
    @Test
    public void testDtoToEntity() {
        F24File f24File = mock(F24File.class);
        when(f24File.getStatus()).thenReturn(F24FileStatus.PROCESSING);
        when(f24File.getTtl()).thenReturn(1L);
        when(f24File.getCreated()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(f24File.getFileKey()).thenReturn("File Key");
        when(f24File.getPk()).thenReturn("Pk");
        when(f24File.getRequestId()).thenReturn("42");
        when(f24File.getSk()).thenReturn("Sk");
        when(f24File.getUpdated()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        F24FileEntity actualDtoToEntityResult = F24FileMapper.dtoToEntity(f24File);
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualDtoToEntityResult.getCreated());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals("00:00", actualDtoToEntityResult.getUpdated().toLocalTime().toString());
        assertEquals(F24FileStatusEntity.PROCESSING, actualDtoToEntityResult.getStatus());
        assertEquals("Sk", actualDtoToEntityResult.getSk());
        assertEquals("42", actualDtoToEntityResult.getRequestId());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        assertEquals("File Key", actualDtoToEntityResult.getFileKey());
        verify(f24File, atLeast(1)).getStatus();
        verify(f24File).getTtl();
        verify(f24File).getCreated();
        verify(f24File).getFileKey();
        verify(f24File).getPk();
        verify(f24File).getRequestId();
        verify(f24File).getSk();
        verify(f24File).getUpdated();
    }

    /**
     * Method under test: {@link F24FileMapper#dtoToEntity(F24File)}
     */
    @Test
    public void testDtoToEntity2() {
        F24File f24File = mock(F24File.class);
        when(f24File.getStatus()).thenReturn(null);
        when(f24File.getTtl()).thenReturn(1L);
        when(f24File.getCreated()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(f24File.getFileKey()).thenReturn("File Key");
        when(f24File.getPk()).thenReturn("Pk");
        when(f24File.getRequestId()).thenReturn("42");
        when(f24File.getSk()).thenReturn("Sk");
        when(f24File.getUpdated()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        F24FileEntity actualDtoToEntityResult = F24FileMapper.dtoToEntity(f24File);
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualDtoToEntityResult.getCreated());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals("00:00", actualDtoToEntityResult.getUpdated().toLocalTime().toString());
        assertNull(actualDtoToEntityResult.getStatus());
        assertEquals("Sk", actualDtoToEntityResult.getSk());
        assertEquals("42", actualDtoToEntityResult.getRequestId());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        assertEquals("File Key", actualDtoToEntityResult.getFileKey());
        verify(f24File).getStatus();
        verify(f24File).getTtl();
        verify(f24File).getCreated();
        verify(f24File).getFileKey();
        verify(f24File).getPk();
        verify(f24File).getRequestId();
        verify(f24File).getSk();
        verify(f24File).getUpdated();
    }

    /**
     * Method under test: {@link F24FileMapper#dtoToEntity(F24File)}
     */
    @Test
    public void testDtoToEntity3() {
        F24File f24File = mock(F24File.class);
        when(f24File.getStatus()).thenReturn(F24FileStatus.GENERATED);
        when(f24File.getTtl()).thenReturn(1L);
        when(f24File.getCreated()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(f24File.getFileKey()).thenReturn("File Key");
        when(f24File.getPk()).thenReturn("Pk");
        when(f24File.getRequestId()).thenReturn("42");
        when(f24File.getSk()).thenReturn("Sk");
        when(f24File.getUpdated()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        F24FileEntity actualDtoToEntityResult = F24FileMapper.dtoToEntity(f24File);
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualDtoToEntityResult.getCreated());
        assertEquals(1L, actualDtoToEntityResult.getTtl().longValue());
        assertEquals("00:00", actualDtoToEntityResult.getUpdated().toLocalTime().toString());
        assertEquals(F24FileStatusEntity.GENERATED, actualDtoToEntityResult.getStatus());
        assertEquals("Sk", actualDtoToEntityResult.getSk());
        assertEquals("42", actualDtoToEntityResult.getRequestId());
        assertEquals("Pk", actualDtoToEntityResult.getPk());
        assertEquals("File Key", actualDtoToEntityResult.getFileKey());
        verify(f24File, atLeast(1)).getStatus();
        verify(f24File).getTtl();
        verify(f24File).getCreated();
        verify(f24File).getFileKey();
        verify(f24File).getPk();
        verify(f24File).getRequestId();
        verify(f24File).getSk();
        verify(f24File).getUpdated();
    }
}

