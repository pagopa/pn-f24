package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;



import it.pagopa.pn.f24.dto.F24File;
import it.pagopa.pn.f24.dto.F24FileStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileStatusEntity;

public class F24FileMapper {

    public static F24File entityToDto(F24FileEntity f24FileEntity){
        return F24File.builder()
                .pk(f24FileEntity.getPk())
                .created((f24FileEntity.getCreated()))
                .sk(f24FileEntity.getSk())
                .requestId(f24FileEntity.getRequestId())
                .status(f24FileEntity.getStatus() != null ? F24FileStatus.valueOf(f24FileEntity.getStatus().getValue()) : null)
                .fileKey(f24FileEntity.getFileKey())
                .ttl(f24FileEntity.getTtl())
                .updated(f24FileEntity.getUpdated())
                .build();
    }


    public static F24FileEntity dtoToEntity(F24File f24File){
        return F24FileEntity.builder()
                .pk(f24File.getPk())
                .created((f24File.getCreated()))
                .sk(f24File.getSk())
                .requestId(f24File.getRequestId())
                .status(f24File.getStatus() != null ? F24FileStatusEntity.valueOf(f24File.getStatus().getValue()) : null)
                .fileKey(f24File.getFileKey())
                .ttl(f24File.getTtl())
                .updated(f24File.getUpdated())
                .build();
    }
}
