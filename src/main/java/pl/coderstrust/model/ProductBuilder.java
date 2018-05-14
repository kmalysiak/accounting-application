package pl.coderstrust.model;

import static pl.coderstrust.model.Vat.VAT_23;

import java.math.BigDecimal;

public class ProductBuilder {

  private Product product = new Product();

  /**
   * If not provided Vat rate is set to 23%.
   *
   * @param name -Product name.
   * @param netValue = Product net value.
   */
  public ProductBuilder(String name, double netValue) {
    product.setName(name);
    product.setNetValue(BigDecimal.valueOf(netValue));
    product.setVatRate(VAT_23);
  }

  public ProductBuilder setDescription(String description) {
    product.setDescription(description);
    return this;
  }

  public ProductBuilder setVatRate(Vat vatRate) {
    product.setVatRate(vatRate);
    return this;
  }

  public ProductBuilder setProductType(ProductType productType) {
    product.setProductType(productType);
    return this;
  }

  public Product build() {
    return product;
  }
}