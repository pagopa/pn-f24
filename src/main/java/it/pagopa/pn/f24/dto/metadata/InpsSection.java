package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.InpsRecord;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * INPS Section (Sezione INPS) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class InpsSection   {

  @JsonProperty("records")
  @Valid
  private List<InpsRecord> records = null;

  public InpsSection records(List<InpsRecord> records) {
    this.records = records;
    return this;
  }

  public InpsSection addRecordsItem(InpsRecord recordsItem) {
    if (this.records == null) {
      this.records = new ArrayList<>();
    }
    this.records.add(recordsItem);
    return this;
  }

  /**
   * INPS Record List
   * @return records
  */
  @Valid 
  public List<InpsRecord> getRecords() {
    return records;
  }

  public void setRecords(List<InpsRecord> records) {
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
    InpsSection inpsSection = (InpsSection) o;
    return Objects.equals(this.records, inpsSection.records);
  }

  @Override
  public int hashCode() {
    return Objects.hash(records);
  }
}
