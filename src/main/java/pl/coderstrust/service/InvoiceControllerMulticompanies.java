package pl.coderstrust.service;

import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.filters.InvoiceByCompanyFilter;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("v2/company/{companyId}/invoice")
@RestController
public class InvoiceControllerMulticompanies extends AbstractController<Invoice> {

  public InvoiceControllerMulticompanies(InvoiceService invoiceService,
      InvoiceByCompanyFilter invoiceByCompanyFilter) {
    super.service = invoiceService;
    super.filter = invoiceByCompanyFilter;
  }


  @RequestMapping(value = "", method = RequestMethod.POST)
  @ApiOperation(value = "Adds the invoice and returning id validating company")
  public synchronized ResponseEntity addInvoicePerCompany(
      @PathVariable("companyId") Long companyId,
      @RequestBody Invoice invoice) {
    return super.addEntry(invoice, companyId);
  }

  @RequestMapping(value = "/{invoiceId}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns the invoice by id validating company")
  public synchronized ResponseEntity getInvoiceByIdPerCompany(
      @PathVariable("companyId") Long companyId,
      @PathVariable("invoiceId") Long invoiceId) {
    return super.getEntryById(invoiceId, companyId);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  @ApiOperation(value = "Returns invoices list in the specified date range validating company")
  public synchronized ResponseEntity getInvoiceByDatePerCompany(
      @PathVariable("companyId") Long companyId,
      @RequestParam(name = "startDate", required = false) LocalDate startDate,
      @RequestParam(name = "endDate", required = false) LocalDate endDate) {
    return super.getEntryByDate(startDate, endDate, companyId);
  }

  @RequestMapping(value = "/{invoiceId}", method = RequestMethod.PUT)
  @ApiOperation(value = "Updates the invoices by id validating company")
  public synchronized ResponseEntity updateInvoicePerCompany(
      @PathVariable("invoiceId") Long invoiceId,
      @PathVariable("companyId") Long companyId,
      @RequestBody Invoice invoice) {
    return super.updateEntry(invoiceId, invoice, companyId);
  }

  @RequestMapping(value = "/{invoiceId}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes the invoices by id, validating company")
  public synchronized ResponseEntity removeInvoicePerCompany(
      @PathVariable("invoiceId") Long invoiceId,
      @PathVariable("companyId") Long companyId) {
    return super.removeEntry(invoiceId, companyId);
  }

  @RequestMapping(value = "{invoiceId}/pdf", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_PDF_VALUE)
  @ApiOperation(value = "Returns invoice in pdf format")
  public ResponseEntity<InputStreamResource> invoiceToPdf(
      @PathVariable("companyId") Long companyId,
      @PathVariable("invoiceId") Long invoiceId) {
    return super.getPdfFromEntry(invoiceId, companyId);
  }
}
