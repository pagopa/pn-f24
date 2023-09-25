package it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataRefEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataSetStatusEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.dynamo.entity.F24MetadataValidationEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class F24MetadataSetMapper {
    public static F24MetadataSet entityToDto(F24MetadataSetEntity f24MetadataSetEntity) {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setPk(f24MetadataSetEntity.getPk());
        f24MetadataSet.setSetId(f24MetadataSetEntity.getSetId());
        f24MetadataSet.setCxId(f24MetadataSetEntity.getCxId());
        f24MetadataSet.setFileKeys(f24MetadataSetEntity.getFileKeys() != null ? convertEntityFileKeysToDto(f24MetadataSetEntity.getFileKeys()) : null);
        f24MetadataSet.setValidationEventSent(f24MetadataSetEntity.getValidationEventSent());
        f24MetadataSet.setHaveToSendValidationEvent(f24MetadataSetEntity.getHaveToSendValidationEvent());
        f24MetadataSet.setCreated(f24MetadataSetEntity.getCreated());
        f24MetadataSet.setUpdated(f24MetadataSetEntity.getUpdated());
        f24MetadataSet.setValidationResult(f24MetadataSetEntity.getValidationResult() != null ? convertEntityValidationResultToDto(f24MetadataSetEntity.getValidationResult()) : null);
        F24MetadataStatus status = f24MetadataSetEntity.getStatus() != null ? F24MetadataStatus.valueOf(f24MetadataSetEntity.getStatus().getValue()): null;
        f24MetadataSet.setStatus(status);
        f24MetadataSet.setSha256(f24MetadataSetEntity.getSha256());
        f24MetadataSet.setTtl(f24MetadataSetEntity.getTtl());
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

    private static List<F24MetadataValidationIssue> convertEntityValidationResultToDto(List<F24MetadataValidationEntity> validationResult) {
        return validationResult.stream()
                .map(
                        issue -> F24MetadataValidationIssue.builder()
                                .code(issue.getCode())
                                .element(issue.getElement())
                                .detail(issue.getDetail())
                                .build()

                )
                .toList();
    }

    public static F24MetadataSetEntity dtoToEntity(F24MetadataSet f24MetadataSet) {
        F24MetadataSetEntity f24MetadataSetEntity = new F24MetadataSetEntity(f24MetadataSet.getCxId(), f24MetadataSet.getSetId());
        f24MetadataSetEntity.setFileKeys(f24MetadataSet.getFileKeys() != null ? convertDtoFileKeysToEntity(f24MetadataSet.getFileKeys()) : null);
        f24MetadataSetEntity.setValidationEventSent(f24MetadataSet.getValidationEventSent());
        f24MetadataSetEntity.setHaveToSendValidationEvent(f24MetadataSet.getHaveToSendValidationEvent());
        f24MetadataSetEntity.setCreated(f24MetadataSet.getCreated());
        f24MetadataSetEntity.setUpdated(f24MetadataSet.getUpdated());
        f24MetadataSetEntity.setValidationResult(f24MetadataSet.getValidationResult() != null ? convertDtoValidationResultToEntity(f24MetadataSet.getValidationResult()) : null);
        F24MetadataSetStatusEntity status = f24MetadataSet.getStatus() != null ? F24MetadataSetStatusEntity.valueOf(f24MetadataSet.getStatus().getValue()): null;
        f24MetadataSetEntity.setStatus(status);
        f24MetadataSetEntity.setSha256(f24MetadataSet.getSha256());
        f24MetadataSetEntity.setTtl(f24MetadataSet.getTtl());
        return f24MetadataSetEntity;
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

    private static List<F24MetadataValidationEntity> convertDtoValidationResultToEntity(List<F24MetadataValidationIssue> validationResult) {
        if(validationResult == null) {
            return null;
        }
        return validationResult.stream()
                .map(
                    issue -> F24MetadataValidationEntity.builder()
                        .code(issue.getCode())
                        .element(issue.getElement())
                        .detail(issue.getDetail())
                        .build()

                )
                .toList();
    }
}
