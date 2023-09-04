package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.IncludeNotificationCost;
import it.pagopa.pn.f24.dto.metadata.Period;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * INPS Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class InpsRecord   {

  @JsonProperty("office")
  private String office;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("inps")
  private String inps;

  @JsonProperty("period")
  private Period period;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("credit")
  private String credit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public InpsRecord office(String office) {
    this.office = office;
    return this;
  }

  /**
   * identification code of the office
   * @return office
  */
  @Pattern(regexp = "^[0-9]{3,4}$") 
  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  public InpsRecord reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * contribution reason for the record
   * @return reason
  */
  @Pattern(regexp = "^[A-Z-]{3,4}$") 
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public InpsRecord inps(String inps) {
    this.inps = inps;
    return this;
  }

  /**
   * INPS identification code
   * @return inps
  */
  @Pattern(regexp = "^[A-Za-z0-9\\s]{0,17}$") 
  public String getInps() {
    return inps;
  }

  public void setInps(String inps) {
    this.inps = inps;
  }

  public InpsRecord period(Period period) {
    this.period = period;
    return this;
  }

  /**
   * Get period
   * @return period
  */
  @Valid 
  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public InpsRecord debit(String debit) {
    this.debit = debit;
    return this;
  }

  /**
   * debit amount of the record
   * @return debit
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getDebit() {
    return debit;
  }

  public void setDebit(String debit) {
    this.debit = debit;
  }

  public InpsRecord credit(String credit) {
    this.credit = credit;
    return this;
  }

  /**
   * credit amount of the record
   * @return credit
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getCredit() {
    return credit;
  }

  public void setCredit(String credit) {
    this.credit = credit;
  }

  public InpsRecord applyCost(Boolean applyCost) {
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
    InpsRecord inpsRecord = (InpsRecord) o;
    return Objects.equals(this.office, inpsRecord.office) &&
        Objects.equals(this.reason, inpsRecord.reason) &&
        Objects.equals(this.inps, inpsRecord.inps) &&
        Objects.equals(this.period, inpsRecord.period) &&
        Objects.equals(this.debit, inpsRecord.debit) &&
        Objects.equals(this.credit, inpsRecord.credit) &&
        Objects.equals(this.applyCost, inpsRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(office, reason, inps, period, debit, credit, applyCost);
  }
}
