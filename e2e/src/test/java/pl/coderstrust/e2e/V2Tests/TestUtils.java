package pl.coderstrust.e2e.V2Tests;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestUtils {

  private static Pattern extractIntFromString = Pattern
      .compile(TestsConfiguration.INT_FROM_STRING_REGEX_PATTERN);

  public static long getInvoiceIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }

  public static String getV2InvoicePath(long companyId) {
    return "v2/company/" + String.valueOf(companyId) + "/invoice/";
  }

  public static String getV2InvoicePathWithInvoiceId(long companyId, long invoiceId) {
    return getV2InvoicePath(companyId) + String.valueOf(invoiceId);
  }

  public static String getV2CompanyPath() {
    return "v2/company";
  }

  public static long registerCompany(Company testCompany) {
    Response ServiceResponse = given()
        .contentType("application/json")
        .body(testCompany)
        .when()
        .post(getV2CompanyPath());
    return getInvoiceIdFromServiceResponse(ServiceResponse.print());
  }

  public static Invoice getTestInvoiceWithRegisteredBuyerSeller() {
    Invoice testInvoice;

    testInvoice = new TestCasesGenerator()
        .getTestInvoice(TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER,
            TestsConfiguration.DEFAULT_ENTRIES_COUNT);

    long sellerId = registerCompany(testInvoice.getSeller());
    long buyerId = registerCompany(testInvoice.getBuyer());

    testInvoice.getSeller().setId(sellerId);
    testInvoice.getBuyer().setId(buyerId);

    return testInvoice;

  }

}
