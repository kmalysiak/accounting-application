package pl.coderstrust.e2e.V2Tests;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pl.coderstrust.e2e.AbstractValidInputTests;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Invoice;

import java.time.LocalDate;

public class ValidInputTest extends AbstractValidInputTests {

  private TestsConfiguration config = new TestsConfiguration();

  private long testBuyerId;


  @BeforeClass
  public void setupClass() {
    for (int i = 0; i < TestsConfiguration.TEST_INVOICES_COUNT; i++) {
      testInvoices.add(generator.getTestInvoice(i + 1,
          TestsConfiguration.DEFAULT_ENTRIES_COUNT));
      testInvoices.get(i).setIssueDate(currentDate.plusYears(i));
      testInvoices.get(i).setPaymentDate(currentDate.plusYears(i).plusDays(15));
      testInvoices.get(i).getSeller()
          .setId(TestUtils.registerCompany(testInvoices.get(i).getSeller()));
    }
  }

  @BeforeMethod
  public void setupMethod() {
    testInvoice = TestUtils.getTestInvoiceWithRegisteredBuyerSeller();
    testBuyerId = testInvoice.getBuyer().getId();
  }

  @Override
  protected String getInvoicePath() {
    return TestUtils.getV2InvoicePath(testBuyerId);
  }

  @Override
  protected long addInvoice(Invoice testInvoice) {
    Response ServiceResponse = given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .post(getInvoicePath());
    return TestUtils.getInvoiceIdFromServiceResponse(ServiceResponse.print());
  }

  @Override
  protected String getInvoicePathWithInvoiceId(long invoiceId) {
    return TestUtils.getV2InvoicePathWithInvoiceId(testBuyerId, invoiceId);
  }

  @Override
  protected String getInvoicePathWithDateRange(LocalDate dateFrom, LocalDate dateTo) {
    return TestUtils.getV2InvoicePath(testBuyerId) + "?startDate=" + dateFrom + "&endDate="
        + dateTo;
  }
}