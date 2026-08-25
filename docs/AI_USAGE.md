# AI usage

This project was built in Cursor with an agentic coding assistant (Grok), as the assessment requires.

## How AI was used

- **Framing** — turned the brief into a tight scope (manage + insights) and an explicit out-of-scope list before any code.
- **Scaffold fill-in** — Spring Boot 4 / Angular 12 were already generated; AI implemented domain, APIs, seed, UI, tests, and these docs on that stack rather than replacing it.
- **Boilerplate** — JPA entity, DTOs, Angular services, list/filter/form chrome, toast service.
- **Tests** — FX conversion, median/average, employee codes, catalog, search tokens, controller HTTP contracts, specification search (including full name and hidden inactive rows).
- **Iteration from review** — success toasts, full-name search (`John` vs `John Doe`), hide deactivated people from the directory, remove the Activate button.

## How AI was *not* trusted blindly

- Rejected over-engineering (auth, payroll, microservices, live FX, loading 10k rows into the SPA).
- Kept money in `BigDecimal`, paging in the database, and mixed-currency totals explicit.
- Reviewed Spring Boot 4 packages (`@WebMvcTest` / `@MockitoBean`) instead of pasting Boot 2/3 snippets.
- Search was verified against the running API: one-word `LIKE` on a single column is not a full-name search; the process had to be restarted for the spec change to load.
- Seed uses a fixed `Random(42)` so demos and debugging are reproducible.
- Generated queries and tests were checked for mocks that assert nothing and for accidentally summing INR + USD.

## Prompts / instructions (representative)

1. “Approach this assessment as product + engineering judgment, not the most complex system.”
2. “Use the existing Angular 12 + Spring Boot 4 + Postgres scaffold; don’t rewrite the stack.”
3. “10k employees must be paginated and aggregated in SQL. Document what we are deliberately not building.”
4. “Write fast, deterministic unit tests around FX conversion, stats, and employee validation.”
5. “Full-name search must work; deactivate should hide people from the working set, not offer Activate.”

Human (candidate) remaining accountable for: scope, currency model, whether the HR flow actually works on the running app, and the commit / demo story.
