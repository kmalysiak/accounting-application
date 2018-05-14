import { Component, OnInit } from '@angular/core';
import { Company } from '../company';
import { Invoice } from '../invoice';
import { InvoiceService } from '../invoice.service';
@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css']
})
export class InvoiceComponent implements OnInit {
  invoices: Invoice[];

  selectedInvoice: Invoice;

  onSelect(invoice: Invoice): void {
    this.selectedInvoice = invoice;
  }

  getInvoices(): void {
    this.invoiceService.getInvoices()
      .subscribe(invoices => this.invoices = invoices);
  }
  constructor(private invoiceService: InvoiceService) { }

  ngOnInit() {
    this.getInvoices();
  }

}
