import { Injectable } from '@angular/core';
import { Company } from './company';
import { Invoice } from './invoice';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { MessageService } from './message.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class InvoiceService {
  private apiUrl = 'http://localhost:8080/v1/invoice';  // URL to web api

  getInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.apiUrl).pipe(
      tap(invoice => this.log(`fetched invoices`)),
      catchError(this.handleError('getInvoices', [])));
  }

  getInvoice(id: number): Observable<Invoice> {
    const url = this.apiUrl + '/' + id;
    return this.http.get<Invoice>(url).pipe(
      tap(_ => this.log(`fetched invoice id=${id}`)),
      catchError(this.handleError<Invoice>(`getInvoice id=${id}`))
    );
  }

  deleteInvoice(id: number): Observable<Invoice> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Invoice>(url).pipe(
      tap(_ => this.log(`deleted invoice id=${id}`)),
      catchError(this.handleError<Invoice>(`deleted id=${id}`))
    );
  }

  updateInvoice(invoice: Invoice): Observable<any> {
    return this.http.put(this.apiUrl + '/' + invoice.invoiceId, invoice, httpOptions).pipe(
      tap(_ => this.log(`updated company id=${invoice.invoiceId}`)),
      catchError(this.handleError<any>('updateInvoice'))
    );
  }

  addInvoice(invoice: Invoice): Observable<Invoice> {
    this.messageService.add(invoice.name);
    return this.http.post<Invoice>(this.apiUrl, invoice, httpOptions).pipe(
      tap((invoice: Invoice) => this.log(`added invoice w/ id=${invoice.invoiceId}`)),
      catchError(this.handleError<Invoice>('addInvoice'))
    );
  }

  searchInvoices(term: string): Observable<Invoice[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<Invoice[]>(this.apiUrl + '/term?term=' + term).pipe(
      tap(_ => this.log(`found invoices matching "${term}"`)),
      catchError(this.handleError<Invoice[]>('searchInvoices', []))
    );
  }

  constructor(private messageService: MessageService,
    private http: HttpClient) { }

  private log(message: string) {
    this.messageService.add('InvoiceService: ' + message);
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
