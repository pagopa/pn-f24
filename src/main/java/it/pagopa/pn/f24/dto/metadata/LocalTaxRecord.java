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
 * LocalTax Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class LocalTaxRecord   {

  @JsonProperty("municipality")
  private String municipality;

  @JsonProperty("reconsideration")
  private Boolean reconsideration;

  @JsonProperty("propertiesChanges")
  private Boolean propertiesChanges;

  @JsonProperty("advancePayment")
  private Boolean advancePayment;

  @JsonProperty("fullPayment")
  private Boolean fullPayment;

  @JsonProperty("numberOfProperties")
  private String numberOfProperties;

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

  public LocalTaxRecord municipality(String municipality) {
    this.municipality = municipality;
    return this;
  }

  /**
   * identification code of the municipality
   * @return municipality
  */
  @NotNull @Pattern(regexp = "^[0-9A-Z]{4}$") 
  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public LocalTaxRecord reconsideration(Boolean reconsideration) {
    this.reconsideration = reconsideration;
    return this;
  }

  /**
   * to check if it is a reconsideration act
   * @return reconsideration
  */
  
  public Boolean getReconsideration() {
    return reconsideration;
  }

  public void setReconsideration(Boolean reconsideration) {
    this.reconsideration = reconsideration;
  }

  public LocalTaxRecord propertiesChanges(Boolean propertiesChanges) {
    this.propertiesChanges = propertiesChanges;
    return this;
  }

  /**
   * to check if there are some changes in properties list
   * @return propertiesChanges
  */
  
  public Boolean getPropertiesChanges() {
    return propertiesChanges;
  }

  public void setPropertiesChanges(Boolean propertiesChanges) {
    this.propertiesChanges = propertiesChanges;
  }

  public LocalTaxRecord advancePayment(Boolean advancePayment) {
    this.advancePayment = advancePayment;
    return this;
  }

  /**
   * to check if it is a payment in advance
   * @return advancePayment
  */
  
  public Boolean getAdvancePayment() {
    return advancePayment;
  }

  public void setAdvancePayment(Boolean advancePayment) {
    this.advancePayment = advancePayment;
  }

  public LocalTaxRecord fullPayment(Boolean fullPayment) {
    this.fullPayment = fullPayment;
    return this;
  }

  /**
   * to check if it a full payment
   * @return fullPayment
  */
  
  public Boolean getFullPayment() {
    return fullPayment;
  }

  public void setFullPayment(Boolean fullPayment) {
    this.fullPayment = fullPayment;
  }

  public LocalTaxRecord numberOfProperties(String numberOfProperties) {
    this.numberOfProperties = numberOfProperties;
    return this;
  }

  /**
   * number of properties
   * @return numberOfProperties
  */
  @Pattern(regexp = "^[0-9]{3}$") 
  public String getNumberOfProperties() {
    return numberOfProperties;
  }

  public void setNumberOfProperties(String numberOfProperties) {
    this.numberOfProperties = numberOfProperties;
  }

  public LocalTaxRecord taxType(String taxType) {
    this.taxType = taxType;
    return this;
  }

  /**
   * identification code of the type of tax
   * @return taxType
  */
  @NotNull @Pattern(regexp = "^[0-9]{4}$") 
  public String getTaxType() {
    return taxType;
  }

  public void setTaxType(String taxType) {
    this.taxType = taxType;
  }

  public LocalTaxRecord installment(String installment) {
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

  public LocalTaxRecord year(String year) {
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

  public LocalTaxRecord debit(String debit) {
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

  public LocalTaxRecord credit(String credit) {
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

  public LocalTaxRecord applyCost(Boolean applyCost) {
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
    LocalTaxRecord localTaxRecord = (LocalTaxRecord) o;
    return Objects.equals(this.municipality, localTaxRecord.municipality) &&
        Objects.equals(this.reconsideration, localTaxRecord.reconsideration) &&
        Objects.equals(this.propertiesChanges, localTaxRecord.propertiesChanges) &&
        Objects.equals(this.advancePayment, localTaxRecord.advancePayment) &&
        Objects.equals(this.fullPayment, localTaxRecord.fullPayment) &&
        Objects.equals(this.numberOfProperties, localTaxRecord.numberOfProperties) &&
        Objects.equals(this.taxType, localTaxRecord.taxType) &&
        Objects.equals(this.installment, localTaxRecord.installment) &&
        Objects.equals(this.year, localTaxRecord.year) &&
        Objects.equals(this.debit, localTaxRecord.debit) &&
        Objects.equals(this.credit, localTaxRecord.credit) &&
        Objects.equals(this.applyCost, localTaxRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(municipality, reconsideration, propertiesChanges, advancePayment, fullPayment, numberOfProperties, taxType, installment, year, debit, credit, applyCost);
  }
}
