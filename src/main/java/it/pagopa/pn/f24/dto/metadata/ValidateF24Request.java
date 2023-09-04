package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * ValidateF24Request
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class ValidateF24Request   {

  @JsonProperty("setId")
  private String setId;

  public ValidateF24Request setId(String setId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidateF24Request validateF24Request = (ValidateF24Request) o;
    return Objects.equals(this.setId, validateF24Request.setId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(setId);
  }
}
