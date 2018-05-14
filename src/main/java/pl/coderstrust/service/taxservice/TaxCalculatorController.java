package pl.coderstrust.service.taxservice;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Messages;
import pl.coderstrust.service.CompanyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@RestController
@Configuration
public class TaxCalculatorController {

  private TaxCalculatorService taxService;
  private CompanyService companyService;

  @Autowired
  public TaxCalculatorController(TaxCalculatorService taxService,
      CompanyService companyService) {
    this.taxService = taxService;
    this.companyService = companyService;
  }

  //TODO Maybe change companyID from RequestParam to Path Variable will look cleaner?

  @RequestMapping(value = "income/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns income in specific date range")
  public ResponseEntity calculateIncome(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      return ResponseEntity.ok(taxService.calculateIncome(companyId, startDate, endDate)
          .setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "cost/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns cost in specific date range")
  public ResponseEntity calculateCost(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      return ResponseEntity.ok(taxService.calculateCost(companyId, startDate, endDate)
          .setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "incomeTax/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns income Tax in specific date range")
  public ResponseEntity calculateIncomeTax(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      BigDecimal income = taxService.calculateIncome(companyId, startDate, endDate);
      BigDecimal cost = taxService.calculateCost(companyId, startDate, endDate);
      return ResponseEntity.ok(income.subtract(cost).setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "incVat/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns income Vat in specific date range")
  public ResponseEntity calculateIncomeVat(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      return ResponseEntity.ok(taxService.calculateIncomeVat(companyId, startDate, endDate)
          .setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "outVat/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns outcome Vat in specific date range")
  public ResponseEntity calculateOutcomeVat(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      return ResponseEntity.ok(taxService.calculateOutcomeVat(companyId, startDate, endDate)
          .setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "diffVat/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns difference in Vat in specific date range")
  public ResponseEntity calculateDifferenceVat(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
      BigDecimal outVat = taxService.calculateOutcomeVat(companyId, startDate, endDate);
      BigDecimal incVat = taxService.calculateIncomeVat(companyId, startDate, endDate);
      return ResponseEntity.ok(outVat.subtract(incVat).setScale(2, RoundingMode.HALF_UP));
    }
    return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
  }

  @RequestMapping(value = "taxSummary/{companyId}/{year}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns taxes summary in specific date range")
  public ResponseEntity calculateTaxSummary(
      @PathVariable("companyId") long companyId,
      @PathVariable("year") int year) {
    if (year > LocalDate.now().getYear() + 50 || year < LocalDate.now().getYear() - 200) {
      return ResponseEntity.badRequest().body(Messages.INCORRECT_YEAR);
    }

    return ResponseEntity.ok(taxService.taxSummary(companyId, year));
  }

  @RequestMapping(value = "incomeTaxAdvance/{companyId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns value of income tax advance in specific date range")
  public ResponseEntity calculateIncomeTaxAdvance(
      @PathVariable("companyId") long companyId,
      @RequestParam(value = "startDate") LocalDate startDate,
      @RequestParam(value = "endDate") LocalDate endDate
  ) {
    if ((startDate.isAfter(endDate)) || (endDate.getYear() != startDate.getYear())) {
      return ResponseEntity.badRequest().body(Messages.END_BEFORE_START);
    }
    return ResponseEntity.ok(
        taxService.calculateIncomeTaxAdvance(companyId, startDate, endDate)
            .setScale(2, RoundingMode.HALF_UP));
  }

}