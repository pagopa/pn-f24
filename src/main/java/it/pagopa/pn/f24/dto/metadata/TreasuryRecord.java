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
 * Treasury Record object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TreasuryRecord   {

  @JsonProperty("type")
  private String type;

  @JsonProperty("id")
  private String id;

  @JsonProperty("taxType")
  private String taxType;

  @JsonProperty("year")
  private String year;

  @JsonProperty("debit")
  private String debit;

  @JsonProperty("applyCost")
  private Boolean applyCost;

  public TreasuryRecord type(String type) {
    this.type = type;
    return this;
  }

  /**
   * type of treasury
   * @return type
  */
  @Pattern(regexp = "^[A-Z]$") 
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public TreasuryRecord id(String id) {
    this.id = id;
    return this;
  }

  /**
   * identification code of the element
   * @return id
  */
  @Pattern(regexp = "^[A-Z0-9]{17}$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TreasuryRecord taxType(String taxType) {
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

  public TreasuryRecord year(String year) {
    this.year = year;
    return this;
  }

  /**
   * referance year
   * @return year
  */
  @Pattern(regexp = "^[1-2][0-9]{3}$") 
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public TreasuryRecord debit(String debit) {
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

  public TreasuryRecord applyCost(Boolean applyCost) {
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
    TreasuryRecord treasuryRecord = (TreasuryRecord) o;
    return Objects.equals(this.type, treasuryRecord.type) &&
        Objects.equals(this.id, treasuryRecord.id) &&
        Objects.equals(this.taxType, treasuryRecord.taxType) &&
        Objects.equals(this.year, treasuryRecord.year) &&
        Objects.equals(this.debit, treasuryRecord.debit) &&
        Objects.equals(this.applyCost, treasuryRecord.applyCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, id, taxType, year, debit, applyCost);
  }
}
