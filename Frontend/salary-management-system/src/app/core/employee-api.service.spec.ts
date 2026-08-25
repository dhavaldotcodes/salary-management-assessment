import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EmployeeApiService } from './employee-api.service';
import { environment } from '../../environments/environment';

describe('EmployeeApiService', () => {
  let service: EmployeeApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(EmployeeApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('sends search filters as query params and never asks for the full table', () => {
    service.search({ search: 'ada', country: 'IN', page: 2, size: 25 }).subscribe();

    const req = http.expectOne((request) => request.url === `${environment.apiUrl}/employees`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('ada');
    expect(req.request.params.get('country')).toBe('IN');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('25');
    req.flush({ content: [], totalElements: 0, totalPages: 0, page: 2, size: 25 });
  });

  it('deactivates with PATCH so records are not deleted', () => {
    service.deactivate(9).subscribe();
    const req = http.expectOne(`${environment.apiUrl}/employees/9/deactivate`);
    expect(req.request.method).toBe('PATCH');
    req.flush({});
  });
});
