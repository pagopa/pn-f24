package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.CompanyData;
import it.pagopa.pn.f24.dto.metadata.PersonData;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * TaxPayerElide
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TaxPayerElide   {

  @JsonProperty("taxCode")
  private String taxCode;

  @JsonProperty("person")
  private PersonData person;

  @JsonProperty("company")
  private CompanyData company;

  @JsonProperty("relativePersonTaxCode")
  private String relativePersonTaxCode;

  @JsonProperty("id")
  private String id;

  public TaxPayerElide taxCode(String taxCode) {
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

  public TaxPayerElide person(PersonData person) {
    this.person = person;
    return this;
  }

  /**
   * Get person
   * @return person
  */
  @Valid 
  public PersonData getPerson() {
    return person;
  }

  public void setPerson(PersonData person) {
    this.person = person;
  }

  public TaxPayerElide company(CompanyData company) {
    this.company = company;
    return this;
  }

  /**
   * Get company
   * @return company
  */
  @Valid 
  public CompanyData getCompany() {
    return company;
  }

  public void setCompany(CompanyData company) {
    this.company = company;
  }

  public TaxPayerElide relativePersonTaxCode(String relativePersonTaxCode) {
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

  public TaxPayerElide id(String id) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxPayerElide taxPayerElide = (TaxPayerElide) o;
    return Objects.equals(this.taxCode, taxPayerElide.taxCode) &&
        Objects.equals(this.person, taxPayerElide.person) &&
        Objects.equals(this.company, taxPayerElide.company) &&
        Objects.equals(this.relativePersonTaxCode, taxPayerElide.relativePersonTaxCode) &&
        Objects.equals(this.id, taxPayerElide.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxCode, person, company, relativePersonTaxCode, id);
  }
}
