package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.Tax;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Treasury Section (Sezione Erario) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class TreasurySection   {

  @JsonProperty("records")
  @Valid
  private List<Tax> records = null;

  @JsonProperty("office")
  private String office;

  @JsonProperty("document")
  private String document;

  public TreasurySection records(List<Tax> records) {
    this.records = records;
    return this;
  }

  public TreasurySection addRecordsItem(Tax recordsItem) {
    if (this.records == null) {
      this.records = new ArrayList<>();
    }
    this.records.add(recordsItem);
    return this;
  }

  /**
   * list of the taxes
   * @return records
  */
  @Valid 
  public List<Tax> getRecords() {
    return records;
  }

  public void setRecords(List<Tax> records) {
    this.records = records;
  }

  public TreasurySection office(String office) {
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

  public TreasurySection document(String document) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TreasurySection treasurySection = (TreasurySection) o;
    return Objects.equals(this.records, treasurySection.records) &&
        Objects.equals(this.office, treasurySection.office) &&
        Objects.equals(this.document, treasurySection.document);
  }

  @Override
  public int hashCode() {
    return Objects.hash(records, office, document);
  }
}
