package pl.coderstrust.e2e.V2Tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.Test;
import pl.coderstrust.e2e.AbstractInvalidInputTests;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.model.Messages;

public class InvalidInputTest extends AbstractInvalidInputTests {

  private long testBuyerId;
  private long otherCompanyId;

  @Override
  protected String getBasePath() {
    return TestUtils.getV2InvoicePath(testBuyerId);
  }

  @Test
  public void shouldNotAddInvoiceWhenCompanyIdNotExist() {
    Invoice testInvoice = getDefaultTestInvoice();
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .post(TestUtils.getV2InvoicePath(otherCompanyId + 1))
        .then()
        .assertThat()
        .statusCode(TestsConfiguration.SERVER_ENTRY_NOT_EXIST_STATUS_CODE);
  }

  @Override
  protected Invoice getDefaultTestInvoice() {
    Invoice testInvoice;
    testInvoice = TestUtils.getTestInvoiceWithRegisteredBuyerSeller();
    testBuyerId = testInvoice.getBuyer().getId();
    otherCompanyId = TestUtils.registerCompany(testInvoice.getSeller());

    return testInvoice;
  }

  @Test
  public void shouldNotAddInvoiceWhenCompanyNotMatch() {
    Invoice testInvoice = getDefaultTestInvoice();
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .post(TestUtils.getV2InvoicePath(otherCompanyId))
        .then()
        .assertThat()
        .body(containsString(Messages.COMPANY_ID_NOT_MATCH));
  }

  @Test
  public void shouldNotGetInvoiceWhenCompanyIdNotMatch() {
    Invoice testInvoice = getDefaultTestInvoice();
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .get(TestUtils.getV2InvoicePathWithInvoiceId(otherCompanyId, testInvoice.getId()))
        .then()
        .assertThat()
        .body(containsString(""));
  }

  @Test
  public void shouldNotUpdateInvoiceWhenCompanyIdNotMatch() {
    Invoice testInvoice = getDefaultTestInvoice();
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .put(TestUtils.getV2InvoicePathWithInvoiceId(otherCompanyId, testInvoice.getId()))
        .then()
        .assertThat()
        .body(containsString(Messages.COMPANY_ID_NOT_MATCH));
  }

  @Test
  public void shouldNotDeleteInvoiceWhenCompanyIdNotMatch() {
    Invoice testInvoice = getDefaultTestInvoice();
    given()
        .contentType("application/json")
        .body(testInvoice)
        .when()
        .delete(TestUtils.getV2InvoicePathWithInvoiceId(otherCompanyId, testInvoice.getId()))
        .then()
        .assertThat()
        .body(containsString(Messages.COMPANY_ID_NOT_MATCH));
  }
}
