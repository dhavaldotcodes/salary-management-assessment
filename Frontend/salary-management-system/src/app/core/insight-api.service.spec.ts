import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { InsightApiService } from './insight-api.service';
import { environment } from '../../environments/environment';

describe('InsightApiService', () => {
  let service: InsightApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(InsightApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads org insights from the API', () => {
    service.load().subscribe();
    const req = http.expectOne(`${environment.apiUrl}/insights`);
    expect(req.request.method).toBe('GET');
    req.flush({
      activeHeadcount: 10,
      inactiveHeadcount: 1,
      payrollUsd: 1000000,
      averageCompensationUsd: 100000,
      medianCompensationUsd: 90000,
      fxAsOf: '2026-01-01',
      disclaimer: 'static FX',
      byCountry: [],
      byDepartment: [],
      byJobLevel: []
    });
  });
});
