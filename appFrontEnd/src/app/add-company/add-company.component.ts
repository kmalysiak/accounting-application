import { Component, OnInit } from '@angular/core';
import { CompanyService } from '../company.service';
import { Company } from '../company';

@Component({
  selector: 'app-add-company',
  templateUrl: './add-company.component.html',
  styleUrls: ['./add-company.component.css']
})
export class AddCompanyComponent implements OnInit {
  company: Company = new Company();

  taxes = ['LINEAR', 'PROGRESIVE'];

  trueFalse = ['true', 'false'];

  constructor(private companyService: CompanyService) { }

  ngOnInit() {
  }

  add(company: Company): void {
    this.companyService.addCompany(this.company)
      .subscribe();
  }

}
