package it.pagopa.pn.f24.dto.metadata;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import it.pagopa.pn.f24.dto.metadata.F24Item;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;


import java.util.*;
import javax.annotation.Generated;

/**
 * SaveF24Request
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-18T13:00:30.717926500+02:00[Europe/Berlin]")
@lombok.ToString
public class SaveF24Request   {

  @JsonProperty("setId")
  private String setId;

  @JsonProperty("f24Items")
  @Valid
  private List<F24Item> f24Items = null;

  public SaveF24Request setId(String setId) {
    this.setId = setId;
    return this;
  }

  /**
   * Identificativo SetId della richiesta. E' lo stesso usato nel path del metodo
   * @return setId
  */
  @NotNull 
  public String getSetId() {
    return setId;
  }

  public void setSetId(String setId) {
    this.setId = setId;
  }

  public SaveF24Request f24Items(List<F24Item> f24Items) {
    this.f24Items = f24Items;
    return this;
  }

  public SaveF24Request addF24ItemsItem(F24Item f24ItemsItem) {
    if (this.f24Items == null) {
      this.f24Items = new ArrayList<>();
    }
    this.f24Items.add(f24ItemsItem);
    return this;
  }

  /**
   * Get f24Items
   * @return f24Items
  */
  @Valid 
  public List<F24Item> getF24Items() {
    return f24Items;
  }

  public void setF24Items(List<F24Item> f24Items) {
    this.f24Items = f24Items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaveF24Request saveF24Request = (SaveF24Request) o;
    return Objects.equals(this.setId, saveF24Request.setId) &&
        Objects.equals(this.f24Items, saveF24Request.f24Items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(setId, f24Items);
  }
}
