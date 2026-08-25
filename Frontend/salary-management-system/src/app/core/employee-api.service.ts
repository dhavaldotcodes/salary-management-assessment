import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Employee, EmployeeRequest, LookupResponse, PageResponse } from './models';

export interface EmployeeSearch {
  search?: string;
  country?: string;
  department?: string;
  jobLevel?: string;
  status?: string;
  page?: number;
  size?: number;
  sort?: string;
  direction?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeApiService {
  private readonly base = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  search(query: EmployeeSearch): Observable<PageResponse<Employee>> {
    let params = new HttpParams()
      .set('page', String(query.page ?? 0))
      .set('size', String(query.size ?? 25))
      .set('sort', query.sort ?? 'lastName')
      .set('direction', query.direction ?? 'asc');

    (['search', 'country', 'department', 'jobLevel', 'status'] as const).forEach((key) => {
      const value = query[key];
      if (value) {
        params = params.set(key, value);
      }
    });

    return this.http.get<PageResponse<Employee>>(this.base, { params });
  }

  get(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.base}/${id}`);
  }

  create(body: EmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(this.base, body);
  }

  update(id: number, body: EmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.base}/${id}`, body);
  }

  deactivate(id: number): Observable<Employee> {
    return this.http.patch<Employee>(`${this.base}/${id}/deactivate`, {});
  }

  lookups(): Observable<LookupResponse> {
    return this.http.get<LookupResponse>(`${environment.apiUrl}/lookups`);
  }
}
