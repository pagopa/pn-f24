package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Uri relativo a SafeStorage o indicazione dell&#39;errore avvenuto
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class PDFSetReadyEventItem   {

  @JsonProperty("pathTokens")
  @Valid
  private List<String> pathTokens = new ArrayList<>();

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("errorCode")
  private String errorCode;

  public PDFSetReadyEventItem pathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
    return this;
  }

  public PDFSetReadyEventItem addPathTokensItem(String pathTokensItem) {
    if (this.pathTokens == null) {
      this.pathTokens = new ArrayList<>();
    }
    this.pathTokens.add(pathTokensItem);
    return this;
  }

  /**
   * identificativo del
   * @return pathTokens
  */
  @NotNull 
  public List<String> getPathTokens() {
    return pathTokens;
  }

  public void setPathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
  }

  public PDFSetReadyEventItem uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * riferimento al file caricato su safestorage
   * @return uri
  */
  
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public PDFSetReadyEventItem errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * codice di errore _man mano che si trovano casi aggiungerli alla lista seguente_ - __F4__ : sono rimasto basito (non è serio) - __BO__ : mi è andata una bruschetta nell'occhio (non è serio)
   * @return errorCode
  */
  
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PDFSetReadyEventItem pdFSetReadyEventItem = (PDFSetReadyEventItem) o;
    return Objects.equals(this.pathTokens, pdFSetReadyEventItem.pathTokens) &&
        Objects.equals(this.uri, pdFSetReadyEventItem.uri) &&
        Objects.equals(this.errorCode, pdFSetReadyEventItem.errorCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pathTokens, uri, errorCode);
  }
}
