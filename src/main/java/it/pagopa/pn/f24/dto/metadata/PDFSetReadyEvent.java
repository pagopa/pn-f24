package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.PDFSetReadyEventItem;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Body del messaggio inviato ad event bridge per indicare che un insieme di PDF è  stato generato
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class PDFSetReadyEvent   {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("status")
  private String status;

  @JsonProperty("generatedPdfsUrls")
  @Valid
  private List<PDFSetReadyEventItem> generatedPdfsUrls = new ArrayList<>();

  public PDFSetReadyEvent requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Identificativo della richiesta. E' usato per correlare la risposta alla richiesta
   * @return requestId
  */
  @NotNull 
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public PDFSetReadyEvent status(String status) {
    this.status = status;
    return this;
  }

  /**
   * - OK se tutto va bene - KO in caso di problemi
   * @return status
  */
  @NotNull 
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public PDFSetReadyEvent generatedPdfsUrls(List<PDFSetReadyEventItem> generatedPdfsUrls) {
    this.generatedPdfsUrls = generatedPdfsUrls;
    return this;
  }

  public PDFSetReadyEvent addGeneratedPdfsUrlsItem(PDFSetReadyEventItem generatedPdfsUrlsItem) {
    if (this.generatedPdfsUrls == null) {
      this.generatedPdfsUrls = new ArrayList<>();
    }
    this.generatedPdfsUrls.add(generatedPdfsUrlsItem);
    return this;
  }

  /**
   * Array di elementi di cui ognuno riporta il risultato della generazione di un  file PDF a partire dai metadati di un F24
   * @return generatedPdfsUrls
  */
  @NotNull @Valid @Size(min = 1, max = 9999) 
  public List<PDFSetReadyEventItem> getGeneratedPdfsUrls() {
    return generatedPdfsUrls;
  }

  public void setGeneratedPdfsUrls(List<PDFSetReadyEventItem> generatedPdfsUrls) {
    this.generatedPdfsUrls = generatedPdfsUrls;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PDFSetReadyEvent pdFSetReadyEvent = (PDFSetReadyEvent) o;
    return Objects.equals(this.requestId, pdFSetReadyEvent.requestId) &&
        Objects.equals(this.status, pdFSetReadyEvent.status) &&
        Objects.equals(this.generatedPdfsUrls, pdFSetReadyEvent.generatedPdfsUrls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, status, generatedPdfsUrls);
  }
}
