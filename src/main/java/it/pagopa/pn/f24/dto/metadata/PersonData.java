package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.PersonalData;
import it.pagopa.pn.f24.dto.metadata.TaxAddress;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Person Data (Dati Anagrafici PF) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class PersonData   {

  @JsonProperty("personalData")
  private PersonalData personalData;

  @JsonProperty("taxAddress")
  private TaxAddress taxAddress;

  public PersonData personalData(PersonalData personalData) {
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

  public PersonData taxAddress(TaxAddress taxAddress) {
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
    PersonData personData = (PersonData) o;
    return Objects.equals(this.personalData, personData.personalData) &&
        Objects.equals(this.taxAddress, personData.taxAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(personalData, taxAddress);
  }
}
