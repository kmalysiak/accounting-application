package pl.coderstrust.service.soap;

import pl.coderstrust.model.CompanyBuilder;
import pl.coderstrust.model.InvoiceBuilder;
import pl.coderstrust.model.ProductBuilder;
import pl.coderstrust.service.soap.bindingClasses.Company;
import pl.coderstrust.service.soap.bindingClasses.Invoice;
import pl.coderstrust.service.soap.bindingClasses.InvoiceEntry;
import pl.coderstrust.service.soap.bindingClasses.PaymentState;
import pl.coderstrust.service.soap.bindingClasses.Product;
import pl.coderstrust.service.soap.bindingClasses.ProductType;
import pl.coderstrust.service.soap.bindingClasses.TaxType;
import pl.coderstrust.service.soap.bindingClasses.Vat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ModelSoapConverter {

  public pl.coderstrust.model.Invoice soapInvoiceToInvoice(Invoice invoice) {
    pl.coderstrust.model.Company seller = soapCompanyToCompany(invoice.getSeller());
    pl.coderstrust.model.Company buyer = soapCompanyToCompany(invoice.getBuyer());
    InvoiceBuilder builder = new InvoiceBuilder(invoice.getId(), buyer.getName(), seller.getName());
    builder.setVisibleId(invoice.getName());
    builder.setBuyer(buyer);
    builder.setSeller(seller);
    builder.setIssueDate(LocalDate.parse(invoice.getInvoiceIssueDate()));
    builder.setPaymentDate(LocalDate.parse(invoice.getPaymentDate()));
    builder.setProducts(soapEntriesToEntries(invoice.getProducts()));
    builder.setPaymentState(
        pl.coderstrust.model.PaymentState.valueOf(invoice.getPaymentState().value()));
    return builder.build();
  }

  public Invoice invoiceToSoapInvoice(pl.coderstrust.model.Invoice invoice) {
    Invoice soapInvoice = new Invoice();
    soapInvoice.setId(invoice.getId());
    soapInvoice.setName(invoice.getName());
    soapInvoice.setInvoiceIssueDate(invoice.getIssueDate().toString());
    soapInvoice.setPaymentDate(invoice.getPaymentDate().toString());
    soapInvoice.setPaymentState(PaymentState.fromValue(invoice.getPaymentState().toString()));
    soapInvoice.setBuyer(companyToSoapCompany(invoice.getBuyer()));
    soapInvoice.setSeller(companyToSoapCompany(invoice.getSeller()));

    for (pl.coderstrust.model.InvoiceEntry product : invoice.getProducts()) {
      soapInvoice.getProducts().add(invoiceEntryToSoapInvoiceEntry(product));
    }
    return soapInvoice;
  }

  private InvoiceEntry invoiceEntryToSoapInvoiceEntry(pl.coderstrust.model.InvoiceEntry entry) {
    InvoiceEntry soapInvoiceEntry = new InvoiceEntry();
    soapInvoiceEntry.setAmount(entry.getAmount());
    soapInvoiceEntry.setProduct(productToSoapProduct(entry.getProduct()));
    return soapInvoiceEntry;
  }

  private Product productToSoapProduct(pl.coderstrust.model.Product entry) {
    Product soapProduct = new Product();
    soapProduct.setDescription(entry.getDescription());
    soapProduct.setName(entry.getName());
    soapProduct.setNetValue(entry.getNetValue());
    soapProduct.setProductType(ProductType.fromValue(entry.getProductType().toString()));
    soapProduct.setVatRate(Vat.valueOf(entry.getVatRate().toString()));
    return soapProduct;
  }

  private Company companyToSoapCompany(pl.coderstrust.model.Company company) {
    Company soapCompany = new Company();
    soapCompany.setAddress(company.getAddress());
    soapCompany.setBankAccoutNumber(company.getBankAccoutNumber());
    soapCompany.setCity(company.getCity());
    soapCompany.setCustomerIssueDate(company.getIssueDate().toString());
    soapCompany.setId(company.getId());
    soapCompany.setName(company.getName());
    soapCompany.setNip(company.getName());
    soapCompany.setZipCode(company.getZipCode());
    soapCompany.setPersonalCarUsage(company.isPersonalCarUsage());
    soapCompany.setTaxType(TaxType.valueOf(company.getTaxType().toString()));
    return soapCompany;
  }

  private pl.coderstrust.model.Company soapCompanyToCompany(Company company) {
    CompanyBuilder builder = new CompanyBuilder(company.getName());
    builder.setIssueDate(LocalDate.parse(company.getCustomerIssueDate()));
    builder.setAddress(company.getAddress());
    builder.setCity(company.getCity());
    builder.setZipCode(company.getZipCode());
    builder.setNip(company.getNip());
    builder.setBankAccoutNumber(company.getBankAccoutNumber());
    builder.setTaxType(pl.coderstrust.model.TaxType.valueOf(company.getTaxType().value()));
    builder.setIsCarPersonalUsage(company.isPersonalCarUsage());
    return builder.build();
  }

  private List<pl.coderstrust.model.InvoiceEntry> soapEntriesToEntries(List<InvoiceEntry> entries) {
    int productsCount = entries.size();
    ArrayList<pl.coderstrust.model.InvoiceEntry> invoiceEntries = new ArrayList<>(productsCount);

    for (InvoiceEntry entry : entries) {
      invoiceEntries.add(
          new pl.coderstrust.model.InvoiceEntry(soapProductToProduct(entry.getProduct()),
              entry.getAmount()));
    }
    return invoiceEntries;
  }

  private pl.coderstrust.model.Product soapProductToProduct(Product product) {
    ProductBuilder builder = new ProductBuilder(product.getName(),
        product.getNetValue().doubleValue());
    builder.setDescription(product.getDescription());
    builder.setVatRate(pl.coderstrust.model.Vat.valueOf(product.getVatRate().value()));
    builder
        .setProductType(pl.coderstrust.model.ProductType.valueOf(product.getProductType().value()));
    return builder.build();
  }
}
