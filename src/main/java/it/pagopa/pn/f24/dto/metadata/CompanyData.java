package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.TaxAddress;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Company Data (Dati Anagrafici PNF) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class CompanyData   {

  @JsonProperty("name")
  private String name;

  @JsonProperty("taxAddress")
  private TaxAddress taxAddress;

  public CompanyData name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Company name
   * @return name
  */
  @Pattern(regexp = "^[A-Z\\s]{1,60}$") 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CompanyData taxAddress(TaxAddress taxAddress) {
    this.taxAddress = taxAddress;
    return this;
  }

  /**
   * Get taxAddress
   * @return taxAddress
  */
  @Valid 
  public TaxAddress getTaxAddress() {
    return taxAddress;
  }

  public void setTaxAddress(TaxAddress taxAddress) {
    this.taxAddress = taxAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyData companyData = (CompanyData) o;
    return Objects.equals(this.name, companyData.name) &&
        Objects.equals(this.taxAddress, companyData.taxAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, taxAddress);
  }
}
