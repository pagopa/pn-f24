package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.ExciseSection;
import it.pagopa.pn.f24.dto.metadata.InpsSection;
import it.pagopa.pn.f24.dto.metadata.LocalTaxSection;
import it.pagopa.pn.f24.dto.metadata.RegionSection;
import it.pagopa.pn.f24.dto.metadata.TaxPayerExcise;
import it.pagopa.pn.f24.dto.metadata.TreasurySection;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24 Excise (Accise) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Excise   {

  @JsonProperty("taxPayer")
  private TaxPayerExcise taxPayer;

  @JsonProperty("treasury")
  private TreasurySection treasury;

  @JsonProperty("inps")
  private InpsSection inps;

  @JsonProperty("region")
  private RegionSection region;

  @JsonProperty("localTax")
  private LocalTaxSection localTax;

  @JsonProperty("excise")
  private ExciseSection excise;

  public F24Excise taxPayer(TaxPayerExcise taxPayer) {
    this.taxPayer = taxPayer;
    return this;
  }

  /**
   * Get taxPayer
   * @return taxPayer
  */
  @Valid 
  public TaxPayerExcise getTaxPayer() {
    return taxPayer;
  }

  public void setTaxPayer(TaxPayerExcise taxPayer) {
    this.taxPayer = taxPayer;
  }

  public F24Excise treasury(TreasurySection treasury) {
    this.treasury = treasury;
    return this;
  }

  /**
   * Get treasury
   * @return treasury
  */
  @Valid 
  public TreasurySection getTreasury() {
    return treasury;
  }

  public void setTreasury(TreasurySection treasury) {
    this.treasury = treasury;
  }

  public F24Excise inps(InpsSection inps) {
    this.inps = inps;
    return this;
  }

  /**
   * Get inps
   * @return inps
  */
  @Valid 
  public InpsSection getInps() {
    return inps;
  }

  public void setInps(InpsSection inps) {
    this.inps = inps;
  }

  public F24Excise region(RegionSection region) {
    this.region = region;
    return this;
  }

  /**
   * Get region
   * @return region
  */
  @Valid 
  public RegionSection getRegion() {
    return region;
  }

  public void setRegion(RegionSection region) {
    this.region = region;
  }

  public F24Excise localTax(LocalTaxSection localTax) {
    this.localTax = localTax;
    return this;
  }

  /**
   * Get localTax
   * @return localTax
  */
  @Valid 
  public LocalTaxSection getLocalTax() {
    return localTax;
  }

  public void setLocalTax(LocalTaxSection localTax) {
    this.localTax = localTax;
  }

  public F24Excise excise(ExciseSection excise) {
    this.excise = excise;
    return this;
  }

  /**
   * Get excise
   * @return excise
  */
  @Valid 
  public ExciseSection getExcise() {
    return excise;
  }

  public void setExcise(ExciseSection excise) {
    this.excise = excise;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Excise f24Excise = (F24Excise) o;
    return Objects.equals(this.taxPayer, f24Excise.taxPayer) &&
        Objects.equals(this.treasury, f24Excise.treasury) &&
        Objects.equals(this.inps, f24Excise.inps) &&
        Objects.equals(this.region, f24Excise.region) &&
        Objects.equals(this.localTax, f24Excise.localTax) &&
        Objects.equals(this.excise, f24Excise.excise);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxPayer, treasury, inps, region, localTax, excise);
  }
}
