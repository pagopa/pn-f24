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
 * Reporting Period (Sezione INPS) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class Period   {

  @JsonProperty("startDate")
  private String startDate;

  @JsonProperty("endDate")
  private String endDate;

  public Period startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * start date of the period
   * @return startDate
  */
  @Pattern(regexp = "^[0-9]{6}$") 
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public Period endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * end date of the period
   * @return endDate
  */
  @Pattern(regexp = "^[0-9]{6}$") 
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Period period = (Period) o;
    return Objects.equals(this.startDate, period.startDate) &&
        Objects.equals(this.endDate, period.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate);
  }
}
