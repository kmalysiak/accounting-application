package pl.coderstrust.e2e;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.testHelpers.ObjectMapperHelper;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.time.LocalDate;
import java.util.ArrayList;


public abstract class AbstractValidInputTests {

  protected TestCasesGenerator generator = new TestCasesGenerator();
  protected ObjectMapperHelper mapper = new ObjectMapperHelper();
  protected LocalDate currentDate = LocalDate.now();
  protected Invoice testInvoice;
  protected ArrayList<Invoice> testInvoices = new ArrayList<>();

  @Test
  public void shouldReturnCorrectStatusCodeWhenServiceIsUp() {
    given()
        .when()
        .get(getInvoicePath())

        .then()
        .statusCode(TestsConfiguration.SERVER_OK_STATUS_CODE);
  }

  protected abstract String getInvoicePath();

  @Test
  public void shouldCorrectlyAddAndGetInvoiceById() {
    long invoiceId = addInvoice(testInvoice);
    testInvoice.setId(invoiceId);
    given()
        .when()
        .get(getInvoicePathWithInvoiceId(invoiceId)).

        then()
        .assertThat()
        .body(jsonEquals(mapper.toJson(testInvoice)));

  }

  protected abstract String getInvoicePathWithInvoiceId(long invoiceId);

  protected abstract long addInvoice(Invoice testInvoice);

  @Test
  public void shouldCorrectlyUpdateInvoice() {
    long invoiceId = addInvoice(testInvoice);
    Invoice updatedInvoice = generator.getTestInvoice(
        TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER + 1,
        TestsConfiguration.DEFAULT_ENTRIES_COUNT);
    updatedInvoice.setId(invoiceId);
    updatedInvoice.setBuyer(testInvoice.getBuyer());
    updatedInvoice.setSeller(testInvoice.getSeller());
    given()
        .contentType("application/json")
        .body(updatedInvoice)
        .when()
        .put(getInvoicePathWithInvoiceId(invoiceId));

    given()
        .when()
        .get(getInvoicePathWithInvoiceId(invoiceId))

        .then()
        .assertThat()
        .body(jsonEquals(mapper.toJson(updatedInvoice)));
  }

  @Test
  public void shouldCorrectlyDeleteInvoiceById() {
    long invoiceId = addInvoice(testInvoice);
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .delete(getInvoicePathWithInvoiceId(invoiceId));

    given()
        .when()
        .get(getInvoicePathWithInvoiceId(invoiceId))

        .then()
        .assertThat()
        .body(equalTo(""));
  }

  @Test
  public void shouldAddSeveralInvoicesAndReturnCorrectMessage() {
    for (int i = 0; i < TestsConfiguration.TEST_INVOICES_COUNT; i++) {
      given()
          .contentType("application/json")
          .body(testInvoice)

          .when()
          .post(getInvoicePath())

          .then()
          .assertThat()
          .body(containsString("Entry added under id :"));
    }
  }

  @Test(dataProvider = "validDates")
  public void shouldAddSeveralInvoicesAndFindThemByIssueDate(LocalDate newDate) {
    int invoicesAtDateCount = getInvoicesCountForDateRange(newDate, newDate);
    testInvoice.setIssueDate(newDate);
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .post(getInvoicePath());

    int invoicesAdded = getInvoicesCountForDateRange(newDate, newDate) - invoicesAtDateCount;
    Assert.assertEquals(invoicesAdded, 1);
  }

  @DataProvider(name = "validDates")
  public Object[] validDatesProvider() {
    Object[] validDates = new Object[10];
    for (int i = 0; i < TestsConfiguration.TEST_INVOICES_COUNT; i++) {
      validDates[i] = LocalDate.now().plusYears(i);
    }
    return validDates;
  }

  private int getInvoicesCountForDateRange(LocalDate dateFrom, LocalDate dateTo) {
    String path = getInvoicePathWithDateRange(dateFrom, dateTo);
    String response = given()
        .get(path)
        .body().print();
    return mapper.toInvoiceList(response).size();
  }

  protected abstract String getInvoicePathWithDateRange(LocalDate dateFrom, LocalDate dateTo);
}


