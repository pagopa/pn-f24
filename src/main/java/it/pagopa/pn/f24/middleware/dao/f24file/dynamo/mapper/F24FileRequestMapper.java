package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24Request;
import it.pagopa.pn.f24.dto.F24RequestStatus;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24FileRequestEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.F24RequestStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity.FileKeyEntity;

import java.util.Map;
import java.util.stream.Collectors;

public class F24FileRequestMapper {
    private F24FileRequestMapper() { }
    public static F24Request entityToDto(F24FileRequestEntity f24FileRequestEntity){
        F24Request f24Request = new F24Request();
        f24Request.setPk(f24FileRequestEntity.getPk());
        f24Request.setCxId(f24FileRequestEntity.getCxId());
        f24Request.setRequestId(f24FileRequestEntity.getRequestId());
        f24Request.setFiles(convertEntityFilesToDto(f24FileRequestEntity.getFiles()));
        f24Request.setSetId(f24FileRequestEntity.getSetId());
        f24Request.setCost(f24FileRequestEntity.getCost());
        f24Request.setStatus(f24FileRequestEntity.getStatus() != null ? F24RequestStatus.valueOf(f24FileRequestEntity.getStatus().getValue()) : null);
        f24Request.setPathTokens(f24FileRequestEntity.getPathTokens());
        f24Request.setCreated(f24FileRequestEntity.getCreated());
        f24Request.setTtl(f24FileRequestEntity.getTtl());
        return f24Request;
    }

    private static Map<String, F24Request.FileKey> convertEntityFilesToDto(Map<String, FileKeyEntity> files) {
        return files.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new F24Request.FileKey(entry.getValue().getFileKey())
                ));
    }

    public static F24FileRequestEntity dtoToEntity(F24Request f24Request){
        F24FileRequestEntity f24FileRequestEntity = new F24FileRequestEntity(f24Request.getCxId(), f24Request.getRequestId());
        f24FileRequestEntity.setFiles(convertDtoFilesToEntity(f24Request.getFiles()));
        f24FileRequestEntity.setSetId(f24Request.getSetId());
        f24FileRequestEntity.setCost(f24Request.getCost());
        f24FileRequestEntity.setStatus(f24Request.getStatus() != null ? F24RequestStatusEntity.valueOf(f24Request.getStatus().getValue()) : null);
        f24FileRequestEntity.setPathTokens(f24Request.getPathTokens());
        f24FileRequestEntity.setCreated(f24Request.getCreated());
        f24FileRequestEntity.setTtl(f24Request.getTtl());
        return f24FileRequestEntity;
    }

    private static Map<String, FileKeyEntity> convertDtoFilesToEntity(Map<String, F24Request.FileKey> files) {
        return files.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new FileKeyEntity(entry.getValue().getFileKey())
                ));
    }
}
