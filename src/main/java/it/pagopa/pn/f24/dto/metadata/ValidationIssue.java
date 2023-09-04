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
 * Un errore di validazione dei metadati F24
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class ValidationIssue   {

  @JsonProperty("code")
  private String code;

  @JsonProperty("element")
  private String element;

  @JsonProperty("detail")
  private String detail;

  public ValidationIssue code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Internal code of the error, in human-readable format
   * @return code
  */
  @NotNull 
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ValidationIssue element(String element) {
    this.element = element;
    return this;
  }

  /**
   * Parameter or request body field name for validation error
   * @return element
  */
  
  public String getElement() {
    return element;
  }

  public void setElement(String element) {
    this.element = element;
  }

  public ValidationIssue detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * A human readable explanation specific to this occurrence of the problem.
   * @return detail
  */
  @Size(max = 1024) 
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationIssue validationIssue = (ValidationIssue) o;
    return Objects.equals(this.code, validationIssue.code) &&
        Objects.equals(this.element, validationIssue.element) &&
        Objects.equals(this.detail, validationIssue.detail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, element, detail);
  }
}
