import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LookupResponse } from '../../core/models';
import { EmployeeApiService } from '../../core/employee-api.service';
import { ToastService } from '../../core/toast.service';
import { currencyForCountry } from '../../core/country-currency';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.scss']
})
export class EmployeeFormComponent implements OnInit, OnDestroy {
  form: FormGroup;
  lookups: LookupResponse = { countries: [], departments: [], jobLevels: [], currencies: [] };
  readonly fallbackLevels = ['L1', 'L2', 'L3', 'L4', 'L5', 'L6'];
  id: number | null = null;
  loading = false;

  private suppressCurrencySync = false;
  private readonly destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private employeeApi: EmployeeApiService,
    private toasts: ToastService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(80)]],
      lastName: ['', [Validators.required, Validators.maxLength(80)]],
      email: ['', [Validators.required, Validators.email]],
      country: ['US', Validators.required],
      department: ['Engineering', Validators.required],
      jobLevel: ['L3', Validators.required],
      baseSalary: [null, [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      bonus: [0, [Validators.min(0)]],
      effectiveDate: [new Date().toISOString().slice(0, 10), Validators.required],
      status: ['ACTIVE']
    });
  }

  get isEdit(): boolean {
    return this.id !== null;
  }

  ngOnInit(): void {
    this.form.get('country')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((country: string) => {
      if (this.suppressCurrencySync) {
        return;
      }
      const currency = currencyForCountry(country);
      if (currency) {
        this.form.patchValue({ currency });
      }
    });

    this.employeeApi.lookups().subscribe((lookups) => {
      this.lookups = lookups;
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id = Number(idParam);
      this.loading = true;
      this.employeeApi.get(this.id).subscribe({
        next: (employee) => {
          this.suppressCurrencySync = true;
          this.form.patchValue({
            firstName: employee.firstName,
            lastName: employee.lastName,
            email: employee.email,
            country: employee.country,
            department: employee.department,
            jobLevel: employee.jobLevel,
            baseSalary: employee.baseSalary,
            currency: employee.currency,
            bonus: employee.bonus,
            effectiveDate: employee.effectiveDate,
            status: employee.status
          });
          this.suppressCurrencySync = false;
          this.loading = false;
        },
        error: () => {
          this.toasts.error('Employee not found.');
          this.loading = false;
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  invalid(control: string): boolean {
    const field = this.form.get(control);
    return !!field && field.invalid && field.touched;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    const body = this.form.value;
    const request$ = this.isEdit
      ? this.employeeApi.update(this.id as number, body)
      : this.employeeApi.create(body);

    request$.subscribe({
      next: () => {
        this.toasts.success(this.isEdit ? 'Employee updated.' : 'Employee added.');
        this.router.navigate(['/employees']);
      },
      error: (err) => {
        this.loading = false;
        this.toasts.error(err?.error?.message || 'Could not save this employee.');
      }
    });
  }
}
