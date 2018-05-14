package pl.coderstrust.e2e.TaxSummaryScenario;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.model.Payment;
import pl.coderstrust.e2e.model.Rates;
import pl.coderstrust.e2e.model.TaxType;
import pl.coderstrust.e2e.testHelpers.ObjectMapperHelper;
import pl.coderstrust.e2e.testHelpers.TaxSummaryMapBuilder;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaxSummaryTestsScenarios {

  private ObjectMapperHelper objectMapperHelper = new ObjectMapperHelper();
  private TestCasesGenerator generator = new TestCasesGenerator();
  private TestsConfiguration testsConfiguration = new TestsConfiguration();
  private TaxSummaryMapBuilder mapBuilder = new TaxSummaryMapBuilder();
  private LocalDate startDate;

  @BeforeClass
  public void setup() {
    startDate = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
  }

  @Test
  public void shouldAddCompanyPaymentsInvoicesAndReturnTaxSummaryLinearTaxCase() {
    //given
    Company company = TestUtils.getTestCompany();
    company.setIssueDate(startDate);
    long companyId = addCompany(company);
    company.setId(companyId);
    addInvoices(company, 300);
    createAndAddPayments(companyId);

    Map<String, BigDecimal> expected = mapBuilder.setIncome(97500).setCosts(11700)
        .setIncomeMinusCosts(85800)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84).setTaxCalculationBase(79625.16).setIncomeTax(15128.78)
        .setIncomeTaxPaid(3900).setHealthInsurancePaid(3600).setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(8128.78).build();

    System.out.println("@@@@@@@" + expected);

    given().when()
        .get("/taxSummary/" + String.valueOf(companyId) + "/" + String.valueOf(startDate.getYear()))

        .then().assertThat().body(jsonEquals(objectMapperHelper.toJson(expected)));
  }

  @Test
  public void shouldAddCompanyPaymentsInvoicesAndReturnTaxSummaryProgressiveTaxCaseLowThreshold() {
    //given
    Company company = TestUtils.getTestCompany();
    company.setTaxType(TaxType.PROGRESIVE);
    company.setIssueDate(startDate);
    long companyId = addCompany(company);
    company.setId(companyId);
    addInvoices(company, 300);
    createAndAddPayments(companyId);

    Map<String, BigDecimal> expected = mapBuilder.setIncome(97500).setCosts(11700)
        .setIncomeMinusCosts(85800)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84).setTaxCalculationBase(79625.16).setIncomeTax(14332.53)
        .setDecresingTaxAmount(Rates.DECREASING_TAX_AMOUNT.getValue().doubleValue())
        .setIncomeTaxPaid(3900).setHealthInsurancePaid(3600).setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(6776.51).build();

    given().when()
        .get("/taxSummary/" + String.valueOf(companyId) + "/" + String.valueOf(startDate.getYear()))
        .then().assertThat().body(jsonEquals(objectMapperHelper.toJson(expected)));
  }

  @Test
  public void shouldAddCompanyPaymentsInvoicesAndReturnTaxSummaryProgressiveTaxCaseHighThreshold() {
    //given
    Company company = TestUtils.getTestCompany();
    company.setTaxType(TaxType.PROGRESIVE);
    company.setIssueDate(startDate);
    long companyId = addCompany(company);
    company.setId(companyId);
    addInvoices(company, 600);
    createAndAddPayments(companyId);

    Map<String, BigDecimal> expected = mapBuilder.setIncome(195000).setCosts(23400)
        .setIncomeMinusCosts(171600)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84).setTaxCalculationBase(165425.16).setIncomeTax(40962.13)
        .setIncomeTaxPaid(3900).setHealthInsurancePaid(3600).setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(33962.13).build();

    given().when()
        .get("/taxSummary/" + String.valueOf(companyId) + "/" + String.valueOf(startDate.getYear()))

        .then().assertThat().body(jsonEquals(objectMapperHelper.toJson(expected)));
  }

  private long addCompany(Company company) {
    Response serviceRespone = given().contentType("application/json")
        .body(objectMapperHelper.toJson(company)).when().post("/v2/company");
    return TestUtils.getIdFromServiceResponse(serviceRespone.print());
  }

  private void addInvoices(Company company, int amountMultiplier) {
    List<Invoice> incomeInvoices = new ArrayList<>();
    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 14));
      invoice.setSeller(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier * i));
      incomeInvoices.add(invoice);

      given().contentType("application/json").body(objectMapperHelper.toJson(invoice)).when()
          .post(TestUtils.getV2InvoicePath(company.getId()));
    }
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 28));
      invoice.setBuyer(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier / 2 * i));
      given().contentType("application/json").body(objectMapperHelper.toJson(invoice)).when()
          .post(TestUtils.getV2InvoicePath(company.getId()));
    }
  }

  private void createAndAddPayments(long companyId) {
    addPayments(generator.createHealthInsurancePaymentsForYear(startDate.getYear()), companyId);
    addPayments(generator.createPensionInsurancePaymentsForYear(startDate.getYear()), companyId);
    addPayments(generator.createIncomeTaxAdvancePaymentsForYear(startDate.getYear()), companyId);
  }

  private void addPayments(List<Payment> payments, long companyId) {
    payments.forEach(payment -> {

      given().contentType("application/json").body(objectMapperHelper.toJson(payment)).when()
          .post(TestUtils.getPaymentPath(companyId));
    });
  }
}
