package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * Personal Data (Dati Anagrafici) object
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class PersonalData   {

  @JsonProperty("surname")
  private String surname;

  @JsonProperty("name")
  private String name;

  @JsonProperty("birthDate")
  private String birthDate;

  @JsonProperty("sex")
  private String sex;

  @JsonProperty("birthPlace")
  private String birthPlace;

  @JsonProperty("birthProvince")
  private String birthProvince;

  public PersonalData surname(String surname) {
    this.surname = surname;
    return this;
  }

  /**
   * surname of the person
   * @return surname
  */
  @Pattern(regexp = "^[A-Z\\\\s]{1,24}$") 
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public PersonalData name(String name) {
    this.name = name;
    return this;
  }

  /**
   * name of the person
   * @return name
  */
  @Pattern(regexp = "^[A-Z\\\\s]{1,20}$") 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PersonalData birthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * birthdate of the person
   * @return birthDate
  */
  @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$") 
  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public PersonalData sex(String sex) {
    this.sex = sex;
    return this;
  }

  /**
   * indicate if is (F)emale or (M)ale
   * @return sex
  */
  @Pattern(regexp = "^[FM]$") 
  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public PersonalData birthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
    return this;
  }

  /**
   * birth place of the person
   * @return birthPlace
  */
  @Pattern(regexp = "^[A-Z\\s]{1,40}$") 
  public String getBirthPlace() {
    return birthPlace;
  }

  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public PersonalData birthProvince(String birthProvince) {
    this.birthProvince = birthProvince;
    return this;
  }

  /**
   * birth province of the person
   * @return birthProvince
  */
  @Pattern(regexp = "^[A-Z]{2}$") 
  public String getBirthProvince() {
    return birthProvince;
  }

  public void setBirthProvince(String birthProvince) {
    this.birthProvince = birthProvince;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PersonalData personalData = (PersonalData) o;
    return Objects.equals(this.surname, personalData.surname) &&
        Objects.equals(this.name, personalData.name) &&
        Objects.equals(this.birthDate, personalData.birthDate) &&
        Objects.equals(this.sex, personalData.sex) &&
        Objects.equals(this.birthPlace, personalData.birthPlace) &&
        Objects.equals(this.birthProvince, personalData.birthProvince);
  }

  @Override
  public int hashCode() {
    return Objects.hash(surname, name, birthDate, sex, birthPlace, birthProvince);
  }
}
