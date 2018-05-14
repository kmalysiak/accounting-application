package pl.coderstrust.e2e.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ProductType {
  CAR("Car"),
  OFFICE("Office"),
  ELECTRIONICS("Electronics"),
  CLEANERS("Cleaners"),
  OTHER("Other");

  private String type;

  ProductType(String type) {
    this.type = type;
  }
}