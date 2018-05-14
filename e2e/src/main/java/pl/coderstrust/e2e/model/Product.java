package pl.coderstrust.e2e.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {

  private String name;
  private String description;
  private BigDecimal netValue;
  private Vat vatRate;
  private ProductType productType;
}
