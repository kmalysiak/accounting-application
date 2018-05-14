import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Company } from '../company';
import { CompanyService } from '../company.service';
import { Subject } from 'rxjs/Subject';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-company-search',
  templateUrl: './company-search.component.html',
  styleUrls: ['./company-search.component.css']
})
export class CompanySearchComponent implements OnInit {
  companies$: Observable<Company[]>;
  companyById$: Observable<Company>;

  private searchTerms = new Subject<string>();
  constructor(private companyService: CompanyService) { }

  search(term: string): void {
    this.searchTerms.next(term);
  }

  ngOnInit(): void {
    this.companies$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.companyService.searchCompanies(term)),
    );
  
  }

}

