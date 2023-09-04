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
 * Tax object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class Tax   {

  @JsonProperty("taxType")
  private String taxType;

  @JsonProperty("installment")
  private String installment;

  @JsonProperty("year")
  private String year;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("credit")
  private String credit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public Tax taxType(String taxType) {
    this.taxType = taxType;
    return this;
  }

  /**
   * identification code of the type of tax
   * @return taxType
  */
  @Pattern(regexp = "^[A-Z0-9]{4}$") 
  public String getTaxType() {
    return taxType;
  }

  public void setTaxType(String taxType) {
    this.taxType = taxType;
  }

  public Tax installment(String installment) {
    this.installment = installment;
    return this;
  }

  /**
   * identification code of the ente
   * @return installment
  */
  @Pattern(regexp = "^[A-Z0-9]{0,4}$") 
  public String getInstallment() {
    return installment;
  }

  public void setInstallment(String installment) {
    this.installment = installment;
  }

  public Tax year(String year) {
    this.year = year;
    return this;
  }

  /**
   * reference year of the tax
   * @return year
  */
  @Pattern(regexp = "^[1-2][0-9]{3}$") 
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Tax debit(String debit) {
    this.debit = debit;
    return this;
  }

  /**
   * debit amount of the tax
   * @return debit
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getDebit() {
    return debit;
  }

  public void setDebit(String debit) {
    this.debit = debit;
  }

  public Tax credit(String credit) {
    this.credit = credit;
    return this;
  }

  /**
   * credit amount of the tax
   * @return credit
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getCredit() {
    return credit;
  }

  public void setCredit(String credit) {
    this.credit = credit;
  }

  public Tax applyCost(Boolean applyCost) {
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
    Tax tax = (Tax) o;
    return Objects.equals(this.taxType, tax.taxType) &&
        Objects.equals(this.installment, tax.installment) &&
        Objects.equals(this.year, tax.year) &&
        Objects.equals(this.debit, tax.debit) &&
        Objects.equals(this.credit, tax.credit) &&
        Objects.equals(this.applyCost, tax.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxType, installment, year, debit, credit, applyCost);
  }
}
