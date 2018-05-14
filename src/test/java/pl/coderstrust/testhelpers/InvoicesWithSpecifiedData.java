package pl.coderstrust.testhelpers;

import pl.coderstrust.model.Company;
import pl.coderstrust.model.CompanyBuilder;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceBuilder;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.PaymentState;
import pl.coderstrust.model.Product;
import pl.coderstrust.model.ProductBuilder;
import pl.coderstrust.model.ProductType;
import pl.coderstrust.model.TaxType;
import pl.coderstrust.model.Vat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvoicesWithSpecifiedData {


  //Polish
  public static final Company getPolishCompanyBuyer() {
    return new CompanyBuilder("P.H. Marian Paździoch")
        .setId(2)
        .setAddress("Bazarowa 3/6")
        .setCity("Wrocław")
        .setBankAccoutNumber("99 1010 2222 3333 4444 5555 6666")
        .setNip("123-456-32-18")
        .setZipCode("00-999")
        .setIsCarPersonalUsage(false)
        .build();
  }

  public static final Company getPolishCompanySeller() {
    return new CompanyBuilder(
        "Ferdynand Kiepski i Syn Sp.zoo")
        .setId(1)
        .setAddress("ćwiartki 3/4")
        .setCity("Wrocław")
        .setBankAccoutNumber("11 1010 2222 3333 4444 5555 6655")
        .setNip("123-456-32-22")
        .setZipCode("00-909")
        .setTaxType(TaxType.LINEAR)
        .setIsCarPersonalUsage(false)
        .build();

  }

  public static final Product getPolishProduct() {
    return new ProductBuilder("Mocny Full", 1.99)
        .setDescription("Piwo Jasne")
        .setVatRate(Vat.VAT_23)
        .setProductType(ProductType.OTHER)
        .build();
  }

  public static List<InvoiceEntry> getPolishProductList() {
    InvoiceEntry invoiceEntry = new InvoiceEntry(getPolishProduct(), 100);
    return new ArrayList<>(Arrays.asList(invoiceEntry));
  }

  public static final Invoice getInvoiceWithPolishData() {
    return new InvoiceBuilder(1, "", "Ferdynand Kiepski i Syn Sp.zoo")
        .setBuyer(getPolishCompanyBuyer())
        .setSeller(getPolishCompanySeller())
        .setIssueDate(LocalDate.of(2025, 12, 24))
        .setPaymentDate(LocalDate.of(2025, 12, 31))
        .setProducts(getPolishProductList())
        .setPaymentState(PaymentState.NOT_PAID)
        .build();

  }
}
