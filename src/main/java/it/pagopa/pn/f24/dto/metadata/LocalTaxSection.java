package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.LocalTaxRecord;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * LocalTax Section (Sezione LocalTax e Altri Tributi Locali) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class LocalTaxSection   {

  @JsonProperty("operationId")
  private String operationId;

  @JsonProperty("records")
  @Valid
  private List<LocalTaxRecord> records = null;

  @JsonProperty("deduction")
  private String deduction;

  public LocalTaxSection operationId(String operationId) {
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

  public LocalTaxSection records(List<LocalTaxRecord> records) {
    this.records = records;
    return this;
  }

  public LocalTaxSection addRecordsItem(LocalTaxRecord recordsItem) {
    if (this.records == null) {
      this.records = new ArrayList<>();
    }
    this.records.add(recordsItem);
    return this;
  }

  /**
   * Get records
   * @return records
  */
  @Valid 
  public List<LocalTaxRecord> getRecords() {
    return records;
  }

  public void setRecords(List<LocalTaxRecord> records) {
    this.records = records;
  }

  public LocalTaxSection deduction(String deduction) {
    this.deduction = deduction;
    return this;
  }

  /**
   * if there are any deduction
   * @return deduction
  */
  @Pattern(regexp = "^[0-9]{3,15}$") 
  public String getDeduction() {
    return deduction;
  }

  public void setDeduction(String deduction) {
    this.deduction = deduction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalTaxSection localTaxSection = (LocalTaxSection) o;
    return Objects.equals(this.operationId, localTaxSection.operationId) &&
        Objects.equals(this.records, localTaxSection.records) &&
        Objects.equals(this.deduction, localTaxSection.deduction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operationId, records, deduction);
  }
}
