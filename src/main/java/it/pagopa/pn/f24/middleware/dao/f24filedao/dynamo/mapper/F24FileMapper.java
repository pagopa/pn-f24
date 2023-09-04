package it.pagopa.pn.f24.middleware.dao.f24filedao.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.middleware.dao.f24filedao.dynamo.entity.F24FileEntity;

public class F24FileMapper {

    public static F24File entityToDto(F24FileEntity f24FileEntity){
        F24File f24File = new F24File();
        f24File.setPk(f24FileEntity.getPk());
        f24File.setCreated(f24FileEntity.getCreated());
        f24File.setSk(f24FileEntity.getSk());
        f24File.setRequestId(f24FileEntity.getRequestId());
        f24File.setStatus(f24FileEntity.getStatus());
        f24File.setFileKey(f24FileEntity.getFileKey());
        f24File.setTtl(f24FileEntity.getTtl());
        f24File.setUpdated(f24FileEntity.getUpdated());
        return f24File;
    }

    public static F24FileEntity dtoToEntity(F24File f24File){
        return F24FileEntity.builder()
                .pk(f24File.getPk())
                .created((f24File.getCreated()))
                .sk(f24File.getSk())
                .requestId(f24File.getRequestId())
                .status(f24File.getStatus())
                .fileKey(f24File.getFileKey())
                .ttl(f24File.getTtl())
                .updated(f24File.getUpdated())
                .build();
    }
}
