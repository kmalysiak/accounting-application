import { Component, OnInit, Input } from '@angular/core';
import { Company } from '../company';
import { CompanyService } from '../company.service';
import { AppRoutingModule } from '../app-routing.module';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MessageService } from '../message.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-company-detail',
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.css']
})

export class CompanyDetailComponent implements OnInit {
  company: Company;
  id: number;

  goBack(): any {
    this.location.back();
  }

  constructor(private companyService: CompanyService,
    private route: ActivatedRoute,
    private router: Router, private messageService: MessageService, private location: Location) { }

  ngOnInit() {
    this.route.params.subscribe(param => {
      this.id = +param['id'];
      this.companyService.getCompany(this.id).subscribe(company => this.company = company);
    });
  }

  delete(): void {
    this.companyService.deleteCompany(this.id)
      .subscribe(() => this.goBack());
  }

}
