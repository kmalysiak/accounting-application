package pl.coderstrust.service.taxservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.TaxType;
import pl.coderstrust.service.CompanyService;
import pl.coderstrust.testhelpers.InvoicesWithSpecifiedData;
import pl.coderstrust.testhelpers.TaxSummaryMapBuilder;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//TODO This class needs to be cleaned and simplified

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class TaxCalculatorControllerTest {

  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final String DEFAULT_PATH = "/v2/company/1/invoice";
  private static final String MY_COMPANY_ID =
      "/1?startDate=";
  @Autowired
  CompanyService companyService;
  private LocalDate startDate = LocalDate.now().plusMonths(1);
  private LocalDate endDate = LocalDate.now().plusYears(1).plusMonths(1).minusDays(1);
  private LocalDate endDateInHalf = LocalDate.now().plusMonths(7).minusDays(1);
  private TaxSummaryMapBuilder mapBuilder = new TaxSummaryMapBuilder();
  @Autowired
  private TestCasesGenerator generator;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private MockMvc mockMvc;

  @Before
  public void defaultGiven() {
    companyService.addEntry(InvoicesWithSpecifiedData.getPolishCompanySeller());
  }

  @Test
  public void shouldCalculateIncome() throws Exception {
    //given
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/income" + MY_COMPANY_ID + startDate + "&endDate=" + endDate))
        .andExpect(handler().methodName("calculateIncome"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(1170.0)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("1170.00")));
  }

  @Test
  public void shouldReturnErrorCausedByEndDateBeforeStartDate() throws Exception {
    //when
    this.mockMvc
        .perform(
            get("/income" + MY_COMPANY_ID + endDate + "&endDate=" + startDate))
        .andExpect(handler().methodName("calculateIncome"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  @Test
  public void shouldCalculateCost() throws Exception {
    //given
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/cost" + MY_COMPANY_ID + startDate + "&endDate=" + endDateInHalf))
        .andExpect(handler().methodName("calculateCost"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(315.0)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("315.00")));
  }

  @Test
  public void shouldCalculateIncomeTax() throws Exception {
    //given
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/incomeTax" + MY_COMPANY_ID + startDate + "&endDate=" + endDate))
        .andExpect(handler().methodName("calculateIncomeTax"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(855.0)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("855.00")));
  }


  @Test
  public void shouldCalculateIncomeVat() throws Exception {
    //given
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/incVat" + MY_COMPANY_ID + startDate + "&endDate=" + endDateInHalf))
        .andExpect(handler().methodName("calculateIncomeVat"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(72.45)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("72.45")));
  }

  @Test
  public void shouldCalculateOutcomeVat() throws Exception {
    //given
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/outVat" + MY_COMPANY_ID + startDate + "&endDate=" + endDate))
        .andExpect(handler().methodName("calculateOutcomeVat"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(269.1)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("269.10")));
  }

  @Test
  public void shouldCalculateDifferenceVat() throws Exception {
    //given
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());

      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(
            get("/diffVat" + MY_COMPANY_ID + startDate + "&endDate=" + endDate))
        .andExpect(handler().methodName("calculateDifferenceVat"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(196.65)))
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    assertThat(response, is(equalTo("196.65")));
  }

  @Test
  public void shouldCalculateTaxSummaryProgressiveLowThresholdTaxCase() throws Exception {
    Map<String, BigDecimal> expected = mapBuilder
        .setIncome(97500)
        .setCosts(11700)
        .setIncomeMinusCosts(85800)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84)
        .setTaxCalculationBase(79625.16)
        .setIncomeTax(14332.53)
        .setDecresingTaxAmount(Rates.DECREASING_TAX_AMOUNT.getValue().doubleValue())
        .setIncomeTaxPaid(3900)
        .setHealthInsurancePaid(3600)
        .setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(6776.51)
        .build();

    taxSummaryTestPattern(TaxType.PROGRESIVE, 300, expected);
  }

  @Test
  public void shouldCalculateTaxSummaryProgressiveHighThresholdTaxCase() throws Exception {
    Map<String, BigDecimal> expected = mapBuilder
        .setIncome(195000)
        .setCosts(23400)
        .setIncomeMinusCosts(171600)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84)
        .setTaxCalculationBase(165425.16)
        .setIncomeTax(40962.13)
        .setIncomeTaxPaid(3900)
        .setHealthInsurancePaid(3600)
        .setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(33962.13)
        .build();

    taxSummaryTestPattern(TaxType.PROGRESIVE, 600, expected);
  }

  @Test
  public void shouldReturnTaxSummaryLinearTaxCase() throws Exception {
    Map<String, BigDecimal> expected = mapBuilder
        .setIncome(97500)
        .setCosts(11700)
        .setIncomeMinusCosts(85800)
        .setPensionInsuranceMonthlyRate(Rates.PENSION_INSURANCE.getValue().doubleValue())
        .setPensionInsurancePaid(6174.84)
        .setTaxCalculationBase(79625.16)
        .setIncomeTax(15128.78)
        .setIncomeTaxPaid(3900)
        .setHealthInsurancePaid(3600)
        .setHealthInsurancetoSusbstract(3100)
        .setIncomeTaxToPay(8128.78)
        .build();

    taxSummaryTestPattern(TaxType.LINEAR, 300, expected);
  }

  @Test
  public void shouldReturnErrorCausedByWrongYear() throws Exception {
    this.mockMvc
        .perform(
            get("/taxSummary/2/" + String.valueOf("-1")))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.INCORRECT_YEAR));
  }

  @Test
  public void shouldCalculateIncomeTaxAdvance() throws Exception {
    //given
    final LocalDate startDate = LocalDate.of(LocalDate.now().getYear(),
        LocalDate.now().getMonthValue() + 1, 1);
    final LocalDate endDate = startDate.plusMonths(1).minusDays(1);
    Company company = InvoicesWithSpecifiedData.getPolishCompanySeller();
    company.setId(1);
    this.mockMvc
        .perform(post("/v2/company")
            .content(json(company))
            .contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setSeller(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(100 * i));
      this.mockMvc
          .perform(post("/v2/company/1/invoice")
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setBuyer(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(50 * i));
      this.mockMvc
          .perform(post("/v2/company/1/invoice")
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }

    //when
    String response = this.mockMvc
        .perform(
            get("/incomeTaxAdvance/1?startDate=" + startDate + "&endDate=" + endDate))
        .andExpect(handler().methodName("calculateIncomeTaxAdvance"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then

    System.out.println("@@@@@@##@!#!#@!!@@#@!#@!" + response.toString());
    assertThat(response, is(equalTo("5434.00")));
  }

  @Test
  public void shouldReturnErrorCausedByWrongYearInIncomeTaxCalculations() throws Exception {
    this.mockMvc
        .perform(
            get("/incomeTaxAdvance/1?startDate=" + endDate + "&endDate=" + startDate))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  @Test
  public void shouldReturnErrorCausedByStartDateAfterEndDateInIncomeVat() throws Exception {
    this.mockMvc
        .perform(
            get("/incVat/1?startDate=" + endDate + "&endDate=" + startDate))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  @Test
  public void shouldReturnErrorCausedByStartDateAfterEndDateInDiffVat() throws Exception {
    this.mockMvc
        .perform(
            get("/diffVat/1?startDate=" + endDate + "&endDate=" + startDate))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  @Test
  public void shouldReturnErrorCausedByStartDateAfterEndDateInOutcomeVat() throws Exception {
    this.mockMvc
        .perform(
            get("/diffVat/1?startDate=" + endDate + "&endDate=" + startDate))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  @Test
  public void shouldReturnErrorCausedByStartDateAfterEndDateInIncomeTax() throws Exception {
    this.mockMvc
        .perform(
            get("/incomeTax/1?startDate=" + endDate + "&endDate=" + startDate))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.END_BEFORE_START));
  }

  private void taxSummaryTestPattern(TaxType type, int amountMultiplier,
      Map<String, BigDecimal> expected) throws Exception {
    LocalDate startDate = LocalDate.of(
        LocalDate.now().plusYears(1).getYear(), 1, 1);
    LocalDate endDate = LocalDate.of(
        LocalDate.now().plusYears(1).getYear(), 12, 31);
    //given
    Company company = InvoicesWithSpecifiedData.getPolishCompanySeller();
    company.setId(2);
    switch (type) {
      case LINEAR: {
        company.setTaxType(TaxType.LINEAR);
        break;
      }
      case PROGRESIVE: {
        company.setTaxType(TaxType.PROGRESIVE);
        break;
      }
      default:
        throw new IllegalArgumentException("Wrong argument");
    }
    this.mockMvc
        .perform(post("/v2/company")
            .content(json(company))
            .contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 14));
      invoice.setSeller(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier * i));
      this.mockMvc
          .perform(post("/v2/company/2/invoice")
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 28));
      invoice.setBuyer(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier / 2 * i));
      this.mockMvc
          .perform(post("/v2/company/2/invoice")
              .content(json(invoice))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    List<Payment> pensionInsurance =
        generator.createPensionInsurancePaymentsForYear(startDate.getYear());
    for (int i = 0; i < 12; i++) {
      this.mockMvc
          .perform(post("/payment/2")
              .content(json(pensionInsurance.get(i)))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    List<Payment> incomeTaxAdvance =
        generator.createIncomeTaxAdvancePaymentsForYear(startDate.getYear());
    for (int i = 0; i < 12; i++) {
      this.mockMvc
          .perform(post("/payment/2")
              .content(json(incomeTaxAdvance.get(i)))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    List<Payment> healthInsruace =
        generator.createHealthInsurancePaymentsForYear(startDate.getYear());
    for (int i = 0; i < 12; i++) {
      this.mockMvc
          .perform(post("/payment/2")
              .content(json(healthInsruace.get(i)))
              .contentType(CONTENT_TYPE))
          .andExpect(status().isOk());
    }
    String response = this.mockMvc
        .perform(
            get("/taxSummary/2/" + String.valueOf(startDate.getYear())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    Map<String, BigDecimal> output = getMapFromResponse(response);
    assertThat(output, is(equalTo(expected)));
  }

  private String json(Invoice invoice) throws Exception {
    return mapper.writeValueAsString(invoice);
  }

  private String json(Company company) throws Exception {
    return mapper.writeValueAsString(company);
  }

  private String json(Payment payment) throws Exception {
    return mapper.writeValueAsString(payment);
  }

  private Map<String, BigDecimal> getMapFromResponse(String response) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    return mapper.readValue(
        response,
        new TypeReference<Map<String, BigDecimal>>() {
        });
  }
}