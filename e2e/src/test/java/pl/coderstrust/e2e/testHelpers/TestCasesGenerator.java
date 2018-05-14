package pl.coderstrust.e2e.testHelpers;

import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.model.InvoiceEntry;
import pl.coderstrust.e2e.model.Payment;
import pl.coderstrust.e2e.model.PaymentState;
import pl.coderstrust.e2e.model.PaymentType;
import pl.coderstrust.e2e.model.Product;
import pl.coderstrust.e2e.model.ProductType;
import pl.coderstrust.e2e.model.Rates;
import pl.coderstrust.e2e.model.TaxType;
import pl.coderstrust.e2e.model.Vat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class TestCasesGenerator {


  public Invoice getTestInvoice(int invoiceNumber, int entriesCount) {
    LocalDate dateIssue = LocalDate.of(2018, 10, 1);

    return Invoice.builder()
        .id(invoiceNumber)
        .name("idVisible_" + Integer.toString(invoiceNumber))
        .buyer(getTestCompany(invoiceNumber, "buyer_"))
        .seller(getTestCompany(invoiceNumber, "seller_"))
        .issueDate(dateIssue)
        .paymentDate(dateIssue.plusDays(15))
        .products(getTestEntries(invoiceNumber, entriesCount))
        .paymentState(PaymentState.NOT_PAID)
        .build();
  }

  public Company getTestCompany(int invoiceNumber, String prefix) {
    LocalDate dateIssue = LocalDate.of(2018, 10, 1);
    return Company.builder()
        .id(invoiceNumber)
        .issueDate(dateIssue)
        .name(prefix + "name_" + Integer.toString(invoiceNumber))
        .address(prefix + "address_" + Integer.toString(invoiceNumber))
        .city(prefix + "city_" + Integer.toString(invoiceNumber))
        .zipCode(prefix + "zipCode_" + Integer.toString(invoiceNumber))
        .nip(prefix + "nip_" + Integer.toString(invoiceNumber))
        .bankAccoutNumber(prefix + "bankAccoutNumber_" + Integer.toString(invoiceNumber))
        .taxType(TaxType.LINEAR)
        .personalCarUsage(false)
        .payments(getTestPayments(5))
        .build();
  }


  public List<Payment> getTestPayments(int paymentsCount) {
    List<Payment> testPayments = new ArrayList<>();
    for (int i = 0; i < paymentsCount; i++) {
      testPayments.add(getTestPayment(i, i));
    }
    return testPayments;
  }

  public Payment getTestPayment(long id, int count) {
    return Payment.builder()
        .id(id)
        .issueDate(LocalDate.now().plusYears(id))
        .amount(BigDecimal.valueOf(count))
        .type(PaymentType.HEALTH_INSURANCE)
        .build();
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
    return Product.builder()
        .name(name)
        .netValue(BigDecimal.valueOf(invoiceNumber))
        .vatRate(Vat.VAT_23)
        .description(name + "_" + "description_" + Integer.toString(invoiceNumber))
        .productType(ProductType.ELECTRIONICS)
        .build();
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
