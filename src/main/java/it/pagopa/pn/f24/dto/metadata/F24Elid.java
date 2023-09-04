package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.TaxPayerElide;
import it.pagopa.pn.f24.dto.metadata.TreasuryAndOtherSection;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24Elid
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Elid   {

  @JsonProperty("taxPayer")
  private TaxPayerElide taxPayer;

  @JsonProperty("treasury")
  private TreasuryAndOtherSection treasury;

  public F24Elid taxPayer(TaxPayerElide taxPayer) {
    this.taxPayer = taxPayer;
    return this;
  }

  /**
   * Get taxPayer
   * @return taxPayer
  */
  @Valid 
  public TaxPayerElide getTaxPayer() {
    return taxPayer;
  }

  public void setTaxPayer(TaxPayerElide taxPayer) {
    this.taxPayer = taxPayer;
  }

  public F24Elid treasury(TreasuryAndOtherSection treasury) {
    this.treasury = treasury;
    return this;
  }

  /**
   * Get treasury
   * @return treasury
  */
  @Valid 
  public TreasuryAndOtherSection getTreasury() {
    return treasury;
  }

  public void setTreasury(TreasuryAndOtherSection treasury) {
    this.treasury = treasury;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Elid f24Elid = (F24Elid) o;
    return Objects.equals(this.taxPayer, f24Elid.taxPayer) &&
        Objects.equals(this.treasury, f24Elid.treasury);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxPayer, treasury);
  }
}
