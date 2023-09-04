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
 * PrepareF24Request
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class PrepareF24Request   {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("setId")
  private String setId;

  @JsonProperty("pathTokens")
  @Valid
  private List<String> pathTokens = null;

  @JsonProperty("notificationCost")
  private Integer notificationCost = null;

  public PrepareF24Request requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Identificativo della richiesta. E' lo stesso usato nel path del metodo
   * @return requestId
  */
  @NotNull 
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public PrepareF24Request setId(String setId) {
    this.setId = setId;
    return this;
  }

  /**
   * Get setId
   * @return setId
  */
  @NotNull @Size(min = 1) 
  public String getSetId() {
    return setId;
  }

  public void setSetId(String setId) {
    this.setId = setId;
  }

  public PrepareF24Request pathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
    return this;
  }

  public PrepareF24Request addPathTokensItem(String pathTokensItem) {
    if (this.pathTokens == null) {
      this.pathTokens = new ArrayList<>();
    }
    this.pathTokens.add(pathTokensItem);
    return this;
  }

  /**
   * Identificativo id path
   * @return pathTokens
  */
  
  public List<String> getPathTokens() {
    return pathTokens;
  }

  public void setPathTokens(List<String> pathTokens) {
    this.pathTokens = pathTokens;
  }

  public PrepareF24Request notificationCost(Integer notificationCost) {
    this.notificationCost = notificationCost;
    return this;
  }

  /**
   * notification cost in eurocent to include, obbligatorio se i documenti prevedono  l'aggiornamento con il costo della notifica.
   * @return notificationCost
  */
  
  public Integer getNotificationCost() {
    return notificationCost;
  }

  public void setNotificationCost(Integer notificationCost) {
    this.notificationCost = notificationCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrepareF24Request prepareF24Request = (PrepareF24Request) o;
    return Objects.equals(this.requestId, prepareF24Request.requestId) &&
        Objects.equals(this.setId, prepareF24Request.setId) &&
        Objects.equals(this.pathTokens, prepareF24Request.pathTokens) &&
        Objects.equals(this.notificationCost, prepareF24Request.notificationCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, setId, pathTokens, notificationCost);
  }
}
