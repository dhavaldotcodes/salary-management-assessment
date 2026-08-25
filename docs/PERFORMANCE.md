# Performance (10,000 employees)

10k is small for Postgres and large for a browser. The design treats the **client** as the scarce resource.

## What we do

- **Server-side pagination** (default 25, choices 25/50/100, hard max 100). The list API never returns the full table.
- **SQL aggregations** for insights (`GROUP BY`, `SUM`/`AVG`, `PERCENTILE_CONT`). No “load all employees then reduce in Java/Angular.”
- **Active-only directory** — list, search, lookups, and GET skip `INACTIVE` rows so the working set stays the people on payroll.
- **Indexes** on filter columns: country, department, job level, status, `(last_name, first_name)`.
- **JDBC batching** on seed (`hibernate.jdbc.batch_size` + sequence ids). Identity columns would disable insert batching.
- **`open-in-view=false`** so request threads don’t leak lazy queries.
- **FX cache** — the 10-row rate table is loaded once per process.
- **Seed is idempotent** — skips when enough rows already exist so restarts don’t rewrite 10k employees.
- **Debounced search** (300ms) so typing does not fire a request per keystroke.

## What we accept

- Name search is `LIKE '%token%'` on first name, last name, email, and code. A leading wildcard will not use a btree index. At 10k this is milliseconds; at millions we would add `pg_trgm`.
- Full-name queries (`John Doe`) AND the tokens together instead of concatenating columns in SQL. Same cost class as a single `LIKE`.
- FX conversion in SQL is a nested loop against a 10-row table. Negligible.
- First seed takes a few seconds locally; afterwards startup is cheap.

## What we measure informally

List + insights should feel instant on a laptop against local Postgres. If either endpoint scanned the heap of 10k entities, it would show up as multi-hundred-ms JSON and a frozen table.
