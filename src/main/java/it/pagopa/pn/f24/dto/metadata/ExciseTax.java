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
 * Excise Tax object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class ExciseTax   {

  @JsonProperty("institution")
  private String institution;

  @JsonProperty("province")
  private String province;

  @JsonProperty("id")
  private String id;

  @JsonProperty("taxType")
  private String taxType;

  @JsonProperty("installment")
  private String installment;

  @JsonProperty("month")
  private String month;

  @JsonProperty("year")
  private String year;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public ExciseTax institution(String institution) {
    this.institution = institution;
    return this;
  }

  /**
   * institution of the tax
   * @return institution
  */
  @Pattern(regexp = "^[A-Z]{2}$") 
  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public ExciseTax province(String province) {
    this.province = province;
    return this;
  }

  /**
   * province of the tax
   * @return province
  */
  @Pattern(regexp = "^[A-Z]{2}$") 
  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public ExciseTax id(String id) {
    this.id = id;
    return this;
  }

  /**
   * identification code
   * @return id
  */
  @Pattern(regexp = "^[A-Z0-9]{17}$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExciseTax taxType(String taxType) {
    this.taxType = taxType;
    return this;
  }

  /**
   * identification code of the type of tax
   * @return taxType
  */
  @Pattern(regexp = "^[0-9A-Z]{4}$") 
  public String getTaxType() {
    return taxType;
  }

  public void setTaxType(String taxType) {
    this.taxType = taxType;
  }

  public ExciseTax installment(String installment) {
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

  public ExciseTax month(String month) {
    this.month = month;
    return this;
  }

  /**
   * month reference
   * @return month
  */
  @Pattern(regexp = "^[0-1][0-9]$") 
  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public ExciseTax year(String year) {
    this.year = year;
    return this;
  }

  /**
   * reference year
   * @return year
  */
  @Pattern(regexp = "^[1-2][0-9]{3}$") 
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public ExciseTax debit(String debit) {
    this.debit = debit;
    return this;
  }

  /**
   * debit amount
   * @return debit
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getDebit() {
    return debit;
  }

  public void setDebit(String debit) {
    this.debit = debit;
  }

  public ExciseTax applyCost(Boolean applyCost) {
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
    ExciseTax exciseTax = (ExciseTax) o;
    return Objects.equals(this.institution, exciseTax.institution) &&
        Objects.equals(this.province, exciseTax.province) &&
        Objects.equals(this.id, exciseTax.id) &&
        Objects.equals(this.taxType, exciseTax.taxType) &&
        Objects.equals(this.installment, exciseTax.installment) &&
        Objects.equals(this.month, exciseTax.month) &&
        Objects.equals(this.year, exciseTax.year) &&
        Objects.equals(this.debit, exciseTax.debit) &&
        Objects.equals(this.applyCost, exciseTax.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(institution, province, id, taxType, installment, month, year, debit, applyCost);
  }
}
