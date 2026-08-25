import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { Employee, LookupResponse } from '../../core/models';
import { EmployeeApiService } from '../../core/employee-api.service';
import { formatMoney, formatUsd } from '../../core/format-money';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit, OnDestroy {
  employees: Employee[] = [];
  lookups: LookupResponse = { countries: [], departments: [], jobLevels: [], currencies: [] };
  totalElements = 0;
  page = 0;
  size = 25;
  search = '';
  country = '';
  department = '';
  jobLevel = '';
  status = 'ACTIVE';
  loading = false;
  error: string | null = null;
  notice: string | null = null;

  private readonly search$ = new Subject<string>();
  private readonly destroy$ = new Subject<void>();

  constructor(private employeeApi: EmployeeApiService) {}

  ngOnInit(): void {
    this.employeeApi.lookups().subscribe((lookups) => this.lookups = lookups);
    this.search$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe((value) => {
      this.search = value;
      this.page = 0;
      this.load();
    });
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearch(value: string): void {
    this.search$.next(value.trim());
  }

  onFilterChange(): void {
    this.page = 0;
    this.load();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.employeeApi.search({
      search: this.search || undefined,
      country: this.country || undefined,
      department: this.department || undefined,
      jobLevel: this.jobLevel || undefined,
      status: this.status || undefined,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (result) => {
        this.employees = result.content;
        this.totalElements = result.totalElements;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load employees. Is the API running on port 8080?';
        this.loading = false;
      }
    });
  }

  get totalPages(): number {
    return Math.max(Math.ceil(this.totalElements / this.size), 1);
  }

  prev(): void {
    if (this.page > 0) {
      this.page -= 1;
      this.load();
    }
  }

  next(): void {
    if (this.page + 1 < this.totalPages) {
      this.page += 1;
      this.load();
    }
  }

  deactivate(employee: Employee): void {
    const name = `${employee.firstName} ${employee.lastName}`;
    if (!confirm(`Deactivate ${name}? They stay in the system but leave active payroll.`)) {
      return;
    }
    this.employeeApi.deactivate(employee.id).subscribe({
      next: () => {
        this.notice = `${name} is now inactive.`;
        this.load();
      },
      error: () => this.error = 'Could not deactivate this employee.'
    });
  }

  money(value: number, currency: string): string {
    return formatMoney(value, currency);
  }

  usd(value: number): string {
    return formatUsd(value);
  }
}
