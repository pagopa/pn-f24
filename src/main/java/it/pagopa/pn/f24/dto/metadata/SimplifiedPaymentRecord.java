package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.LocalTaxRecord;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Payment Reason Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class SimplifiedPaymentRecord   {

  @JsonProperty("section")
  private String section;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public SimplifiedPaymentRecord section(String section) {
    this.section = section;
    return this;
  }

  /**
   * section code (ER|RG|EL)
   * @return section
  */
  @NotNull @Pattern(regexp = "^(ER|RG|EL)$") 
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public SimplifiedPaymentRecord applyCost(Boolean applyCost) {
    this.applyCost = applyCost;
    return this;
  }

  /**
   * to check if include notification cost
   * @return applyCost
  */
  @NotNull 
  public Boolean getApplyCost() {
    return applyCost;
  }

  public void setApplyCost(Boolean applyCost) {
    this.applyCost = applyCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimplifiedPaymentRecord simplifiedPaymentRecord = (SimplifiedPaymentRecord) o;
    return Objects.equals(this.section, simplifiedPaymentRecord.section) &&
        Objects.equals(this.applyCost, simplifiedPaymentRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(section, applyCost);
  }
}
