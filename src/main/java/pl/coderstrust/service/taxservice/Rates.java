package pl.coderstrust.service.taxservice;

import java.math.BigDecimal;

public enum Rates {
  LINEAR_TAX_RATE(BigDecimal.valueOf(0.19)),
  PROGRESSIVE_TAX_RATE_THRESHOLD_LOW_PERCENT(BigDecimal.valueOf(0.18)),
  PROGRESSIVE_TAX_RATE_THRESHOLD_HIGH_PERCENT(BigDecimal.valueOf(0.32)),
  PROGRESSIVE_TAX_RATE_THRESHOLD(BigDecimal.valueOf(85528)),
  HEALTH_INSURANCE_TAX_RATE(BigDecimal.valueOf(0.0775)),
  PENSION_INSURANCE(BigDecimal.valueOf(514.57)),
  DECREASING_TAX_AMOUNT(BigDecimal.valueOf(556.02));

  private BigDecimal value;

  Rates(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getValue() {
    return value;
  }
}