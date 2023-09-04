package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.TreasuryRecord;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Treasury ans Other Section (Sezione Erario e Altro) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TreasuryAndOtherSection   {

  @JsonProperty("office")
  private String office;

  @JsonProperty("document")
  private String document;

  @JsonProperty("records")
  @Valid
  private List<TreasuryRecord> records = null;

  public TreasuryAndOtherSection office(String office) {
    this.office = office;
    return this;
  }

  /**
   * identification code of the office
   * @return office
  */
  @Pattern(regexp = "^[A-Z0-9]{3}$") 
  public String getOffice() {
    return office;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  public TreasuryAndOtherSection document(String document) {
    this.document = document;
    return this;
  }

  /**
   * identification code of the document
   * @return document
  */
  @Pattern(regexp = "^\\d{11}$") 
  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public TreasuryAndOtherSection records(List<TreasuryRecord> records) {
    this.records = records;
    return this;
  }

  public TreasuryAndOtherSection addRecordsItem(TreasuryRecord recordsItem) {
    if (this.records == null) {
      this.records = new ArrayList<>();
    }
    this.records.add(recordsItem);
    return this;
  }

  /**
   * Treasury Records
   * @return records
  */
  @Valid 
  public List<TreasuryRecord> getRecords() {
    return records;
  }

  public void setRecords(List<TreasuryRecord> records) {
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
    TreasuryAndOtherSection treasuryAndOtherSection = (TreasuryAndOtherSection) o;
    return Objects.equals(this.office, treasuryAndOtherSection.office) &&
        Objects.equals(this.document, treasuryAndOtherSection.document) &&
        Objects.equals(this.records, treasuryAndOtherSection.records);
  }

  @Override
  public int hashCode() {
    return Objects.hash(office, document, records);
  }
}
