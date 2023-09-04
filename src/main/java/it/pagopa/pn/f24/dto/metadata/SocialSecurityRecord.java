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
 * Social Security Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class SocialSecurityRecord   {

  @JsonProperty("institution")
  private String institution;

  @JsonProperty("office")
  private String office;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("position")
  private String position;

  @JsonProperty("period")
  private Period period;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("credit")
  private String credit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public SocialSecurityRecord institution(String institution) {
    this.institution = institution;
    return this;
  }

  /**
   * identification code of the institution
   * @return institution
  */
  @Pattern(regexp = "^[0-9]{4}$") 
  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public SocialSecurityRecord office(String office) {
    this.office = office;
    return this;
  }

  /**
   * identification code of the office
   * @return office
  */
  @Pattern(regexp = "^[0-9A-Z]{5}$") 
  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  public SocialSecurityRecord reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * reason of the contribution
   * @return reason
  */
  @Pattern(regexp = "^[0-9A-Z]{3,4}$") 
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public SocialSecurityRecord position(String position) {
    this.position = position;
    return this;
  }

  /**
   * identification code of the position
   * @return position
  */
  @Pattern(regexp = "^[0-9]{9}$") 
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public SocialSecurityRecord period(Period period) {
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

  public SocialSecurityRecord debit(String debit) {
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

  public SocialSecurityRecord credit(String credit) {
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

  public SocialSecurityRecord applyCost(Boolean applyCost) {
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
    SocialSecurityRecord socialSecurityRecord = (SocialSecurityRecord) o;
    return Objects.equals(this.institution, socialSecurityRecord.institution) &&
        Objects.equals(this.office, socialSecurityRecord.office) &&
        Objects.equals(this.reason, socialSecurityRecord.reason) &&
        Objects.equals(this.position, socialSecurityRecord.position) &&
        Objects.equals(this.period, socialSecurityRecord.period) &&
        Objects.equals(this.debit, socialSecurityRecord.debit) &&
        Objects.equals(this.credit, socialSecurityRecord.credit) &&
        Objects.equals(this.applyCost, socialSecurityRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(institution, office, reason, position, period, debit, credit, applyCost);
  }
}
