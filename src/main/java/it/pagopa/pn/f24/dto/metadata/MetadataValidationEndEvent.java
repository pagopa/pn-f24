package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.ValidationIssue;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Il risultato di una validazione F24
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class MetadataValidationEndEvent   {

  @JsonProperty("setId")
  private String setId;

  @JsonProperty("status")
  private String status;

  @JsonProperty("errors")
  @Valid
  private List<ValidationIssue> errors = null;

  public MetadataValidationEndEvent setId(String setId) {
    this.setId = setId;
    return this;
  }

  /**
   * Identificativo SetId della richiesta. E' lo stesso usato nel path del metodo
   * @return setId
  */
  @NotNull 
  public String getSetId() {
    return setId;
  }

  public void setSetId(String setId) {
    this.setId = setId;
  }

  public MetadataValidationEndEvent status(String status) {
    this.status = status;
    return this;
  }

  /**
   * - OK se tutto va bene - KO in caso di problemi
   * @return status
  */
  @NotNull 
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public MetadataValidationEndEvent errors(List<ValidationIssue> errors) {
    this.errors = errors;
    return this;
  }

  public MetadataValidationEndEvent addErrorsItem(ValidationIssue errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }

  /**
   * Get errors
   * @return errors
  */
  @Valid @Size(max = 100) 
  public List<ValidationIssue> getErrors() {
    return errors;
  }

  public void setErrors(List<ValidationIssue> errors) {
    this.errors = errors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataValidationEndEvent metadataValidationEndEvent = (MetadataValidationEndEvent) o;
    return Objects.equals(this.setId, metadataValidationEndEvent.setId) &&
        Objects.equals(this.status, metadataValidationEndEvent.status) &&
        Objects.equals(this.errors, metadataValidationEndEvent.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(setId, status, errors);
  }
}
