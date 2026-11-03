# Employee Management System (EMS)

A full-stack Java project built to cover the topics that come up repeatedly
in **mid-to-hard Java full-stack developer interviews**: layered Spring Boot
architecture, JPA relationships, Spring Security + JWT, validation, global
exception handling, pagination/sorting, caching, unit testing, and a small
JS frontend that consumes the API.

## Stack
- Java 17, Spring Boot 3.3 (Web, Data JPA, Security, Validation, Cache)
- **PostgreSQL** (via Docker Compose for local dev); H2 in-memory used only for the `test` profile so unit tests don't need a live DB
- JWT auth (jjwt), BCrypt password hashing
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + AssertJ
- Plain HTML/CSS/JS frontend (`frontend/index.html`) — no build step needed

## Domain model
```
Department 1 ──< Employee >── Task
                    │  self-referential
                    └──> manager (Employee)
```
- `Employee.department` → `@ManyToOne(LAZY)`
- `Department.employees` → `@OneToMany(mappedBy = "department")`
- `Employee.manager` → self-referential `@ManyToOne`
- `Task.assignedTo` → `@ManyToOne`

## Running it

**1. Start PostgreSQL** (Docker required):
```bash
cd ems
docker compose up -d
```
This starts Postgres on `localhost:5432` with db `emsdb`, user `ems_user`,
password `ems_password` (see `docker-compose.yml`). `ddl-auto: update` means
Hibernate creates the tables automatically on first run — no manual schema
step needed.

**2. Run the app:**
```bash
mvn spring-boot:run
```
Connection settings default to the Docker Compose values above and can be
overridden with the `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` env vars (see
`.env.example`) — e.g. to point at a managed Postgres instance instead.

- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- Frontend: just open `frontend/index.html` in a browser (uses `fetch` against `localhost:8080`)

A default admin user is seeded on startup: **admin / Admin@123**. Log in to
get a JWT, or register a new user (defaults to `ROLE_USER`).

**Running tests** (`mvn test`) automatically uses the `test` Spring profile
(`application-test.yml`), which points at an in-memory H2 database in
Postgres-compatibility mode — so `mvn test` works with zero setup, no Docker
needed for the unit tests already in the project.

## What each part demonstrates (use this as an interview cheat-sheet)

| Area | Where | Talking points |
|---|---|---|
| Layered architecture | `controller/ → service/ → repository/` | separation of concerns, why controllers stay thin |
| DTO pattern | `dto/request`, `dto/response`, `mapper/` | never expose entities directly; avoids over-posting & lazy-loading serialization issues |
| JPA relationships | `entity/` | `@ManyToOne` vs `@OneToMany`, `mappedBy`, `FetchType.LAZY` default, `CascadeType`, `orphanRemoval` |
| N+1 problem | `EmployeeRepository.search()` | `JOIN FETCH` in JPQL to eager-fetch in one query |
| Bean validation | `dto/request/*Request.java` | `@NotBlank`, `@Email`, `@PositiveOrZero`, handled centrally |
| Global exception handling | `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice`, consistent `ApiError` shape, never leak stack traces |
| Spring Security + JWT | `config/SecurityConfig.java`, `JwtAuthenticationFilter.java`, `JwtService.java` | stateless sessions, filter chain ordering, `UserDetailsService`, BCrypt, `@PreAuthorize` method security |
| Pagination & sorting | `EmployeeController.search`, `TaskController` | `Pageable`, `Page<T>`, `@PageableDefault` |
| Caching | `DepartmentServiceImpl` | `@Cacheable` / `@CacheEvict`, cache invalidation on write |
| Transactions | `@Transactional`, `readOnly = true` on queries | dirty checking (no explicit `save()` needed on `update()`) |
| Testing | `src/test/.../service/*Test.java` | mocking repositories with Mockito, `@ExtendWith(MockitoExtension.class)`, testing business rules in isolation from the DB |
| API docs | `config/OpenApiConfig.java` | OpenAPI/Swagger annotations, bearer auth scheme |
| CORS | `SecurityConfig.corsConfigurationSource()` | why it's needed for a separate frontend origin |
| DB portability | `application.yml` vs `application-test.yml` | prod uses real Postgres, tests use H2 in Postgres-compat mode — trade-off: fast/no-setup tests vs. risk of dialect differences slipping through (mention Testcontainers as the fix) |

## Likely follow-up interview questions to rehearse against this code
1. Why is `Employee.department` `LAZY` but might still trigger N+1 — how did we avoid it in `search()`?
2. Walk through what happens end-to-end when a request hits `/api/employees` with an expired JWT.
3. Why use `DaoAuthenticationProvider` + `AuthenticationManager` instead of comparing passwords manually?
4. What's the difference between `@Transactional(readOnly = true)` and a plain `@Transactional`?
5. How would you paginate `search()` at the database level vs. in memory, and why does it matter at scale?
6. Why does `update()` in `DepartmentServiceImpl` not call `.save()` explicitly?
7. How would you swap H2 for PostgreSQL in production, and what would break?
8. How would you extend this to refresh tokens instead of a single 1-hour JWT?
9. What's the tradeoff of `ConcurrentMapCacheManager` vs Redis for the `departments` cache in a multi-instance deployment?
10. How would you add optimistic locking (`@Version`) to prevent lost updates on concurrent Employee edits?

## Suggested extensions (good "what would you add next" answers)
- Refresh tokens + token blacklist on logout
- `@Version` optimistic locking on `Employee`
- Flyway/Liquibase migrations instead of `ddl-auto: update`
- Testcontainers-based integration tests against real Postgres
- Rate limiting on `/api/auth/login`
- Audit fields (`createdAt`, `updatedAt`, `createdBy`) via `@EntityListeners(AuditingEntityListener.class)`
