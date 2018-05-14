import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CompanyComponent } from './company/company.component';
import { EditCompanyComponent } from './edit-company/edit-company.component';
import { AddCompanyComponent } from './add-company/add-company.component';
import { CompanyDetailComponent } from './company-detail/company-detail.component';
import { CompanySearchComponent } from './company-search/company-search.component';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoiceDetailComponent } from './invoice-detail/invoice-detail.component';
import { InvoiceAddComponent } from './invoice-add/invoice-add.component';
import { InvoiceEditComponent } from './invoice-edit/invoice-edit.component';

const routes: Routes = [
  { path: 'addCompany', component: AddCompanyComponent },
  { path: 'companies', component: CompanyComponent },
  { path: 'companyEdit/:id', component: EditCompanyComponent },
  { path: '', redirectTo: '/companies', pathMatch: 'full' },
  { path: 'detail/:id', component: CompanyDetailComponent },
  { path: 'searchCompany', component: CompanySearchComponent },
  { path: 'invoices', component: InvoiceComponent },
  { path: 'addInvoice', component: InvoiceAddComponent },
  { path: 'detailinv/:id', component: InvoiceDetailComponent },
  { path: 'invoiceEdit/:id', component: InvoiceEditComponent },

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }