import { Component, OnInit } from '@angular/core';
import { CompanyService } from '../company.service';
import { Company } from '../company';
import { InvoiceService } from '../invoice.service';
import { Invoice } from '../invoice';

@Component({
  selector: 'app-invoice-add',
  templateUrl: './invoice-add.component.html',
  styleUrls: ['./invoice-add.component.css']
})
export class InvoiceAddComponent implements OnInit {
  companies: Company[];
  invoice: Invoice = new Invoice();

  sellerId: number;
  buyerId: number;

  constructor(private invoiceService: InvoiceService,
    private companyService: CompanyService) { }

  ngOnInit() {
    this.companyService.getCompanies().subscribe(companies => this.companies = companies);
  }

  add(invoice: Invoice): void {
    this.invoice.seller = this.companies[this.sellerId];
    this.invoice.buyer = this.companies[this.buyerId];
    this.invoiceService.addInvoice(this.invoice)
      .subscribe();
  }

  private fetchCompanies() {
  }
}
