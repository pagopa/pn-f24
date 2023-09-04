package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.SimplifiedPaymentRecord;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Payment Reason Section (Motivo del Pagamento) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class SimplifiedPaymentSection   {

  @JsonProperty("operationId")
  private String operationId;

  @JsonProperty("records")
  @Valid
  private List<SimplifiedPaymentRecord> records = null;

  public SimplifiedPaymentSection operationId(String operationId) {
    this.operationId = operationId;
    return this;
  }

  /**
   * identification code of the operation
   * @return operationId
  */
  @Pattern(regexp = "^[A-Z0-9]{18}$") 
  public String getOperationId() {
    return operationId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public SimplifiedPaymentSection records(List<SimplifiedPaymentRecord> records) {
    this.records = records;
    return this;
  }

  public SimplifiedPaymentSection addRecordsItem(SimplifiedPaymentRecord recordsItem) {
    if (this.records == null) {
      this.records = new ArrayList<>();
    }
    this.records.add(recordsItem);
    return this;
  }

  /**
   * Payments Record List
   * @return records
  */
  @Valid 
  public List<SimplifiedPaymentRecord> getRecords() {
    return records;
  }

  public void setRecords(List<SimplifiedPaymentRecord> records) {
    this.records = records;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimplifiedPaymentSection simplifiedPaymentSection = (SimplifiedPaymentSection) o;
    return Objects.equals(this.operationId, simplifiedPaymentSection.operationId) &&
        Objects.equals(this.records, simplifiedPaymentSection.records);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operationId, records);
  }
}
