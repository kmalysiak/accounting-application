import { Component, OnInit, Input } from '@angular/core';
import { Company } from "../company";
import { CompanyService } from "../company.service";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MessageService } from '../message.service';

import { AppRoutingModule } from '../app-routing.module';

@Component({
  selector: 'app-edit-company',
  templateUrl: './edit-company.component.html',
  styleUrls: ['./edit-company.component.css']
})
export class EditCompanyComponent implements OnInit {
  company: Company;
  id: number;

  private sub: any;
  constructor(private route: ActivatedRoute,
    private router: Router, private companyService: CompanyService
    , private messageService: MessageService) { }

  edit(): void {
    this.companyService.updateCompany(this.company).subscribe();
  }

  ngOnInit() {
    this.route.params.subscribe(param => {
      this.id = +param['id'];
      this.companyService.getCompany(this.id).subscribe(company => this.company = company);
    });
  }

}
