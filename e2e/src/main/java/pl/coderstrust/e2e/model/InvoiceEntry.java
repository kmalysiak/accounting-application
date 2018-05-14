package pl.coderstrust.e2e.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
public class InvoiceEntry {

  private Product product;
  private int amount;

  public InvoiceEntry(Product product, int amount) {
    this.product = product;
    this.amount = amount;
  }
}
