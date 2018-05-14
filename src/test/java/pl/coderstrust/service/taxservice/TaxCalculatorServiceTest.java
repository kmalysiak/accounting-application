package pl.coderstrust.service.taxservice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.model.TaxType;
import pl.coderstrust.service.CompanyService;
import pl.coderstrust.testhelpers.InvoicesWithSpecifiedData;
import pl.coderstrust.testhelpers.TaxSummaryMapBuilder;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorServiceTest {

  private static final long MY_COMPANY_ID = 1;
  private LocalDate startDate = LocalDate.now().plusMonths(1);
  private LocalDate endDate = LocalDate.now().plusYears(1).plusMonths(1).minusDays(1);
  private LocalDate endDateInHalf = LocalDate.now().plusMonths(7).minusDays(1);
  private TestCasesGenerator generator = new TestCasesGenerator();
  private TaxSummaryMapBuilder mapBuilder = new TaxSummaryMapBuilder();

  @Mock
  private Database database;

  @Mock
  private CompanyService companyService;

  @Mock
  private PaymentService paymentService;

  @InjectMocks
  private TaxCalculatorService taxCalculatorService;


  @Test
  public void shouldCalculateIncomeWholeInvoicesInDataRange() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);

    //when
    BigDecimal calculateValue = taxCalculatorService.calculateIncome(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(1170), new BigDecimal(0.001))));
  }

  @Test
  public void shouldCalculateCostWholeInvoicesInDataRange() {
    //given
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesBuyer.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateValue = taxCalculatorService.calculateCost(MY_COMPANY_ID, startDate,
        endDateInHalf);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(315), new BigDecimal(0.001))));
  }

  @Test
  public void shouldNotCalculateIncomeWholeInvoicesOutOfData() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusYears(2).plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);
    //when
    BigDecimal calculateValue = taxCalculatorService.calculateIncome(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(0), new BigDecimal(0.001))));
  }

  @Test
  public void shouldNotCalculateCostWholeInvoicesOutOfData() {
    //given
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusYears(2).plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesBuyer.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateValue = taxCalculatorService.calculateCost(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(0), new BigDecimal(0.001))));
  }

  @Test
  public void shouldCalculateIncomeWholeInvoicesInDataRangeAndOutOfData() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 36; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);
    //when
    BigDecimal calculateValue = taxCalculatorService.calculateIncome(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(1170), new BigDecimal(0.001))));
  }

  @Test
  public void shouldCalculateCostWholeInvoicesInDataRangeAndOutOfData() {
    //given
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 36; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      ;
      invoicesBuyer.add(invoice);
    }
    when(companyService.findEntry(1))
        .thenReturn(InvoicesWithSpecifiedData.getPolishCompanySeller());
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateValue = taxCalculatorService.calculateCost(MY_COMPANY_ID, startDate,
        endDateInHalf);
    //then
    assertThat(calculateValue, is(closeTo(new BigDecimal(315), new BigDecimal(0.001))));
  }

  @Test
  public void shouldCalculateVatOutcomeWholeInvoicesInDataRange() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateOutcomeVat(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(269.1), new BigDecimal(0.006))));
  }

  @Test
  public void shouldCalculateVatIncomeWholeInvoicesInDataRange() {
    //given
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 6; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesBuyer.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateIncomeVat(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(72.45), new BigDecimal(0.006))));
  }

  @Test
  public void shouldNotCalculateVatOutcomeWholeInvoicesOutOfData() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusYears(2).plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateOutcomeVat(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(0), new BigDecimal(0.006))));
  }

  @Test
  public void shouldNotCalculateVatIncomeWholeInvoicesOutOfData() {
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusYears(2).plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesBuyer.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateIncomeVat(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(0), new BigDecimal(0.006))));
  }

  @Test
  public void shouldCalculateVatOutcomeWholeInvoicesInDataRangeAndOutOfRange() {
    //given
    List<Invoice> invoicesSeller = new ArrayList<>();
    for (int i = 1; i <= 36; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesSeller.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoicesSeller);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateOutcomeVat(MY_COMPANY_ID, startDate,
        endDate);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(269.1), new BigDecimal(0.006))));
  }

  @Test
  public void shouldCalculateVatIncomeWholeInvoicesInDataRangeAndOutOfRange() {
    List<Invoice> invoicesBuyer = new ArrayList<>();
    for (int i = 1; i <= 36; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.now().plusMonths(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoicesBuyer.add(invoice);
    }
    when(companyService.findEntry(1))
        .thenReturn(InvoicesWithSpecifiedData.getPolishCompanySeller());
    when(database.getEntries()).thenReturn(invoicesBuyer);
    //when
    BigDecimal calculateVat = taxCalculatorService.calculateIncomeVat(MY_COMPANY_ID, startDate,
        endDateInHalf);
    //then
    assertThat(calculateVat, is(closeTo(new BigDecimal(72.45), new BigDecimal(0.006))));
  }


  @Test
  public void shouldCalculateIncomeTaxAdvanceLinearTax() {
    //given
    final LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 03, 1);
    final LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 3, 31);

    List<Invoice> invoices = new ArrayList<>();
    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setSeller(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoice.getProducts().get(0).getProduct().setNetValue(BigDecimal.valueOf(100 * i));
      invoices.add(invoice);
    }
    for (int i = 1; i <= 5; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setBuyer(InvoicesWithSpecifiedData.getPolishCompanySeller());
      invoice.getProducts().get(0).getProduct().setNetValue(BigDecimal.valueOf(50 * i));
      invoices.add(invoice);
    }

    Payment pensionInsurance = new Payment(1, startDate,
        BigDecimal.valueOf(500), PaymentType.PENSION_INSURANCE);
    Payment healthInsurance = new Payment(2, startDate,
        BigDecimal.valueOf(300), PaymentType.HEALTH_INSURANCE);
    Payment incomeTaxAdvance = new Payment(3, startDate,
        BigDecimal.valueOf(1100), PaymentType.INCOME_TAX_ADVANCE);

    when(database.getEntries()).thenReturn(invoices);
    when(companyService.findEntry(1))
        .thenReturn(InvoicesWithSpecifiedData.getPolishCompanySeller());
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate.plusDays(20), PaymentType.PENSION_INSURANCE))
        .thenReturn(Arrays.asList(pensionInsurance));
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate, PaymentType.INCOME_TAX_ADVANCE))
        .thenReturn(Arrays.asList(incomeTaxAdvance));
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate.plusDays(20), PaymentType.HEALTH_INSURANCE))
        .thenReturn(Arrays.asList(healthInsurance));

    //when
    BigDecimal taxAdvance = taxCalculatorService.calculateIncomeTaxAdvance(
        1, startDate, endDate);
    //then
    assertThat(taxAdvance, is(BigDecimal.valueOf(4579.17)));
  }

  @Test
  public void shouldCalculateProgressiveTaxAdvanceLinearTaxType() {
    //when
    incomeTaxAdvanceCalculatorTestPattern(
        TaxType.LINEAR, 100, BigDecimal.valueOf(4579.17));

  }

  @Test
  public void shouldCalculateProgressiveTaxAdvanceLowTreshold() {
    //when
    incomeTaxAdvanceCalculatorTestPattern(
        TaxType.PROGRESIVE, 100, BigDecimal.valueOf(3710.65));

  }

  @Test
  public void shouldCalculateProgressiveTaxAdvanceHighTreshold() {
    incomeTaxAdvanceCalculatorTestPattern(
        TaxType.PROGRESIVE, 1000, BigDecimal.valueOf(88107.75));
  }

  private void incomeTaxAdvanceCalculatorTestPattern(TaxType type, int amountMultiplier,
      BigDecimal expectedValue) {
    //given
    final LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 03, 1);
    final LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 3, 31);
    Company company = InvoicesWithSpecifiedData.getPolishCompanySeller();
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
    List<Invoice> invoices = new ArrayList<>();
    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setSeller(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier * i));
      invoices.add(invoice);
    }
    for (int i = 1; i <= 5; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i));
      invoice.setBuyer(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier / 2 * i));
      invoices.add(invoice);
    }

    Payment pensionInsurance = new Payment(1, startDate,
        BigDecimal.valueOf(500), PaymentType.PENSION_INSURANCE);
    Payment healthInsurance = new Payment(2, startDate,
        BigDecimal.valueOf(300), PaymentType.HEALTH_INSURANCE);
    Payment incomeTaxAdvance = new Payment(3, startDate,
        BigDecimal.valueOf(1100), PaymentType.INCOME_TAX_ADVANCE);

    when(database.getEntries()).thenReturn(invoices);
    when(companyService.findEntry(1))
        .thenReturn(company);
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate.plusDays(20), PaymentType.PENSION_INSURANCE))
        .thenReturn(Arrays.asList(pensionInsurance));
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate, PaymentType.INCOME_TAX_ADVANCE))
        .thenReturn(Arrays.asList(incomeTaxAdvance));
    when(paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.of(startDate.getYear(), 1, 1),
        endDate.plusDays(20), PaymentType.HEALTH_INSURANCE))
        .thenReturn(Arrays.asList(healthInsurance));

    //when
    BigDecimal output = taxCalculatorService.calculateIncomeTaxAdvance(
        1, startDate, endDate);

    //then
    assertThat(output, is(expectedValue));
  }

  @Test
  public void shouldCalculateTaxSummaryLinearTaxCase() {
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
  public void shouldCalculateTaxSummaryProgressiveLowThresholdTaxCase() {
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
  public void shouldCalculateTaxSummaryProgressiveHighThresholdTaxCase() {
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

  private void taxSummaryTestPattern(TaxType type, int amountMultiplier,
      Map<String, BigDecimal> expected) {
    final LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
    final LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
    //given
    Company company = InvoicesWithSpecifiedData.getPolishCompanySeller();
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
    List<Invoice> invoices = new ArrayList<>();
    for (int i = 1; i <= 25; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 14));
      invoice.setSeller(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier * i));
      invoices.add(invoice);
    }
    for (int i = 1; i <= 12; i++) {
      Invoice invoice = generator.getTestInvoice(i, 1);
      invoice.setIssueDate(startDate.plusDays(i * 28));
      invoice.setBuyer(company);
      invoice.getProducts().get(0).getProduct()
          .setNetValue(BigDecimal.valueOf(amountMultiplier / 2 * i));
      invoices.add(invoice);
    }
    when(database.getEntries()).thenReturn(invoices);
    when(companyService.findEntry(1))
        .thenReturn(company);
    when(paymentService.getPaymentsByTypeAndDate(1,
        startDate,
        endDate.plusDays(20), PaymentType.PENSION_INSURANCE))
        .thenReturn(generator.createPensionInsurancePaymentsForYear(LocalDate.now().getYear()));
    when(paymentService.getPaymentsByTypeAndDate(1,
        startDate,
        endDate.plusDays(20), PaymentType.INCOME_TAX_ADVANCE))
        .thenReturn(generator.createIncomeTaxAdvancePaymentsForYear(LocalDate.now().getYear()));
    when(paymentService.getPaymentsByTypeAndDate(1,
        startDate,
        endDate.plusDays(20), PaymentType.HEALTH_INSURANCE))
        .thenReturn(generator.createHealthInsurancePaymentsForYear(LocalDate.now().getYear()));
    //when
    Map<String, BigDecimal> output = taxCalculatorService
        .taxSummary(1, LocalDate.now().getYear());
    //then
    assertThat(output, is(equalTo(expected)));
  }
}


