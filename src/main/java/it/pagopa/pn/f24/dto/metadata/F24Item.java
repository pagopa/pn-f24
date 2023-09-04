package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.F24Elid;
import it.pagopa.pn.f24.dto.metadata.F24Excise;
import it.pagopa.pn.f24.dto.metadata.F24Metadata;
import it.pagopa.pn.f24.dto.metadata.F24Simplified;
import it.pagopa.pn.f24.dto.metadata.F24Standard;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * F24Item
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class F24Item   {

  @JsonProperty("pathTokens")
  @Valid
  private List<String> pathTokens = new ArrayList<>();

  @JsonProperty("f24Standard")
  private F24Standard f24Standard = null;

  @JsonProperty("f24Simplified")
  private F24Simplified f24Simplified = null;

  @JsonProperty("f24Excise")
  private F24Excise f24Excise = null;

  @JsonProperty("f24Elid")
  private F24Elid f24Elid = null;

  public F24Item pathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
    return this;
  }

  public F24Item addPathTokensItem(String pathTokensItem) {
    if (this.pathTokens == null) {
      this.pathTokens = new ArrayList<>();
    }
    this.pathTokens.add(pathTokensItem);
    return this;
  }

  /**
   * Lista di id da usare per costruire l'alberatura che identifica il singolo metadato
   * @return pathTokens
  */
  @NotNull 
  public List<String> getPathTokens() {
    return pathTokens;
  }

  public void setPathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
  }

  public F24Item f24Standard(F24Standard f24Standard) {
    this.f24Standard = f24Standard;
    return this;
  }

  /**
   * Get f24Standard
   * @return f24Standard
  */
  @Valid 
  public F24Standard getF24Standard() {
    return f24Standard;
  }

  public void setF24Standard(F24Standard f24Standard) {
    this.f24Standard = f24Standard;
  }

  public F24Item f24Simplified(F24Simplified f24Simplified) {
    this.f24Simplified = f24Simplified;
    return this;
  }

  /**
   * Get f24Simplified
   * @return f24Simplified
  */
  @Valid 
  public F24Simplified getF24Simplified() {
    return f24Simplified;
  }

  public void setF24Simplified(F24Simplified f24Simplified) {
    this.f24Simplified = f24Simplified;
  }

  public F24Item f24Excise(F24Excise f24Excise) {
    this.f24Excise = f24Excise;
    return this;
  }

  /**
   * Get f24Excise
   * @return f24Excise
  */
  @Valid 
  public F24Excise getF24Excise() {
    return f24Excise;
  }

  public void setF24Excise(F24Excise f24Excise) {
    this.f24Excise = f24Excise;
  }

  public F24Item f24Elid(F24Elid f24Elid) {
    this.f24Elid = f24Elid;
    return this;
  }

  /**
   * Get f24Elid
   * @return f24Elid
  */
  @Valid 
  public F24Elid getF24Elid() {
    return f24Elid;
  }

  public void setF24Elid(F24Elid f24Elid) {
    this.f24Elid = f24Elid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    F24Item f24Item = (F24Item) o;
    return Objects.equals(this.pathTokens, f24Item.pathTokens) &&
        Objects.equals(this.f24Standard, f24Item.f24Standard) &&
        Objects.equals(this.f24Simplified, f24Item.f24Simplified) &&
        Objects.equals(this.f24Excise, f24Item.f24Excise) &&
        Objects.equals(this.f24Elid, f24Item.f24Elid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pathTokens, f24Standard, f24Simplified, f24Excise, f24Elid);
  }
}
