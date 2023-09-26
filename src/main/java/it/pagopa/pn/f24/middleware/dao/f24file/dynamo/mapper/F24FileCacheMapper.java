package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileCacheEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;

public class F24FileCacheMapper {

    public static F24File entityToDto(F24FileCacheEntity f24FileCacheEntity){
        F24File f24File = new F24File();
        f24File.setPk(f24FileCacheEntity.getPk());
        f24File.setCxId(f24FileCacheEntity.getCxId());
        f24File.setSetId(f24FileCacheEntity.getSetId());
        f24File.setCost(f24FileCacheEntity.getCost());
        f24File.setPathTokens(f24FileCacheEntity.getPathTokens());
        f24File.setStatus(f24FileCacheEntity.getStatus() != null ? F24FileStatus.valueOf(f24FileCacheEntity.getStatus().getValue()) : null);
        f24File.setFileKey(f24FileCacheEntity.getFileKey());
        f24File.setRequestIds(f24FileCacheEntity.getRequestIds());
        f24File.setTtl(f24FileCacheEntity.getTtl());
        f24File.setCreated(f24FileCacheEntity.getCreated());
        f24File.setUpdated(f24FileCacheEntity.getUpdated());
        return f24File;
    }

    public static F24FileCacheEntity dtoToEntity(F24File f24File){
        F24FileCacheEntity f24FileCacheEntity = new F24FileCacheEntity(f24File.getCxId(), f24File.getSetId(), f24File.getCost(), f24File.getPathTokens());
        f24FileCacheEntity.setStatus(f24File.getStatus() != null ? F24FileStatusEntity.valueOf(f24File.getStatus().getValue()) : null);
        f24FileCacheEntity.setFileKey(f24File.getFileKey());
        f24FileCacheEntity.setRequestIds(f24File.getRequestIds());
        f24FileCacheEntity.setTtl(f24File.getTtl());
        f24FileCacheEntity.setCreated(f24File.getCreated());
        f24FileCacheEntity.setUpdated(f24File.getUpdated());
        return f24FileCacheEntity;
    }
}
