package pl.coderstrust.e2e.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Vat {
  VAT_23(0.23),
  VAT_7(0.07),
  VAT_0(0);

  private final double vatPercent;

  Vat(double vatPercent) {
    this.vatPercent = vatPercent;
  }
}
