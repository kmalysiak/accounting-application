import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { CompanyComponent } from './company/company.component';
import { FormsModule } from '@angular/forms';
import { CompanyDetailComponent } from './company-detail/company-detail.component';
import { CompanyService } from './company.service';
import { InvoiceService } from './invoice.service';
import { MessagesComponent } from './messages/messages.component';
import { MessageService } from './message.service';
import { HttpClientModule }    from '@angular/common/http';
import { AddCompanyComponent } from './add-company/add-company.component';
import { EditCompanyComponent } from './edit-company/edit-company.component';
import { AppRoutingModule } from './/app-routing.module';
import { CompanySearchComponent } from './company-search/company-search.component';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoiceDetailComponent } from './invoice-detail/invoice-detail.component';
import { InvoiceAddComponent } from './invoice-add/invoice-add.component';
import { InvoiceEditComponent } from './invoice-edit/invoice-edit.component';

@NgModule({
  declarations: [
    AppComponent,
    CompanyComponent,
    CompanyDetailComponent,
    MessagesComponent,
    AddCompanyComponent,
    EditCompanyComponent,
    CompanySearchComponent,
    InvoiceComponent,
    InvoiceDetailComponent,
    InvoiceAddComponent,
    InvoiceEditComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    CompanyService,
    MessageService,
    InvoiceService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
