import { Component, OnInit } from '@angular/core';
import { Company } from '../company';
import { CompanyService } from '../company.service';
@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css']
})
export class CompanyComponent implements OnInit {
  companies: Company[];

  selectedCompany: Company;

  onSelect(company: Company): void {
    this.selectedCompany = company;
  }

  getCompanies(): void {
    this.companyService.getCompanies()
      .subscribe(companies => this.companies = companies);
  }
  constructor(private companyService: CompanyService) { }

  ngOnInit() {
    this.getCompanies();
  }

}
