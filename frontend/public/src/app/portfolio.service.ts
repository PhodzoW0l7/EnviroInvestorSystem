import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InvestorPortfolio, WithdrawalRequest } from './portfolio';

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getPortfolio(investorId: number): Observable<InvestorPortfolio> {
    return this.http.get<InvestorPortfolio>(`${this.apiUrl}/investors/${investorId}/portfolio`);
  }

createWithdrawal(request: WithdrawalRequest): Observable<any> {
  return this.http.post(`${this.apiUrl}/withdrawals`, request, {
    responseType: 'text'
  });
}

getWithdrawalHistory(productId: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/products/${productId}/history`);
}

  exportCsv(
    productId: number,
    status?: string,
    fromDate?: string,
    toDate?: string
  ): Observable<Blob> {
    let params = new HttpParams();
    if (status)    params = params.set('status', status);
    if (fromDate)  params = params.set('fromDate', fromDate);
    if (toDate)    params = params.set('toDate', toDate);

    return this.http.get(
      `${this.apiUrl}/products/${productId}/export`,
      { params, responseType: 'blob' }
    ) as Observable<Blob>;
  }
}