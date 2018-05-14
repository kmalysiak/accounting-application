package pl.coderstrust.model;

public class SummaryOfProductsPrices {

  private String productName;
  private String amount;
  private String retailPrice;
  private String productNetValue;
  private String productVatRate;
  private String productVatValue;
  private String total;

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getRetailPrice() {
    return retailPrice;
  }

  public void setRetailPrice(String retailPrice) {
    this.retailPrice = retailPrice;
  }

  public String getProductNetValue() {
    return productNetValue;
  }

  public void setProductNetValue(String productNetValue) {
    this.productNetValue = productNetValue;
  }

  public String getProductVatRate() {
    return productVatRate;
  }

  public void setProductVatRate(String productVatRate) {
    this.productVatRate = productVatRate;
  }

  public String getProductVatValue() {
    return productVatValue;
  }

  public void setProductVatValue(String productVatValue) {
    this.productVatValue = productVatValue;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }
}