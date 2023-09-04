package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.dto.F24MetadataItem;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataItemEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataStatusEntity;

import java.util.HashMap;
import java.util.Map;

public class F24MetadataMapper {
    public static F24Metadata entityToDto(F24MetadataEntity f24MetadataEntity) {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setPk(f24MetadataEntity.getPk());
        f24Metadata.setFileKeys(convertEntityFileKeysToDto(f24MetadataEntity.getFileKeys()));
        f24Metadata.setValidationEventSent(f24MetadataEntity.getValidationEventSent());
        f24Metadata.setHaveToSendValidationEvent(f24MetadataEntity.getHaveToSendValidationEvent());
        f24Metadata.setCreated(f24MetadataEntity.getCreated());
        f24Metadata.setUpdated(f24MetadataEntity.getUpdated());
        F24MetadataStatus status = f24MetadataEntity.getStatus() != null ? F24MetadataStatus.valueOf(f24MetadataEntity.getStatus().getValue()): null;
        f24Metadata.setStatus(status);
        f24Metadata.setSha256(f24MetadataEntity.getSha256());
        return f24Metadata;
    }

    private static Map<String, F24MetadataItem> convertEntityFileKeysToDto(Map<String, F24MetadataItemEntity> fileKeys) {
        Map<String, F24MetadataItem> convertedFileKeys = new HashMap<>();
        fileKeys.forEach((s, f24MetadataItemEntity) -> {
            F24MetadataItem temp = new F24MetadataItem();
            temp.setFileKey(f24MetadataItemEntity.getFileKey());
            temp.setApplyCost(f24MetadataItemEntity.isApplyCost());
            temp.setSha256(f24MetadataItemEntity.getSha256());
            convertedFileKeys.put(s, temp);
        });

        return convertedFileKeys;
    }

    public static F24MetadataEntity dtoToEntity(F24Metadata f24Metadata) {
        return F24MetadataEntity.builder()
                .pk(f24Metadata.getPk())
                .fileKeys(convertDtoFileKeysToEntity(f24Metadata.getFileKeys()))
                .sha256(f24Metadata.getSha256())
                .status(f24Metadata.getStatus() != null ? F24MetadataStatusEntity.valueOf(f24Metadata.getStatus().getValue()): null)
                .haveToSendValidationEvent(f24Metadata.getHaveToSendValidationEvent())
                .validationEventSent(f24Metadata.getValidationEventSent())
                .created(f24Metadata.getCreated())
                .updated(f24Metadata.getUpdated())
                .build();
    }

    private static Map<String, F24MetadataItemEntity> convertDtoFileKeysToEntity(Map<String, F24MetadataItem> fileKeys) {
        Map<String, F24MetadataItemEntity> convertedFileKeys = new HashMap<>();
        fileKeys.forEach((s, f24MetadataItem) -> {
            F24MetadataItemEntity temp = new F24MetadataItemEntity();
            temp.setFileKey(f24MetadataItem.getFileKey());
            temp.setApplyCost(f24MetadataItem.isApplyCost());
            temp.setSha256(f24MetadataItem.getSha256());
            convertedFileKeys.put(s, temp);
        });

        return convertedFileKeys;
    }
}
