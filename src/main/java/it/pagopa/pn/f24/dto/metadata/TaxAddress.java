package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Tax Residence (Domicilio Fiscale) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TaxAddress   {

  @JsonProperty("municipality")
  private String municipality;

  @JsonProperty("province")
  private String province;

  @JsonProperty("address")
  private String address;

  public TaxAddress municipality(String municipality) {
    this.municipality = municipality;
    return this;
  }

  /**
   * municipality of the tax address
   * @return municipality
  */
  @Pattern(regexp = "^[A-Z]{1,40}$") 
  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public TaxAddress province(String province) {
    this.province = province;
    return this;
  }

  /**
   * province of the tax address
   * @return province
  */
  @Pattern(regexp = "^[A-Z]{2}$") 
  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public TaxAddress address(String address) {
    this.address = address;
    return this;
  }

  /**
   * street and house number of the tax address
   * @return address
  */
  @Pattern(regexp = "^[A-Z0-9\\s.]{1,35}$") 
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxAddress taxAddress = (TaxAddress) o;
    return Objects.equals(this.municipality, taxAddress.municipality) &&
        Objects.equals(this.province, taxAddress.province) &&
        Objects.equals(this.address, taxAddress.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(municipality, province, address);
  }
}
