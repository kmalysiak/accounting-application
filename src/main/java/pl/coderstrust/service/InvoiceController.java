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
import pl.coderstrust.service.filters.InvoiceDummyFilter;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("v1/invoice")
@RestController
public class InvoiceController extends AbstractController<Invoice> {

  public InvoiceController(InvoiceService invoiceService, InvoiceDummyFilter dummyFilter) {
    super.service = invoiceService;
    super.filter = dummyFilter;
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  @ApiOperation(value = "Adds the invoice and returning id")
  public synchronized ResponseEntity addInvoice(
      @RequestBody Invoice invoice) {
    return super.addEntry(invoice, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns the invoice by id")
  public synchronized ResponseEntity getInvoiceById(
      @PathVariable("id") Long invoiceId) {
    return super.getEntryById(invoiceId, null);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  @ApiOperation(value = "Returns the list of invoices in the specified date range")
  public synchronized ResponseEntity getInvoiceByDate(
      @RequestParam(name = "startDate", required = false) LocalDate startDate,
      @RequestParam(name = "endDate", required = false) LocalDate endDate) {
    return super.getEntryByDate(startDate, endDate, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @ApiOperation(value = "Updates the invoices by id")
  public synchronized ResponseEntity updateInvoice(
      @PathVariable("id") Long id,
      @RequestBody Invoice invoice) {
    return super.updateEntry(id, invoice, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes the invoices by id")
  public synchronized ResponseEntity removeInvoice(
      @PathVariable("id") Long id) {
    return removeEntry(id, null);
  }


  @RequestMapping(value = "{id}/pdf", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_PDF_VALUE)
  @ApiOperation(value = "Returns invoice in pdf format")
  public ResponseEntity<InputStreamResource> invoiceToPdf(
      @PathVariable("id") Long id) {

    return super.getPdfFromEntry(id, null);
  }

}
