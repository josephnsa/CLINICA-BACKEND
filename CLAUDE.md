# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with this repository.

## Build & Run Commands

```bash
# Build the project
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AppointmentServiceTest

# Run Flyway migrations manually
./mvnw flyway:migrate

# Generate MapStruct mappers (happens automatically during compile)
./mvnw compile
```

The app runs on **port 9090**. Swagger UI is available at `http://localhost:9090/swagger-ui.html`.

## Architecture: Hexagonal (Ports & Adapters)

Each business module under `src/main/java/com/clinica/salud/modules/[module]/` follows this structure:

```
[module]/
├── domain/
│   ├── model/        ← Business entities with state/behavior methods
│   └── port/         ← Repository interfaces (dependency inversion)
├── application/
│   ├── dto/          ← Request/Response DTOs (separate from domain)
│   └── usecase/      ← Application services, business logic orchestration
└── infrastructure/
    ├── persistence/  ← JPA entities, Spring Data repos, MapStruct mappers, adapters
    ├── config/       ← Module-specific config (e.g., AuthBootstrap seed)
    └── web/          ← REST controllers with @PreAuthorize
```

**Key rule:** Domain objects must not depend on infrastructure. Domain ports (interfaces) are implemented by infrastructure adapters.

## Active Modules

`auth`, `agenda` (appointments), `billing`, `clinical` (clinical notes), `exam`, `patients`, `catalog`, `inventory`, `prescription`, `security`, `hrm`, `examinations`, `reports`

## Cross-Cutting Concerns (shared/)

- **`ApiResponse<T>`** — All endpoints return this envelope: `{success, message, data, timestamp}`
- **`GlobalExceptionHandler`** — Maps `DomainException`, `BusinessRuleException`, `ResourceNotFoundException`, `UnauthorizedException` to HTTP codes
- **`JwtService`** — Issues/validates tokens with claims: `role`, `permissions`, `sedeId`
- **`SecurityConfig`** — Stateless JWT, CSRF disabled, method-level security via `@EnableMethodSecurity`

## Database & Migrations

- **PostgreSQL** on `localhost:5432/postgres` (default) or `salud_db` (dev profile)
- **Flyway** manages all schema changes — never modify JPA `ddl-auto` (set to `validate`)
- Migrations are in `src/main/resources/db/migration/` (V1–V9 currently)
- Always create a new versioned migration file (`V10__...sql`) for schema changes; never alter existing migration files

## Security Pattern

Controllers use `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` for fine-grained access control. Permissions are seeded via Flyway (`V1__create_security_tables.sql`) and managed through the `roles`/`permissions` tables. The system is multi-tenant: users belong to `sedes` (clinic branches).

## Adding a New Module

Follow the hexagonal pattern from an existing module (e.g., `agenda`):
1. Create domain model with business logic methods
2. Define repository port interface in `domain/port/`
3. Create JPA entity + MapStruct mapper + repository adapter in `infrastructure/persistence/`
4. Create use cases in `application/usecase/`
5. Create DTOs in `application/dto/`
6. Create controller in `infrastructure/web/` with `@PreAuthorize`
7. Add Flyway migration for new tables

## Tech Stack

- Java 17, Spring Boot 3.5.x
- Spring Data JPA + Hibernate, PostgreSQL, Flyway 11.x
- JWT (JJWT 0.12.3), Spring Security
- MapStruct 1.5.5 (compile-time mappers), Lombok
- SpringDoc OpenAPI 2.8.9 (Swagger)
- Maven build system
