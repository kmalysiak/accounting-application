package pl.coderstrust.service.taxservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.model.Product;
import pl.coderstrust.model.ProductType;
import pl.coderstrust.model.TaxType;
import pl.coderstrust.service.CompanyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Resource;

@Service
public class TaxCalculatorService {

  private CompanyService companyService;
  private PaymentService paymentService;

  @Resource(name = "invoicesDatabase")
  private Database<Invoice> database;

  @Autowired
  public TaxCalculatorService(Database<Invoice> database, CompanyService companyService,
      PaymentService paymentService) {
    this.database = database;
    this.companyService = companyService;
    this.paymentService = paymentService;
  }

  public BigDecimal calculateIncome(long companyId, LocalDate beginDate, LocalDate endDate) {
    Function<Invoice, BigDecimal> getValueFunction = x -> getCompanyIdSeller(companyId).test(x)
        ? getNetValue(x) : BigDecimal.ZERO;
    return calculatePattern(getValueFunction, beginDate, endDate);
  }

  public BigDecimal calculateCost(long companyId, LocalDate beginDate, LocalDate endDate) {
    Function<Invoice, BigDecimal> getValueFunction = x -> getCompanyIdBuyer(companyId).test(x)
        ? getCostValue(x, companyService.findEntry(companyId)) : BigDecimal.ZERO;
    return calculatePattern(getValueFunction, beginDate, endDate);
  }

  public BigDecimal calculateIncomeVat(long companyId, LocalDate beginDate, LocalDate endDate) {
    Function<Invoice, BigDecimal> getValueFunction = x -> getCompanyIdBuyer(companyId).test(x)
        ? getIncomeVatValue(x, companyService.findEntry(companyId)) : BigDecimal.ZERO;
    return calculatePattern(getValueFunction, beginDate, endDate);
  }

  public BigDecimal calculateOutcomeVat(long companyId, LocalDate beginDate,
      LocalDate endDate) {
    Function<Invoice, BigDecimal> getValueFunction = x -> getCompanyIdSeller(companyId).test(x)
        ? getVatValue(x) : BigDecimal.ZERO;
    return calculatePattern(getValueFunction, beginDate, endDate);
  }

  public BigDecimal calculateIncomeTaxAdvance(long companyId,
      LocalDate startDate, LocalDate endDate) {
    startDate = LocalDate.of(endDate.getYear(), 1, 1);
    BigDecimal base = caluclateTaxBase(companyId, startDate, endDate);
    BigDecimal sumHealthInsurancePaid = calculateSpecifiedTypeCostsBetweenDates(
        companyId, startDate, endDate.plusDays(20), PaymentType.HEALTH_INSURANCE);
    BigDecimal sumIncomeTaxesAdvancePaid = calculateSpecifiedTypeCostsBetweenDates(
        companyId, startDate, endDate, PaymentType.INCOME_TAX_ADVANCE);
    if (companyService.findEntry(companyId).getTaxType() == TaxType.LINEAR) {
      return base
          .multiply(Rates.LINEAR_TAX_RATE.getValue())
          .subtract(sumHealthInsurancePaid.multiply(BigDecimal.valueOf(7.75 / 9.0)))
          .subtract(sumIncomeTaxesAdvancePaid)
          .setScale(2, RoundingMode.HALF_UP);
    } else if (companyService.findEntry(companyId).getTaxType() == TaxType.PROGRESIVE) {
      if (base.compareTo(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD.getValue()) > 0) {
        return
            BigDecimal.valueOf(85528)
                .multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_LOW_PERCENT.getValue())
                .add((base
                    .subtract(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD.getValue())
                    .multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_HIGH_PERCENT.getValue())))
                .subtract(sumHealthInsurancePaid.multiply(BigDecimal.valueOf(7.75 / 9)))
                .subtract(sumIncomeTaxesAdvancePaid)
                .setScale(2, RoundingMode.HALF_UP);
      } else {
        return base
            .multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_LOW_PERCENT.getValue())
            .subtract(Rates.DECREASING_TAX_AMOUNT.getValue())
            .subtract(sumHealthInsurancePaid.multiply(BigDecimal.valueOf(7.75 / 9)))
            .subtract(sumIncomeTaxesAdvancePaid)
            .setScale(2, RoundingMode.HALF_UP);
      }
    }
    return BigDecimal.valueOf(-1);
  }

  public BigDecimal caluclateTaxBase(long companyId, LocalDate startDate, LocalDate endDate) {
    BigDecimal base = calculateIncome(companyId, startDate.minusDays(1),
        endDate.plusDays(1)).subtract(calculateCost(companyId, startDate.minusDays(1),
        endDate.plusDays(1)));
    BigDecimal sumToSubstract = calculateSpecifiedTypeCostsBetweenDates(
        companyId, startDate, endDate.plusDays(20), PaymentType.PENSION_INSURANCE);

    return base.subtract(sumToSubstract).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal calculateSpecifiedTypeCostsBetweenDates(long companyId,
      LocalDate startDate, LocalDate endDate, PaymentType type) {
    Optional<BigDecimal> cost = paymentService.getPaymentsByTypeAndDate(
        companyId, startDate,
        endDate, type)
        .stream()
        .map(Payment::getAmount)
        .reduce(BigDecimal::add);
    return cost.orElse(BigDecimal.ZERO);
  }

  public Map<String, BigDecimal> taxSummary(long companyId, int year) {
    LocalDate startDate = LocalDate.of(year, 1, 1);
    LocalDate endDate = LocalDate.of(year, 12, 31);
    final BigDecimal income = calculateIncome(companyId, startDate, endDate);
    final BigDecimal costs = calculateCost(companyId, startDate, endDate);
    Map<String, BigDecimal> taxesSummary = new LinkedHashMap<>();
    taxesSummary.put("Income", income.setScale(2));
    taxesSummary.put("Costs", costs.setScale(2));
    taxesSummary.put("Income - Costs", income.subtract(costs).setScale(2));
    taxesSummary
        .put("Pension Insurance monthly rate", Rates.PENSION_INSURANCE.getValue().setScale(2));
    taxesSummary.put("Pension insurance paid",
        calculateSpecifiedTypeCostsBetweenDates(companyId,
            startDate, endDate.plusDays(20), PaymentType.PENSION_INSURANCE).setScale(2));
    BigDecimal taxBase = caluclateTaxBase(companyId, startDate, endDate);
    taxesSummary.put("Tax calculation base", taxBase.setScale(2));
    BigDecimal incomeTax = BigDecimal.valueOf(-1);
    switch (companyService.findEntry(companyId).getTaxType()) {
      case LINEAR: {
        incomeTax = taxBase.multiply(Rates.LINEAR_TAX_RATE.getValue())
            .setScale(2, BigDecimal.ROUND_HALF_DOWN);
        taxesSummary.put("Income tax", incomeTax.setScale(2));
        break;
      }
      case PROGRESIVE: {
        if (taxBase.compareTo(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD.getValue()) > 0) {
          incomeTax = (Rates.PROGRESSIVE_TAX_RATE_THRESHOLD.getValue()
              .multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_LOW_PERCENT.getValue()))
              .add(taxBase.subtract(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD.getValue())
                  .multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_HIGH_PERCENT.getValue()))
              .setScale(2, BigDecimal.ROUND_HALF_UP);
          taxesSummary.put("Income tax", incomeTax);
        } else {
          incomeTax = taxBase.multiply(Rates.PROGRESSIVE_TAX_RATE_THRESHOLD_LOW_PERCENT.getValue())
              .setScale(2, BigDecimal.ROUND_HALF_UP);
          taxesSummary.put("Income tax", incomeTax);
          taxesSummary
              .put("Decreasing tax amount", Rates.DECREASING_TAX_AMOUNT.getValue().setScale(2));
          incomeTax = incomeTax.subtract(Rates.DECREASING_TAX_AMOUNT.getValue());
        }
        break;
      }
      default: {
        return null;
      }
    }
    BigDecimal healthInsurancePaid = calculateSpecifiedTypeCostsBetweenDates(
        companyId, startDate, endDate.plusDays(20), PaymentType.HEALTH_INSURANCE);
    BigDecimal incomeTaxPaid = calculateSpecifiedTypeCostsBetweenDates(
        companyId, startDate, endDate.plusDays(20), PaymentType.INCOME_TAX_ADVANCE);
    taxesSummary.put("Income tax paid", incomeTaxPaid.setScale(2));
    taxesSummary.put("Health insurance paid", healthInsurancePaid.setScale(2));
    taxesSummary.put("Health insurance to substract",
        healthInsurancePaid.multiply(BigDecimal.valueOf(7.75)).divide(BigDecimal.valueOf(9))
            .setScale(2, BigDecimal.ROUND_HALF_UP));
    taxesSummary.put("Income tax - health insurance to substract - income tax paid",
        incomeTax.subtract(
            healthInsurancePaid.multiply(BigDecimal.valueOf(7.75)).divide(BigDecimal.valueOf(9)))
            .subtract(incomeTaxPaid).setScale(2, BigDecimal.ROUND_HALF_UP));
    return taxesSummary;
  }

  private Predicate<Invoice> getCompanyIdSeller(long companyId) {
    return x -> x.getSeller().getId() == companyId;
  }

  private Predicate<Invoice> getCompanyIdBuyer(long companyId) {
    return x -> x.getBuyer().getId() == companyId;
  }

  private Predicate<InvoiceEntry> isPersonalCarUsageAndProductTypeCar(Company company) {
    return x -> (x.getProduct().getProductType().equals(ProductType.CAR)
        && company.isPersonalCarUsage());
  }

  private BigDecimal calculatePattern(Function<Invoice, BigDecimal> getValueFunction,
      LocalDate beginDate, LocalDate endDate) {
    BigDecimal income = BigDecimal.valueOf(0);
    for (Invoice invoice : getInvoiceByDate(beginDate, endDate)) {
      income = income.add(getValueFunction.apply(invoice));
    }
    return income;
  }

  private BigDecimal getCostValue(Invoice invoice, Company company) {
    BigDecimal netValue = BigDecimal.valueOf(0);
    for (InvoiceEntry products : invoice.getProducts()) {
      Product product = products.getProduct();
      netValue = netValue
          .add(product.getNetValue()
              .multiply(new BigDecimal(products.getAmount())));
      if (isPersonalCarUsageAndProductTypeCar(company).test(products)) {
        netValue = netValue
            .add(addHalfVatValueToCost(products.getProduct(), products.getAmount()));
      }
    }
    return netValue;
  }

  private BigDecimal getNetValue(Invoice invoice) {
    BigDecimal netValue = BigDecimal.valueOf(0);
    for (InvoiceEntry products : invoice.getProducts()) {
      netValue = netValue.add(products.getProduct()
          .getNetValue().multiply(new BigDecimal(products.getAmount())));
    }
    return netValue;
  }

  private BigDecimal getVatValue(Invoice invoice) {
    BigDecimal vatValue = BigDecimal.valueOf(0);
    for (InvoiceEntry products : invoice.getProducts()) {
      vatValue = vatValue.add(products.getProduct().getNetValue()
          .multiply(BigDecimal.valueOf(products.getProduct().getVatRate().getVatPercent()))
          .multiply(new BigDecimal(products.getAmount())));
    }
    return vatValue;
  }

  private BigDecimal getIncomeVatValue(Invoice invoice, Company company) {
    BigDecimal vatValue = BigDecimal.valueOf(0);
    for (InvoiceEntry products : invoice.getProducts()) {
      if (isPersonalCarUsageAndProductTypeCar(company).test(products)) {
        vatValue = vatValue.add(addHalfVatValuetoVatValue(
            products.getProduct(), products.getAmount()));
      } else {
        vatValue = vatValue.add(products.getProduct().getNetValue()
            .multiply(BigDecimal.valueOf(products.getProduct().getVatRate().getVatPercent()))
            .multiply(new BigDecimal(products.getAmount())));
      }
    }
    return vatValue;
  }

  private List<Invoice> getInvoiceByDate(LocalDate beginDate, LocalDate endDate) {
    if (beginDate == null) {
      beginDate = LocalDate.MIN;
    }
    if (endDate == null) {
      endDate = LocalDate.MAX;
    }
    List<Invoice> selectedInvoices = new ArrayList<>();
    List<Invoice> allInvoices = database.getEntries();
    for (Invoice invoice : allInvoices) {
      if ((invoice.getIssueDate().isBefore(endDate) || invoice.getIssueDate().isEqual(endDate))
          && (invoice.getIssueDate().isAfter(beginDate) || invoice.getIssueDate()
          .isEqual(beginDate))) {
        selectedInvoices.add(invoice);
      }
    }
    return selectedInvoices;
  }

  //Method adds half of product vat to cost if needed. Polish tax system.
  private BigDecimal addHalfVatValueToCost(Product product, int amount) {
    return product.getNetValue()
        .multiply(BigDecimal.valueOf(product.getVatRate().getVatPercent()))
        .divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.CEILING)
        .multiply(BigDecimal.valueOf(amount));
  }

  //Method adds half of product vat to total Vat. Polish tax system.
  private BigDecimal addHalfVatValuetoVatValue(Product product, int amount) {
    return product.getNetValue()
        .multiply(BigDecimal.valueOf(product.getVatRate().getVatPercent()))
        .divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.DOWN)
        .multiply(BigDecimal.valueOf(amount));
  }


}
