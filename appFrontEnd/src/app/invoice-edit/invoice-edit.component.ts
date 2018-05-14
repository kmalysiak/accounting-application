import { Component, OnInit, Input } from '@angular/core';
import { Company } from "../company";
import { CompanyService } from "../company.service";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MessageService } from '../message.service';
import { Invoice } from '../invoice';
import { InvoiceService } from '../invoice.service';
import { AppRoutingModule } from '../app-routing.module';

@Component({
  selector: 'app-invoice-edit',
  templateUrl: './invoice-edit.component.html',
  styleUrls: ['./invoice-edit.component.css']
})
export class InvoiceEditComponent implements OnInit {
  invoice: Invoice;
  id: number;

  private sub: any;
  constructor(private route: ActivatedRoute,
    private router: Router, private invoiceService: InvoiceService
    , private messageService: MessageService) { }

  edit(): void {
    this.invoiceService.updateInvoice(this.invoice).subscribe();
  }

  ngOnInit() {
    this.route.params.subscribe(param => {
      this.id = +param['id'];
      this.invoiceService.getInvoice(this.id)
      .subscribe(invoice => this.invoice = invoice);
    });
  }

}
