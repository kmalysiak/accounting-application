package pl.coderstrust.e2e.TaxSummaryScenario;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Payment;
import pl.coderstrust.e2e.model.TaxType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

  private static Pattern extractIntFromString = Pattern
      .compile(TestsConfiguration.INT_FROM_STRING_REGEX_PATTERN);

  public static long getIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }

  public static String getV2InvoicePath(long companyId) {
    return "/v2/company/" + String.valueOf(companyId) + "/invoice";
  }

  public static String getV2InvoicePathWithInvoiceId(long companyId, long invoiceId) {
    return getV2InvoicePath(companyId) + String.valueOf(invoiceId);
  }

  public static String getV2CompanyPath() {
    return "/v2/company";
  }

  public static String getPaymentPath(long companyId) {
    return "/payment/" + String.valueOf(companyId);
  }

  public static long registerCompany(Company testCompany) {
    Response ServiceResponse = given()
        .contentType("application/json")
        .body(testCompany)
        .when()
        .post(getV2CompanyPath());
    return getIdFromServiceResponse(ServiceResponse.print());
  }

  public static Company getTestCompany() {
    return Company.builder()
        .name("Coders Trust Poland")
        .address("Javowa 1")
        .city("Warszawa")
        .zipCode("00-222")
        .nip("nip")
        .bankAccoutNumber("bank account")
        .taxType(TaxType.LINEAR)
        .personalCarUsage(false)
        .payments(new ArrayList<Payment>())
        .build();
  }
}
