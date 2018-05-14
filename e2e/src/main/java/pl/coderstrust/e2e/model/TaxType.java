package pl.coderstrust.e2e.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum TaxType {
  LINEAR("LINEAR"),
  PROGRESIVE("Progresive");

  private String taxesType;

  TaxType(String taxesType) {
    this.taxesType = taxesType;
  }
}