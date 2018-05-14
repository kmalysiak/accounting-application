package pl.coderstrust.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.pdfservice.PdfGenerator;

@Service
public class InvoiceService extends AbstractService<Invoice> {


  @Autowired
  public InvoiceService(@Qualifier("invoicesDatabase") Database<Invoice> dbInvoices,
      PdfGenerator pdfGenerator) {
    super.entriesDb = dbInvoices;
    super.pdfGenerator = pdfGenerator;
  }
}
