package it.pagopa.pn.f24.dto.safestorage;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class FileCreationResponseInt {
    private String key;
}
