package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.InailRecord;
import it.pagopa.pn.f24.dto.metadata.SocialSecurityRecord;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Social Security Section (Sezione Altri Enti Previdenziali) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class SocialSecuritySection   {

  @JsonProperty("records")
  @Valid
  private List<InailRecord> records = null;

  @JsonProperty("socSecRecords")
  @Valid
  private List<SocialSecurityRecord> socSecRecords = null;

  public SocialSecuritySection records(List<InailRecord> records) {
    this.records = records;
    return this;
  }

  public SocialSecuritySection addRecordsItem(InailRecord recordsItem) {
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
  public List<InailRecord> getRecords() {
    return records;
  }

  public void setRecords(List<InailRecord> records) {
    this.records = records;
  }

  public SocialSecuritySection socSecRecords(List<SocialSecurityRecord> socSecRecords) {
    this.socSecRecords = socSecRecords;
    return this;
  }

  public SocialSecuritySection addSocSecRecordsItem(SocialSecurityRecord socSecRecordsItem) {
    if (this.socSecRecords == null) {
      this.socSecRecords = new ArrayList<>();
    }
    this.socSecRecords.add(socSecRecordsItem);
    return this;
  }

  /**
   * Social Security Record List
   * @return socSecRecords
  */
  @Valid 
  public List<SocialSecurityRecord> getSocSecRecords() {
    return socSecRecords;
  }

  public void setSocSecRecords(List<SocialSecurityRecord> socSecRecords) {
    this.socSecRecords = socSecRecords;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SocialSecuritySection socialSecuritySection = (SocialSecuritySection) o;
    return Objects.equals(this.records, socialSecuritySection.records) &&
        Objects.equals(this.socSecRecords, socialSecuritySection.socSecRecords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(records, socSecRecords);
  }
}
