package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.List;

public class InvoiceBuilder {

  private Invoice invoice = new Invoice();

  public InvoiceBuilder(long id, String buyerName, String sellerName) {
    invoice.setId(id);
    invoice.setBuyer(new CompanyBuilder(buyerName).build());
    invoice.setSeller(new CompanyBuilder(sellerName).build());
  }

  public InvoiceBuilder setVisibleId(String visibleId) {
    invoice.setName(visibleId);
    return this;
  }

  public InvoiceBuilder setId(long id) {
    invoice.setId(id);
    return this;
  }

  public InvoiceBuilder setBuyer(Company company) {
    invoice.setBuyer(company);
    return this;
  }

  public InvoiceBuilder setSeller(Company company) {
    invoice.setSeller(company);
    return this;
  }

  public InvoiceBuilder setIssueDate(LocalDate issueDate) {
    invoice.setIssueDate(issueDate);
    return this;
  }

  public InvoiceBuilder setPaymentDate(LocalDate paymentDate) {
    invoice.setPaymentDate(paymentDate);
    return this;
  }

  public InvoiceBuilder setProducts(List<InvoiceEntry> products) {
    invoice.setProducts(products);
    return this;
  }

  public InvoiceBuilder setPaymentState(PaymentState paymentState) {
    invoice.setPaymentState(paymentState);
    return this;
  }

  public Invoice build() {
    return invoice;
  }
}
