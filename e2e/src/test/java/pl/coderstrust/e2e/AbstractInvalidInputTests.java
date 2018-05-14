package pl.coderstrust.e2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.model.InvoiceEntry;
import pl.coderstrust.e2e.model.Messages;
import pl.coderstrust.e2e.model.Product;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInvalidInputTests {

  protected TestCasesGenerator generator = new TestCasesGenerator();
  private TestsConfiguration config = new TestsConfiguration();

  abstract protected String getBasePath();

  abstract protected Invoice getDefaultTestInvoice();

  @Test(dataProvider = "invalidInvoices")
  public void shouldReturnCorrectMessageWhenAddingInvalidInvoice(Invoice invoice, String message) {
    given()
        .contentType("application/json")
        .body(invoice)

        .when()
        .post(getBasePath())

        .then()
        .assertThat()
        .body(containsString(message));
  }

  @DataProvider(name = "invalidInvoices")
  public Object[][] testCases() {
    ArrayList<Object[]> testCases = new ArrayList<>();

    testCases.add(new Object[]{getInvoiceProductListEmpty(), Messages.PRODUCTS_LIST_EMPTY});
    testCases.add(new Object[]{getInvoicePaymentStateEmpty(), Messages.PAYMENT_STATE_EMPTY});
    testCases.add(new Object[]{getInvoiceCompanyNoName(), Messages.COMPANY_NO_NAME});
    testCases.add(new Object[]{getInvoiceCompanyNoAddress(), Messages.COMPANY_NO_ADRESS});
    testCases.add(new Object[]{getInvoiceCompanyNoCity(), Messages.COMPANY_NO_CITY});
    testCases.add(new Object[]{getInvoiceCompanyNoZipCode(), Messages.COMPANY_NO_ZIPCODE});
    testCases.add(new Object[]{getInvoiceCompanyNoBac(), Messages.COMPANY_NO_BACC});
    testCases.add(new Object[]{getInvoiceCompanyNoNip(), Messages.COMPANY_NO_NIP});
    testCases.add(new Object[]{getInvoiceDateEmpty(), Messages.DATE_EMPTY});
    testCases.add(new Object[]{getInvoiceDateToEarly(), Messages.DATE_TOO_EARLY});
    testCases.add(new Object[]{getInvoiceProductNoName(), Messages.PRODUCT_NO_NAME});
    testCases.add(new Object[]{getInvoiceProductNoDescription(), Messages.PRODUCT_NO_DESCRIPTION});
    testCases.add(new Object[]{getInvoiceProductNoNetValue(), Messages.PRODUCT_NO_NET_VALUE});
    testCases.add(new Object[]{getInvoiceProductWrongNetValue(), Messages.PRODUCT_WRONG_NET_VALUE});
    testCases.add(new Object[]{getInvoiceProductNoVat(), Messages.PRODUCT_NO_VAT});

    return testCases.toArray(new Object[][]{});
  }

  private Invoice getInvoiceProductListEmpty() {
    Invoice testInvoice = getDefaultTestInvoice();
    testInvoice.setProducts(new ArrayList<>());
    return testInvoice;
  }

  private Invoice getInvoicePaymentStateEmpty() {
    Company buyer = getDefaultTestCompany();
    Company seller = getDefaultTestCompany();
    List<InvoiceEntry> entries = generator.getTestEntries(1,
        TestsConfiguration.DEFAULT_ENTRIES_COUNT);

    Invoice testInvoice = new Invoice();
    testInvoice.setId(1);
    testInvoice.setName("sampleInvoice");
    testInvoice.setProducts(entries);
    testInvoice.setBuyer(buyer);
    testInvoice.setSeller(seller);
    testInvoice.setIssueDate(LocalDate.now());
    testInvoice.setPaymentDate(LocalDate.now().plusDays(15));
    return testInvoice;
  }

  private Invoice getInvoiceCompanyNoName() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setName("");
    testInvoice.setSeller(seller);
    return testInvoice;

  }

  private Company getDefaultTestCompany() {
    return generator.getTestCompany(TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER, "company");
  }

  private Invoice getInvoiceCompanyNoAddress() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setAddress("");
    testInvoice.setSeller(seller);
    return testInvoice;
  }

  private Invoice getInvoiceCompanyNoCity() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setCity("");
    testInvoice.setSeller(seller);
    return testInvoice;
  }

  private Invoice getInvoiceCompanyNoZipCode() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setZipCode("");
    testInvoice.setSeller(seller);
    return testInvoice;

  }

  private Invoice getInvoiceCompanyNoBac() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setBankAccoutNumber("");
    testInvoice.setSeller(seller);
    return testInvoice;

  }

  private Invoice getInvoiceCompanyNoNip() {
    Invoice testInvoice = getDefaultTestInvoice();
    Company seller = getDefaultTestCompany();
    seller.setNip("");
    testInvoice.setSeller(seller);
    return testInvoice;

  }

  private Invoice getInvoiceDateEmpty() {
    Invoice testInvoice = getDefaultTestInvoice();
    testInvoice.setIssueDate(null);
    return testInvoice;
  }

  private Invoice getInvoiceDateToEarly() {
    Invoice testInvoice = getDefaultTestInvoice();
    testInvoice.setIssueDate(LocalDate.now().minusDays(1));
    return testInvoice;
  }

  private Invoice getInvoiceProductNoName() {
    Invoice testInvoice = getDefaultTestInvoice();
    Product invalidProduct = getDefaultProduct();
    invalidProduct.setName("");
    testInvoice.setProducts(getInvoiceEntriesWithInvalidProduct(invalidProduct));
    return testInvoice;
  }

  private Invoice getInvoiceProductNoDescription() {
    Invoice testInvoice = getDefaultTestInvoice();
    Product invalidProduct = getDefaultProduct();
    invalidProduct.setDescription(" ");
    testInvoice.setProducts(getInvoiceEntriesWithInvalidProduct(invalidProduct));
    return testInvoice;
  }

  private Invoice getInvoiceProductNoNetValue() {
    Invoice testInvoice = getDefaultTestInvoice();
    Product invalidProduct = getDefaultProduct();
    invalidProduct.setNetValue(null);
    testInvoice.setProducts(getInvoiceEntriesWithInvalidProduct(invalidProduct));
    return testInvoice;

  }

  private Invoice getInvoiceProductWrongNetValue() {
    Invoice testInvoice = getDefaultTestInvoice();
    Product invalidProduct = getDefaultProduct();
    invalidProduct.setNetValue(TestsConfiguration.WRONG_NET_VALUE);
    testInvoice.setProducts(getInvoiceEntriesWithInvalidProduct(invalidProduct));
    return testInvoice;

  }

  private Invoice getInvoiceProductNoVat() {
    Invoice testInvoice = getDefaultTestInvoice();
    Product invalidProduct = getDefaultProduct();
    invalidProduct.setVatRate(null);
    testInvoice.setProducts(getInvoiceEntriesWithInvalidProduct(invalidProduct));
    return testInvoice;
  }

  private List<InvoiceEntry> getDefaultInvoiceEntries() {
    return generator
        .getTestEntries(TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER,
            TestsConfiguration.DEFAULT_ENTRIES_COUNT);
  }

  private Product getDefaultProduct() {
    return generator
        .getTestProduct(TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER,
            TestsConfiguration.DEFAULT_ENTRIES_COUNT);
  }

  private List<InvoiceEntry> getInvoiceEntriesWithInvalidProduct(Product invalidProduct) {
    List<InvoiceEntry> entries = getDefaultInvoiceEntries();
    InvoiceEntry entry = new InvoiceEntry();
    entry.setProduct(invalidProduct);
    entry.setAmount(TestsConfiguration.DEFAULT_PRODUCT_QUANTITY);
    entries.set(TestsConfiguration.DEFAULT_ENTRIES_COUNT - 1, entry);

    return entries;
  }
}