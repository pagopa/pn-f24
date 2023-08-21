package it.pagopa.pn.f24.dto.safestorage;

import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileCreationRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileCreationWithContentRequest extends FileCreationRequest {
    private byte[] content;
}