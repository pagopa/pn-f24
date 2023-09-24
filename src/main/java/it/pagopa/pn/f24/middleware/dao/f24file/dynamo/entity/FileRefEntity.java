package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import it.pagopa.pn.f24.dto.F24FileStatus;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileRefEntity {
    private String fileKey;
    private F24FileStatus status;
}
