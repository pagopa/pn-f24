package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.SimplifiedPaymentSection;
import it.pagopa.pn.f24.dto.metadata.TaxPayerSimplified;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24Simplified
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Simplified   {

  @JsonProperty("taxPayer")
  private TaxPayerSimplified taxPayer;

  @JsonProperty("payments")
  private SimplifiedPaymentSection payments;

  public F24Simplified taxPayer(TaxPayerSimplified taxPayer) {
    this.taxPayer = taxPayer;
    return this;
  }

  /**
   * Get taxPayer
   * @return taxPayer
  */
  @Valid 
  public TaxPayerSimplified getTaxPayer() {
    return taxPayer;
  }

  public void setTaxPayer(TaxPayerSimplified taxPayer) {
    this.taxPayer = taxPayer;
  }

  public F24Simplified payments(SimplifiedPaymentSection payments) {
    this.payments = payments;
    return this;
  }

  /**
   * Get payments
   * @return payments
  */
  @Valid 
  public SimplifiedPaymentSection getPayments() {
    return payments;
  }

  public void setPayments(SimplifiedPaymentSection payments) {
    this.payments = payments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Simplified f24Simplified = (F24Simplified) o;
    return Objects.equals(this.taxPayer, f24Simplified.taxPayer) &&
        Objects.equals(this.payments, f24Simplified.payments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxPayer, payments);
  }
}
