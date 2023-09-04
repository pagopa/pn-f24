package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.InpsSection;
import it.pagopa.pn.f24.dto.metadata.LocalTaxSection;
import it.pagopa.pn.f24.dto.metadata.RegionSection;
import it.pagopa.pn.f24.dto.metadata.SocialSecuritySection;
import it.pagopa.pn.f24.dto.metadata.TaxPayerStandard;
import it.pagopa.pn.f24.dto.metadata.TreasurySection;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24Standard
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Standard   {

  @JsonProperty("taxPayer")
  private TaxPayerStandard taxPayer;

  @JsonProperty("treasury")
  private TreasurySection treasury;

  @JsonProperty("inps")
  private InpsSection inps;

  @JsonProperty("region")
  private RegionSection region;

  @JsonProperty("localTax")
  private LocalTaxSection localTax;

  @JsonProperty("socialSecurity")
  private SocialSecuritySection socialSecurity;

  public F24Standard taxPayer(TaxPayerStandard taxPayer) {
    this.taxPayer = taxPayer;
    return this;
  }

  /**
   * Get taxPayer
   * @return taxPayer
  */
  @Valid 
  public TaxPayerStandard getTaxPayer() {
    return taxPayer;
  }

  public void setTaxPayer(TaxPayerStandard taxPayer) {
    this.taxPayer = taxPayer;
  }

  public F24Standard treasury(TreasurySection treasury) {
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

  public F24Standard inps(InpsSection inps) {
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

  public F24Standard region(RegionSection region) {
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

  public F24Standard localTax(LocalTaxSection localTax) {
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

  public F24Standard socialSecurity(SocialSecuritySection socialSecurity) {
    this.socialSecurity = socialSecurity;
    return this;
  }

  /**
   * Get socialSecurity
   * @return socialSecurity
  */
  @Valid 
  public SocialSecuritySection getSocialSecurity() {
    return socialSecurity;
  }

  public void setSocialSecurity(SocialSecuritySection socialSecurity) {
    this.socialSecurity = socialSecurity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Standard f24Standard = (F24Standard) o;
    return Objects.equals(this.taxPayer, f24Standard.taxPayer) &&
        Objects.equals(this.treasury, f24Standard.treasury) &&
        Objects.equals(this.inps, f24Standard.inps) &&
        Objects.equals(this.region, f24Standard.region) &&
        Objects.equals(this.localTax, f24Standard.localTax) &&
        Objects.equals(this.socialSecurity, f24Standard.socialSecurity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxPayer, treasury, inps, region, localTax, socialSecurity);
  }
}
