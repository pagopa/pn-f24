package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.PersonalData;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * TaxPayerSimplified
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TaxPayerSimplified   {

  @JsonProperty("taxCode")
  private String taxCode;

  @JsonProperty("personalData")
  private PersonalData personalData;

  @JsonProperty("relativePersonTaxCode")
  private String relativePersonTaxCode;

  @JsonProperty("id")
  private String id;

  @JsonProperty("document")
  private String document;

  @JsonProperty("office")
  private String office;

  public TaxPayerSimplified taxCode(String taxCode) {
    this.taxCode = taxCode;
    return this;
  }

  /**
   * Tax Payer Tax Code
   * @return taxCode
  */
  @Pattern(regexp = "^([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST]{1}[0-9LMNPQRSTUV]{2}[A-Z]{1}[0-9LMNPQRSTUV]{3}[A-Z]{1})|([0-9]{11})$") 
  public String getTaxCode() {
    return taxCode;
  }

  public void setTaxCode(String taxCode) {
    this.taxCode = taxCode;
  }

  public TaxPayerSimplified personalData(PersonalData personalData) {
    this.personalData = personalData;
    return this;
  }

  /**
   * Get personalData
   * @return personalData
  */
  @Valid 
  public PersonalData getPersonalData() {
    return personalData;
  }

  public void setPersonalData(PersonalData personalData) {
    this.personalData = personalData;
  }

  public TaxPayerSimplified relativePersonTaxCode(String relativePersonTaxCode) {
    this.relativePersonTaxCode = relativePersonTaxCode;
    return this;
  }

  /**
   * It is the tax code of a relative of the main tax payer
   * @return relativePersonTaxCode
  */
  @Pattern(regexp = "^([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST]{1}[0-9LMNPQRSTUV]{2}[A-Z]{1}[0-9LMNPQRSTUV]{3}[A-Z]{1})|([0-9]{11})$") 
  public String getRelativePersonTaxCode() {
    return relativePersonTaxCode;
  }

  public void setRelativePersonTaxCode(String relativePersonTaxCode) {
    this.relativePersonTaxCode = relativePersonTaxCode;
  }

  public TaxPayerSimplified id(String id) {
    this.id = id;
    return this;
  }

  /**
   * identification code
   * @return id
  */
  @Pattern(regexp = "^[A-Z0-9]{2}$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TaxPayerSimplified document(String document) {
    this.document = document;
    return this;
  }

  /**
   * identification code of the document
   * @return document
  */
  @Pattern(regexp = "^\\d{11}$") 
  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public TaxPayerSimplified office(String office) {
    this.office = office;
    return this;
  }

  /**
   * identification code of the office
   * @return office
  */
  @Pattern(regexp = "^[A-Z0-9]{3}$") 
  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxPayerSimplified taxPayerSimplified = (TaxPayerSimplified) o;
    return Objects.equals(this.taxCode, taxPayerSimplified.taxCode) &&
        Objects.equals(this.personalData, taxPayerSimplified.personalData) &&
        Objects.equals(this.relativePersonTaxCode, taxPayerSimplified.relativePersonTaxCode) &&
        Objects.equals(this.id, taxPayerSimplified.id) &&
        Objects.equals(this.document, taxPayerSimplified.document) &&
        Objects.equals(this.office, taxPayerSimplified.office);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxCode, personalData, relativePersonTaxCode, id, document, office);
  }
}
