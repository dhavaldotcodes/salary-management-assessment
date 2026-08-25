# ACME Salary Management — Requirements

**Persona:** HR Manager at ACME (≈10,000 employees, multiple countries).  
**Problem:** Compensation lives in spreadsheets. HR cannot reliably maintain records or answer “how do we pay people?”  
**Goal:** A web app to manage current salary data and see how the organization pays — by country, department, and level.

## In scope (v1)

1. **Employee compensation records** — name, work email, employee code (`ACME-#####`), country, department, job level (L1–L6), base salary, currency, optional bonus, effective date.
2. **Manage records** — search (first name, last name, full name, email, code), filter by country / department / level, paginated list, create, edit, deactivate.
3. **Deactivate** — people stay in the database so the row is not destroyed, but they **leave the directory and active payroll**. They do not appear in search, filters, or edit. There is no Activate button; deactivate is one-way in the UI.
4. **Pay insights** — active headcount, approximate USD payroll, average and median compensation. One breakdown table with a **Group by** control (country, department, or job level).
5. **Multi-currency honesty** — store pay in local currency. Org-wide totals convert to USD with a **static FX table** (dated, labeled as approximate).
6. **Feedback** — success and error toasts on add, update, deactivate, and load failures.
7. **Seed** — deterministic load of 10,000 employees so the product can be demoed at real volume.

**Success:** An HR manager can find someone in seconds (including by `"John Doe"`), correct a salary, take someone off payroll, and answer “what do we spend in India vs the US?” without Excel.

## Out of scope (deliberate)

| Left out | Why |
|---|---|
| Login, SSO, roles | One HR persona; auth is a different product and would dominate the build. |
| Payroll runs, tax, benefits, equity | This is **salary administration**, not a payroll engine. |
| Employee self-service / approvals | Different user and workflow. |
| Salary history / merit cycles | v1 answers *how we pay today*, not the full compensation timeline. |
| Live FX APIs | Extra cost, flakiness, and secrets; a documented table is testable. |
| CSV import | Useful Excel bridge; CRUD + insights already replace the daily job. |
| Reactivate from the table | Deactivate is a soft exit from the working set, not a toggle. |
| Mobile app, multi-tenant, audit log UI | Overkill for this assessment. |

## Assumptions

- One current package per employee (not split base / allowance lines).
- Job architecture is a simple L1–L6 ladder shared across countries.
- USD is the reporting currency for cross-country questions.
- Changing country on the form fills the usual local currency; HR can still override it.
