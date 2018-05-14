import { Component, OnInit, Input } from '@angular/core';
import { Company } from '../company';
import { AppRoutingModule } from '../app-routing.module';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MessageService } from '../message.service';
import { Location } from '@angular/common';
import { Invoice } from '../invoice';
import { InvoiceService } from '../invoice.service';

@Component({
  selector: 'app-invoice-detail',
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.css']
})

export class InvoiceDetailComponent implements OnInit {
  invoice: Invoice;
  id: number;

  goBack(): any {
    this.location.back();
  }

  constructor(private invoiceService: InvoiceService,
    private route: ActivatedRoute,
    private router: Router, private messageService: MessageService, private location: Location) { }

  ngOnInit() {
    this.route.params.subscribe(param => {
      this.id = +param['id'];
      this.invoiceService.getInvoice(this.id).subscribe(invoice => this.invoice = invoice);
    });
  }

  delete(): void {
    this.invoiceService.deleteInvoice(this.id)
      .subscribe(() => this.goBack());
  }

}
