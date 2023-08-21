package it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.mapper;

import it.pagopa.pn.f24.dto.F24Metadata;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataEntity;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.dynamo.entity.F24MetadataStatusEntity;

public class F24MetadataMapper {
    public static F24Metadata entityToDto(F24MetadataEntity f24MetadataEntity) {
        F24Metadata f24Metadata = new F24Metadata();
        f24Metadata.setSetId(f24MetadataEntity.getSetId());
        f24Metadata.setCxId(f24MetadataEntity.getCxId());
        f24Metadata.setFileKey(f24MetadataEntity.getFileKey());
        f24Metadata.setValidationEventSent(f24MetadataEntity.getValidationEventSent());
        f24Metadata.setHaveToSendValidationEvent(f24MetadataEntity.getHaveToSendValidationEvent());
        f24Metadata.setCreated(f24MetadataEntity.getCreated());
        f24Metadata.setUpdated(f24MetadataEntity.getUpdated());
        F24MetadataStatus status = f24MetadataEntity.getStatus() != null ? F24MetadataStatus.valueOf(f24MetadataEntity.getStatus().getValue()): null;
        f24Metadata.setStatus(status);
        f24Metadata.setSha256(f24MetadataEntity.getSha256());
        return f24Metadata;
    }

    public static F24MetadataEntity dtoToEntity(F24Metadata f24Metadata) {
        return F24MetadataEntity.builder()
                .setId(f24Metadata.getSetId())
                .cxId(f24Metadata.getCxId())
                .fileKey(f24Metadata.getFileKey())
                .sha256(f24Metadata.getSha256())
                .status(f24Metadata.getStatus() != null ? F24MetadataStatusEntity.valueOf(f24Metadata.getStatus().getValue()): null)
                .haveToSendValidationEvent(f24Metadata.getHaveToSendValidationEvent())
                .validationEventSent(f24Metadata.getValidationEventSent())
                .created(f24Metadata.getCreated())
                .updated(f24Metadata.getUpdated())
                .build();
    }
}
