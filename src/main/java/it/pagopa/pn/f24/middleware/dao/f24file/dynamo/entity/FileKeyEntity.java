package it.pagopa.pn.f24.middleware.dao.f24file.dynamo.entity;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileKeyEntity {
    public String fileKey;
}
