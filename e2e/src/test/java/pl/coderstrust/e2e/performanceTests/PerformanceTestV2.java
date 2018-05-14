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

public class PerformanceTestV2 extends AbstractPerformanceTests {

  private long testBuyerId;

  @BeforeClass
  public void setupClass() {
    for (int i = 0; i < config.getTestInvoicesCount(); i++) {
      testInvoices.add(generator.getTestInvoice(i + 1,
          config.getDefaultEntriesCount()));
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
  protected List<Invoice> getAllInvoicesFromDatabase() {
    List<Invoice> allInvoices = new ArrayList<>();
    for (Company c : getAllCompaniesFromDatabase()) {
      long id = c.getId();
      String response2 = given()
              .contentType("application/json")
              .get(TestUtils.getV2InvoicePath(id))
              .body().print();
      allInvoices.addAll(mapper.toInvoiceList(response2));
    }
    return allInvoices;
  }

  @Override
  protected synchronized List<Company> getAllCompaniesFromDatabase() {
    String path = config.getBaseUri() + ":" + config.getBasePort();
    String response = given()
            .body(path)
            .get(TestUtils.getV2CompanyPath())
            .body().print();
    return mapper.toCompanyList(response);
  }

  @Override
  protected String getInvoicePath() {
    return TestUtils.getV2InvoicePath(testBuyerId);
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
  protected String getInvoicePathWithInvoiceId(long invoiceId) {
    return TestUtils.getV2InvoicePathWithInvoiceId(testBuyerId, invoiceId);
  }

  @Override
  protected String getInvoicePathWithDateRange(LocalDate dateFrom, LocalDate dateTo) {
    return TestUtils.getV2InvoicePath(testBuyerId) + "?startDate=" + dateFrom + "&endDate="
        + dateTo;
  }
}