# Architecture

```
┌──────────────────────┐         JSON / REST          ┌─────────────────────────┐
│  Angular 12 (port    │  http://localhost:4200  ───► │  Spring Boot 4 (8080)   │
│  4200) HR UI         │         CORS enabled         │  Java 21, JPA           │
│  Insights + Employees│ ◄─────────────────────────── │                         │
└──────────────────────┘                              └───────────┬─────────────┘
                                                                  │
                                                                  ▼
                                                      ┌─────────────────────────┐
                                                      │  PostgreSQL 16          │
                                                      │  employees, fx_rates    │
                                                      └─────────────────────────┘
```

## Why this split

The assessment started from an **Angular 12 + Spring Boot 4 + Postgres** scaffold. We kept that stack instead of rewriting it. SQLite would have been fine for 10k rows; Postgres is what is running and is the better deployed default.

Database URL, credentials, and CORS origins are overridable with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `CORS_ORIGINS`.

## Domain model

**`employees`** — identity + *current* compensation on one row. HR’s Excel mental model is “one row per person.” A separate history table would be the next iteration, not v1.

Status is `ACTIVE` or `INACTIVE`. Deactivate sets `INACTIVE`. The directory, lookups, and GET/PUT for a person only see **active** rows. Insights payroll SQL already filters `status = 'ACTIVE'`.

**`fx_rates`** — `currency → USD` with an as-of date. Tiny, seeded, referenced in insights SQL (`(base + bonus) * usd_rate`). Rates are cached in memory after first read.

Indexes: `country`, `department`, `job_level`, `status`, `(last_name, first_name)`. Unique: `email`, `employee_code`. IDs come from a sequence (`allocationSize = 50`) so seed inserts can batch. Visible codes (`ACME-00001`) increment from the **highest existing code**, not the database id, so sequence gaps never reuse an HR id.

## API (backend owns paging and aggregation)

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/employees` | Search, filters, page (active employees only; never 10k in one payload) |
| GET | `/api/employees/{id}` | Active employee detail (inactive → 404) |
| POST | `/api/employees` | Create (always active) |
| PUT | `/api/employees/{id}` | Update an active employee |
| PATCH | `/api/employees/{id}/deactivate` | Soft exit from directory and payroll |
| GET | `/api/lookups` | Filter dropdowns from **active** employees |
| GET | `/api/insights` | Org totals + breakdowns in USD |
| GET | `/api/health` | Liveness |

Search splits the query into tokens (so `John Doe` matches first + last). Insights use SQL `GROUP BY` and `PERCENTILE_CONT` so the database does the 10k-row work. The UI shows one breakdown table and switches series with **Group by**; the API still returns `byCountry`, `byDepartment`, and `byJobLevel` in one response.

## Layers

- **Domain** (`CompensationStats`, `FxConverter`, `EmployeeCodes`, `OrgCatalog`) — pure Java, unit-tested, no Spring.
- **Service** — validation, uniqueness, mapping, FX on read.
- **Web** — DTOs (records), bean validation, consistent error JSON.
- **UI** — Angular features: Insights (questions) and Employees (management). Shell hosts toasts.

## Auth

None. The chrome labels the user as “HR Manager.” Production would put SSO in front of `/api/**`. See trade-offs.
