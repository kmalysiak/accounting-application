package pl.coderstrust.e2e.V1Tests;

import pl.coderstrust.e2e.TestsConfiguration;

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

  public static String getV1InvoicePath() {
    return "/v1/invoice/";
  }

  public static String getV1InvoicePathWithInvoiceId(long invoiceId) {
    return getV1InvoicePath() + String.valueOf(invoiceId);
  }


}
