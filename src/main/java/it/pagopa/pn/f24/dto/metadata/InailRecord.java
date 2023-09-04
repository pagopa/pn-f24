package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.IncludeNotificationCost;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * INAIL Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class InailRecord   {

  @JsonProperty("office")
  private String office;

  @JsonProperty("company")
  private String company;

  @JsonProperty("control")
  private String control;

  @JsonProperty("refNumber")
  private String refNumber;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("credit")
  private String credit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public InailRecord office(String office) {
    this.office = office;
    return this;
  }

  /**
   * identification code of the office
   * @return office
  */
  @Pattern(regexp = "^[0-9]{5}$") 
  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  public InailRecord company(String company) {
    this.company = company;
    return this;
  }

  /**
   * identification code of the company
   * @return company
  */
  @Pattern(regexp = "^[0-9]{8}$") 
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public InailRecord control(String control) {
    this.control = control;
    return this;
  }

  /**
   * control identification code
   * @return control
  */
  @Pattern(regexp = "^[0-9]{2}$") 
  public String getControl() {
    return control;
  }

  public void setControl(String control) {
    this.control = control;
  }

  public InailRecord refNumber(String refNumber) {
    this.refNumber = refNumber;
    return this;
  }

  /**
   * reference number
   * @return refNumber
  */
  @Pattern(regexp = "^[0-9]{6}$") 
  public String getRefNumber() {
    return refNumber;
  }

  public void setRefNumber(String refNumber) {
    this.refNumber = refNumber;
  }

  public InailRecord reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * reason of the record
   * @return reason
  */
  @Pattern(regexp = "^[A-Z0-9]$") 
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public InailRecord debit(String debit) {
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

  public InailRecord credit(String credit) {
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

  public InailRecord applyCost(Boolean applyCost) {
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
    InailRecord inailRecord = (InailRecord) o;
    return Objects.equals(this.office, inailRecord.office) &&
        Objects.equals(this.company, inailRecord.company) &&
        Objects.equals(this.control, inailRecord.control) &&
        Objects.equals(this.refNumber, inailRecord.refNumber) &&
        Objects.equals(this.reason, inailRecord.reason) &&
        Objects.equals(this.debit, inailRecord.debit) &&
        Objects.equals(this.credit, inailRecord.credit) &&
        Objects.equals(this.applyCost, inailRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(office, company, control, refNumber, reason, debit, credit, applyCost);
  }
}
