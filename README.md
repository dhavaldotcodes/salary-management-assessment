# ACME Salary Management

Web app for an HR manager to maintain compensation for ~10,000 employees and answer how ACME pays people across countries.

**Persona:** HR Manager  
**Stack:** Angular 12 UI (port 4200) · Spring Boot 4 / Java 21 API (port 8080) · PostgreSQL 16

## Thinking (read these first)

- [Requirements](docs/REQUIREMENTS.md) — in/out of scope
- [Architecture](docs/ARCHITECTURE.md)
- [Trade-offs](docs/TRADEOFFS.md)
- [Performance](docs/PERFORMANCE.md)
- [AI usage](docs/AI_USAGE.md)

## Run locally

**Postgres** with database `salary_management`, user/password `postgres` / `postgres` (see `Backend/salary-management-system-be/src/main/resources/application.properties`).

```bash
# optional: local Postgres via Docker
docker compose up -d
```

Database URL, user, password, and CORS origins can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `CORS_ORIGINS`.

```bash
# API — seeds 10,000 employees on first empty database
cd Backend/salary-management-system-be
./mvnw spring-boot:run

# UI
cd Frontend/salary-management-system
npm install
npm start
```

Open [http://localhost:4200](http://localhost:4200).

## Tests

```bash
cd Backend/salary-management-system-be
./mvnw test
```

Backend tests are the source of truth for pay math and API behavior. They use H2 (no Postgres required).

```bash
cd Frontend/salary-management-system
npm test -- --watch=false --browsers=ChromeHeadless
```

## Demo flow (video)

1. **Insights** — org payroll in USD, then country / department / level.
2. **Employees** — search a name, filter India + Engineering, page through results.
3. **Edit** a salary, return to insights, confirm totals moved.
4. **Add** an employee, then **deactivate** and show they leave active payroll.
