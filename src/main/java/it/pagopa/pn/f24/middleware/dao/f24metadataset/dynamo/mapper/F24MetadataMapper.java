package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;

import java.util.HashMap;
import java.util.Map;

public class F24MetadataMapper {
    public static F24MetadataSet entityToDto(F24MetadataSetEntity f24MetadataSetEntity) {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setPk(f24MetadataSetEntity.getPk());
        f24MetadataSet.setFileKeys(convertEntityFileKeysToDto(f24MetadataSetEntity.getFileKeys()));
        f24MetadataSet.setValidationEventSent(f24MetadataSetEntity.getValidationEventSent());
        f24MetadataSet.setHaveToSendValidationEvent(f24MetadataSetEntity.getHaveToSendValidationEvent());
        f24MetadataSet.setCreated(f24MetadataSetEntity.getCreated());
        f24MetadataSet.setUpdated(f24MetadataSetEntity.getUpdated());
        F24MetadataStatus status = f24MetadataSetEntity.getStatus() != null ? F24MetadataStatus.valueOf(f24MetadataSetEntity.getStatus().getValue()): null;
        f24MetadataSet.setStatus(status);
        f24MetadataSet.setSha256(f24MetadataSetEntity.getSha256());
        return f24MetadataSet;
    }

    private static Map<String, F24MetadataRef> convertEntityFileKeysToDto(Map<String, F24MetadataRefEntity> fileKeys) {
        Map<String, F24MetadataRef> convertedFileKeys = new HashMap<>();
        fileKeys.forEach((s, f24MetadataRefEntity) -> {
            F24MetadataRef temp = new F24MetadataRef();
            temp.setFileKey(f24MetadataRefEntity.getFileKey());
            temp.setApplyCost(f24MetadataRefEntity.isApplyCost());
            temp.setSha256(f24MetadataRefEntity.getSha256());
            convertedFileKeys.put(s, temp);
        });

        return convertedFileKeys;
    }

    public static F24MetadataSetEntity dtoToEntity(F24MetadataSet f24MetadataSet) {
        return F24MetadataSetEntity.builder()
                .pk(f24MetadataSet.getPk())
                .fileKeys(convertDtoFileKeysToEntity(f24MetadataSet.getFileKeys()))
                .sha256(f24MetadataSet.getSha256())
                .status(f24MetadataSet.getStatus() != null ? F24MetadataSetStatusEntity.valueOf(f24MetadataSet.getStatus().getValue()): null)
                .haveToSendValidationEvent(f24MetadataSet.getHaveToSendValidationEvent())
                .validationEventSent(f24MetadataSet.getValidationEventSent())
                .created(f24MetadataSet.getCreated())
                .updated(f24MetadataSet.getUpdated())
                .build();
    }

    private static Map<String, F24MetadataRefEntity> convertDtoFileKeysToEntity(Map<String, F24MetadataRef> fileKeys) {
        Map<String, F24MetadataRefEntity> convertedFileKeys = new HashMap<>();
        fileKeys.forEach((s, f24MetadataItem) -> {
            F24MetadataRefEntity temp = new F24MetadataRefEntity();
            temp.setFileKey(f24MetadataItem.getFileKey());
            temp.setApplyCost(f24MetadataItem.isApplyCost());
            temp.setSha256(f24MetadataItem.getSha256());
            convertedFileKeys.put(s, temp);
        });

        return convertedFileKeys;
    }
}
