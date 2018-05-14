package pl.coderstrust.e2e.V1Tests;

import pl.coderstrust.e2e.AbstractInvalidInputTests;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Invoice;

public class InvalidInputTests extends AbstractInvalidInputTests {


  @Override
  protected String getBasePath() {
    return TestUtils.getV1InvoicePath();
  }

  @Override
  protected Invoice getDefaultTestInvoice() {
    return generator
        .getTestInvoice(TestsConfiguration.DEFAULT_TEST_INVOICE_NUMBER,
            TestsConfiguration.DEFAULT_ENTRIES_COUNT);
  }


}

