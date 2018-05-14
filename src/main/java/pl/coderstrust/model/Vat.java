package pl.coderstrust.model;

public enum Vat {
  VAT_23(0.23),
  VAT_8(0.08),
  VAT_5(0.05),
  VAT_0(0);

  private final double vatPercent;

  Vat(double vatPercent) {
    this.vatPercent = vatPercent;
  }

  public double getVatPercent() {
    return vatPercent;
  }
}
