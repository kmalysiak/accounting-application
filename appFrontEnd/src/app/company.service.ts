import { Injectable } from '@angular/core';
import { Company } from './company';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { MessageService } from './message.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class CompanyService {
  private apiUrl = 'http://localhost:8080/v2/company';  // URL to web api

  getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl).pipe(
      tap(companies => this.log(`fetched companies`)),
      catchError(this.handleError('getCompanies', [])));
  }

  getCompany(id: number): Observable<Company> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Company>(url).pipe(
      tap(_ => this.log(`fetched company id=${id}`)),
      catchError(this.handleError<Company>(`getCompany id=${id}`))
    );
  }

  deleteCompany(id: number): Observable<Company> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Company>(url).pipe(
      tap(_ => this.log(`deleted company id=${id}`)),
      catchError(this.handleError<Company>(`deleted id=${id}`))
    );
  }

  updateCompany(company: Company): Observable<any> {
    return this.http.put(this.apiUrl + '/' + company.companyId, company, httpOptions).pipe(
      tap(_ => this.log(`updated company id=${company.companyId}`)),
      catchError(this.handleError<any>('updateCompany'))
    );
  }

  addCompany(company: Company): Observable<Company> {
    this.messageService.add(company.name);
    return this.http.post<Company>(this.apiUrl, company, httpOptions).pipe(
      tap((company: Company) => this.log(`added company w/ id=${company.companyId}`)),
      catchError(this.handleError<Company>('addCompany'))
    );
  }

  searchCompanies(term: string): Observable<Company[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<Company[]>(this.apiUrl + '/term?term=' + term).pipe(
      tap(_ => this.log(`found companies matching "${term}"`)),
      catchError(this.handleError<Company[]>('searchCompanies', []))
    );
  }

  constructor(private messageService: MessageService,
    private http: HttpClient) { }

  private log(message: string) {
    this.messageService.add('CompanyService: ' + message);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
