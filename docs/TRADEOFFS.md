# Trade-offs

**Current pay on `employees` vs a `compensation_history` table.**  
One row is what HR already understands and is enough to *manage* and *report current* pay. History is valuable for raises, but it doubles UI and seed complexity. We would add it when someone asks “what did we pay last year?”

**Static FX vs live rates vs store-everything-in-USD.**  
USD-only storage hides local contracts (wrong for many countries). Live FX needs keys and makes tests non-deterministic. A dated static table is honest: org totals are *approximate USD*, local amounts stay source of truth.

**Hide deactivated people vs keep a status filter.**  
Deactivate is a soft delete for the working set: the row remains, payroll ignores it, and the directory does not list it. A status dropdown would invite HR to manage a second census of leavers. Reactivate was tried as a table action and removed — one-way deactivate matches “they left active payroll,” not an employment-status toggle.

**No authentication.**  
The brief is one HR manager replacing Excel, not a security assessment. Shipping SSO would steal time from pagination, insights, and tests — the actual 10k-employee problem.

**Postgres (given) vs SQLite (suggested).**  
Postgres is already configured and is closer to production. Tests use H2 so they stay fast and do not depend on a local server.

**Custom HR UI vs Angular Material.**  
Angular 12’s CLI scaffold is old enough that Material adds install/theme friction without making the HR flow clearer. A small purpose-built layout (filters, paginated table, KPI cards, toasts) matches the job: scan 10k people and read pay insights. A named component library can be layered on later.

**One insights table with Group by vs three always-visible tables.**  
Country / department / level are the same question with a different grain. One table plus a dropdown keeps the page scannable; the API still computes all three so switching group-by does not refetch.

**Native SQL for median vs Java in memory.**  
Loading 10k rows to compute a median works at this size but teaches the wrong habit. Postgres `PERCENTILE_CONT` is the shape we’d keep at 100k+. Portable average/sum/median math still lives in `CompensationStats` for tests.

**Hibernate `ddl-auto=update` vs Flyway.**  
Fine for an assessment database we own. A real rollout would version migrations; noted here so it isn’t mistaken for production practice.

**Toasts vs inline banners.**  
Mutations navigate away (create/update) or reload the table (deactivate). A top-right toast survives that navigation; banners on the form would vanish before they were useful.
