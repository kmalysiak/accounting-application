package pl.coderstrust.e2e.performanceTests;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

  private static TestsConfiguration testsConfiguration = new TestsConfiguration();

  private static Pattern extractIntFromString = Pattern
      .compile(testsConfiguration.getIntFromStringRegexPattern());

  public static long getInvoiceIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }

  public static String getV1InvoicePath() {
    return "/v1/invoice/";
  }

  public static String getV1InvoicePathWithInvoiceId(long invoiceId) {
    return getV1InvoicePath() + String.valueOf(invoiceId);
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
        .getTestInvoice(testsConfiguration.getDefaultTestInvoiceNumber(),
            testsConfiguration.getDefaultEntriesCount());

    long sellerId = registerCompany(testInvoice.getSeller());
    long buyerId = registerCompany(testInvoice.getBuyer());

    testInvoice.getSeller().setId(sellerId);
    testInvoice.getBuyer().setId(buyerId);

    return testInvoice;

  }

}
