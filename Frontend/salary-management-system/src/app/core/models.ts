export type EmploymentStatus = 'ACTIVE' | 'INACTIVE';

export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  department: string;
  jobLevel: string;
  status: EmploymentStatus;
  baseSalary: number;
  currency: string;
  baseSalaryUsd: number;
  bonus: number;
  bonusUsd: number;
  totalCompensationUsd: number;
  effectiveDate: string;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface EmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  department: string;
  jobLevel: string;
  baseSalary: number;
  currency: string;
  bonus: number;
  effectiveDate: string;
  status?: EmploymentStatus;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface LookupResponse {
  countries: string[];
  departments: string[];
  jobLevels: string[];
  currencies: string[];
}

export interface GroupStat {
  key: string;
  headcount: number;
  payrollUsd: number;
  averageUsd: number;
  medianUsd: number;
}

export interface InsightResponse {
  activeHeadcount: number;
  inactiveHeadcount: number;
  payrollUsd: number;
  averageCompensationUsd: number;
  medianCompensationUsd: number;
  fxAsOf: string;
  disclaimer: string;
  byCountry: GroupStat[];
  byDepartment: GroupStat[];
  byJobLevel: GroupStat[];
}
