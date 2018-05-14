package pl.coderstrust.e2e.performanceTests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PerformanceTestV1 extends AbstractPerformanceTests {

  @BeforeClass
  public void setupClass() {
    currentDate = LocalDate.now();

    for (int i = 0; i < config.getTestInvoicesCount(); i++) {
      testInvoices.add(generator.getTestInvoice(i + 1,
          config.getDefaultEntriesCount()));
      testInvoices.get(i).setIssueDate(currentDate.plusYears(i));
      testInvoices.get(i).setPaymentDate(currentDate.plusYears(i).plusDays(15));
    }
  }

  @BeforeMethod
  public void setupMethod() {
    testInvoice = generator
        .getTestInvoice(config.getDefaultTestInvoiceNumber(), config.getDefaultEntriesCount());
  }

  @Override
  protected synchronized long addInvoice(Invoice testInvoice) {
    Response ServiceResponse = given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .post(getInvoicePath());
    return TestUtils.getInvoiceIdFromServiceResponse(ServiceResponse.print());
  }

  @Override
  protected List<Invoice> getAllInvoicesFromDatabase() {
    String response2 = given()
            .contentType("application/json")
            .get(TestUtils.getV1InvoicePath())
            .body().print();
    return mapper.toInvoiceList(response2);
  }

  @Override
  protected List<Company> getAllCompaniesFromDatabase() {
    List<Company> emptyList = new ArrayList<>(0);
    return emptyList;
  }

  @Override
  protected String getInvoicePath() {
    return TestUtils.getV1InvoicePath();
  }

  @Override
  protected String getInvoicePathWithInvoiceId(long invoiceId) {
    return TestUtils.getV1InvoicePathWithInvoiceId(invoiceId);
  }

  @Override
  protected String getInvoicePathWithDateRange(LocalDate dateFrom, LocalDate dateTo) {
    return TestUtils.getV1InvoicePath() + "?startDate=" + dateFrom + "&endDate=" + dateTo;
  }
}