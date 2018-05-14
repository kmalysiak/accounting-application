package pl.coderstrust.model;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("product")
public class Product implements WithValidation {

  private String name;
  private String description;
  private BigDecimal netValue;
  private Vat vatRate;
  private ProductType productType;

  public Product() {
  }

  @ApiModelProperty(example = "Apple")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(example = "Green Fresh Apple")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(example = "2.50")
  public BigDecimal getNetValue() {
    return netValue;
  }

  public void setNetValue(BigDecimal netValue) {
    this.netValue = netValue;
  }

  public Vat getVatRate() {
    return vatRate;
  }

  public void setVatRate(Vat vatRate) {
    this.vatRate = vatRate;
  }

  public ProductType getProductType() {
    return productType;
  }

  public void setProductType(ProductType productType) {
    this.productType = productType;
  }

  @Override
  public boolean equals(Object object) {
    return EqualsBuilder.reflectionEquals(this, object);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, true);
  }

  @Override
  public List<String> validate() {

    List<String> errors = new ArrayList<>();

    if (checkInputString(this.getName())) {
      errors.add(Messages.PRODUCT_NO_NAME);
    }

    if (checkInputString(this.getDescription())) {
      errors.add(Messages.PRODUCT_NO_DESCRIPTION);
    }

    if (this.getVatRate() == null) {
      errors.add(Messages.PRODUCT_NO_VAT);
    }

    if (this.getNetValue() == null) {
      errors.add(Messages.PRODUCT_NO_NET_VALUE);
    } else if (this.getNetValue().compareTo(BigDecimal.ZERO) <= 0) {
      errors.add(Messages.PRODUCT_WRONG_NET_VALUE);
    }
    return errors;
  }
}
