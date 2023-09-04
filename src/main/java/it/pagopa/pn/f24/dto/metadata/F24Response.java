package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24Response
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Response   {

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("retryAfter")
  private BigDecimal retryAfter = null;

  public F24Response url(String url) {
    this.url = url;
    return this;
  }

  /**
   * URL per il download del contenuto del documento. example: 'https://preloadpn.aws.amazon.......'
   * @return url
  */
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public F24Response retryAfter(BigDecimal retryAfter) {
    this.retryAfter = retryAfter;
    return this;
  }

  /**
   * Stima del numero di secondi da aspettare prima che il contenuto del documento sia scaricabile.
   * @return retryAfter
  */
  @Valid 
  public BigDecimal getRetryAfter() {
    return retryAfter;
  }

  public void setRetryAfter(BigDecimal retryAfter) {
    this.retryAfter = retryAfter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Response f24Response = (F24Response) o;
    return Objects.equals(this.url, f24Response.url) &&
        Objects.equals(this.retryAfter, f24Response.retryAfter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, retryAfter);
  }
}
