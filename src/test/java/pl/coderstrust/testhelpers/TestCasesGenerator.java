package pl.coderstrust.testhelpers;

import org.springframework.stereotype.Service;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.CompanyBuilder;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceBuilder;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentState;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.model.Product;
import pl.coderstrust.model.ProductBuilder;
import pl.coderstrust.model.ProductType;
import pl.coderstrust.model.Vat;
import pl.coderstrust.service.taxservice.Rates;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestCasesGenerator {


  public Invoice getTestInvoice(int invoiceNumber, int entriesCount) {

    String idVisible = "idVisible_" + Integer.toString(invoiceNumber);
    Company buyer = getTestCompany(invoiceNumber, "buyer_");
    Company seller = getTestCompany(invoiceNumber, "seller_");
    InvoiceBuilder builder = new InvoiceBuilder(invoiceNumber, buyer.getName(), seller.getName());
    builder.setVisibleId(idVisible);
    builder.setBuyer(buyer);
    builder.setSeller(seller);
    LocalDate dateIssue = LocalDate.of(2019, 3, 1);
    builder.setIssueDate(dateIssue);
    builder.setPaymentDate(dateIssue.plusDays(15));
    builder.setProducts(getTestEntries(invoiceNumber, entriesCount));
    builder.setPaymentState(PaymentState.NOT_PAID);

    return builder.build();
  }

  public Company getTestCompany(int invoiceNumber, String prefix) {
    String name = prefix + "name_" + Integer.toString(invoiceNumber);
    CompanyBuilder builder = new CompanyBuilder(name);
    LocalDate dateIssue = LocalDate.of(2019, 3, 1);
    builder.setIssueDate(dateIssue);
    builder.setAddress(prefix + "address_" + Integer.toString(invoiceNumber));
    builder.setCity(prefix + "city_" + Integer.toString(invoiceNumber));
    builder.setZipCode(prefix + "zipCode_" + Integer.toString(invoiceNumber));
    builder.setNip(prefix + "nip_" + Integer.toString(invoiceNumber));
    builder.setBankAccoutNumber(prefix + "bankAccoutNumber_" + Integer.toString(invoiceNumber));

    return builder.build();
  }


  public List<InvoiceEntry> getTestEntries(int invoiceNumber, int productsCount) {

    ArrayList<InvoiceEntry> entries = new ArrayList<>(productsCount);
    for (int i = 1; i <= productsCount; i++) {
      entries.add(new InvoiceEntry(getTestProduct(invoiceNumber, i), i));
    }
    return entries;
  }

  public Product getTestProduct(int invoiceNumber, int productCount) {

    String name = "name_" + Integer.toString(invoiceNumber) + "_" + Integer.toString(productCount);
    double netValue = invoiceNumber;
    ProductBuilder builder = new ProductBuilder(name, netValue);
    builder.setDescription(name + "_" + "description_" + Integer.toString(invoiceNumber));
    builder.setVatRate(Vat.VAT_23);
    builder.setProductType(ProductType.OTHER);
    return builder.build();
  }

  public List<Payment> createPensionInsurancePaymentsForYear(int year) {
    LocalDate date = LocalDate.of(year, 1, 10).minusMonths(1);
    List<Payment> paymentsList = new ArrayList<>();

    for (int i = 1; i <= 12; i++) {
      Payment payment = new Payment(i, date.plusMonths(i), Rates.PENSION_INSURANCE.getValue(),
          PaymentType.PENSION_INSURANCE);
      paymentsList.add(payment);
    }
    return paymentsList;
  }

  public List<Payment> createHealthInsurancePaymentsForYear(int year) {
    LocalDate date = LocalDate.of(year, 1, 10);
    List<Payment> paymentsList = new ArrayList<>();

    for (int i = 1; i <= 12; i++) {
      Payment payment = new Payment(i, date.plusMonths(i), BigDecimal.valueOf(300),
          PaymentType.HEALTH_INSURANCE);
      paymentsList.add(payment);
    }
    return paymentsList;
  }

  public List<Payment> createIncomeTaxAdvancePaymentsForYear(int year) {
    LocalDate date = LocalDate.of(year, 1, 20).minusMonths(1);
    List<Payment> paymentsList = new ArrayList<>();

    for (int i = 1; i <= 12; i++) {
      Payment payment = new Payment(i, date.plusMonths(i), BigDecimal.valueOf(i * 50),
          PaymentType.INCOME_TAX_ADVANCE);
      paymentsList.add(payment);
    }
    return paymentsList;
  }

}
