---
trigger: always_on
---

# AI Agent Rules

## Language
- All comments, docs, commit messages, test description: **English only**

## Working Principles
- **Explicit assumptions**: State assumptions clearly; ask questions when uncertain; stop if confused
- **Simplicity first**: Don't add unrequested features, abstractions, or error handling; try simple solutions first
- **Surgical precision**: Change only what was requested; leave everything else untouched
- **Goal-oriented execution**: Convert user requests into clear, focused objectives

## Architecture

**Clean Architecture + DDD** — dependency flows inward: `infrastructure, presentation → application → domain`

### Module Responsibilities

**`domain/`** — pure business logic, no framework dependencies
- `{subdomain}/` — entity, value object, static factory (`create()`), business methods
- `{subdomain}/repository/` — repository interfaces (contracts only)
- `{subdomain}/exception/` — concrete exceptions + `{Domain}ErrorCode` enum
- `exception/` — base exception hierarchy

**`application/`** — orchestration, no domain model mutation
- `{subdomain}/usecase/` — `{Entity}CommandUseCase`, `{Entity}QueryUseCase` (interfaces)
- `{subdomain}/service/` — implements use cases; `@Service @Transactional(readOnly=true)`, write ops override with `@Transactional`
- `{subdomain}/dto/` — application-level DTOs (cross-layer data transfer)

**`infrastructure/persistence/`** — JPA implementation
- `{subdomain}/` — `{Entity}JpaEntity`, `{Entity}JpaRepository`, `{Entity}Mapper` (toEntity/toDomain), `{Entity}RepositoryImpl` (implements domain repository)

**`infrastructure/transients/`** — in-memory / session-based adapters

**`infrastructure/client/`** — external API clients

**`presentation/api/`** — HTTP layer
- `{subdomain}/` — `{Entity}Controller`, `{Entity}DtoMapper` (`@Component`), `{Entity}Dto` (nested static request/response classes with validation)
- `exception/` — `GlobalExceptionHandler` only

**`boot/`** — app entry point, security filters, module wiring via `@Enable*` annotations

### Naming Conventions
| Concept | Name |
|---|---|
| Domain entity | `Game` |
| Service | `GameManagementService` |
| Use cases | `GameCommandUseCase`, `GameQueryUseCase` |
| Controller | `GameController` |
| Presentation DTO | `GameDto` (inner: `CreateRequest`, `Response`, `ListResponse`) |
| Presentation mapper | `GameDtoMapper` |
| JPA entity | `GameJpaEntity` |
| JPA repository | `GameJpaRepository` |
| Persistence mapper | `GameMapper` |
| Repository impl | `GameRepositoryImpl` |

## Tech Stack
- **Java 25** + **Spring Framework**

## Coding Principles
- Research existing patterns before coding
- Investigate codebase first, don't guess
- Use modern Java features
- Use Optional or throw domain exceptions
- Constructor injection only (no field injection)

## Testing
- **Domain unit tests are mandatory**: Every domain entity, value object, and factory method must have a corresponding unit test in `backend/domain/src/test/`. Pure unit tests only — no Spring context, no mocks, no I/O. Use JUnit 5 + AssertJ.
- **Application unit tests are mandatory**: Every use case / service in `backend/application` must have a corresponding unit test. Use Mockito to mock repository dependencies.
- **Other modules** (presentation, infrastructure, boot): Tests are encouraged but not strictly required.
- When adding or modifying any class in domain or application, add or update its test in the same PR.

## Database Schema Management
- **Sync DDL with Entities**: When adding or modifying domain entities that require persistence, always update `backend/infrastructure/persistence/src/main/resources/ddl/database-schema.sql` to reflect the changes.

## Exception Pattern

**Hierarchy:** `BoardBuddyException` (root) → category base → concrete exception

**Category bases** (in `domain/exception/`) — choose by failure nature:
- `NotFoundException` — resource not found
- `ConflictException` — state conflict (e.g., duplicate)
- `AuthException` — authentication failure
- `ForbiddenException` — permission denied
- `ValidationException` — invalid input
- `InfrastructureException` — external/infra failure

**Concrete exceptions** live in `domain/{subdomain}/exception/`:
- `{Domain}ErrorCode` enum implements `ErrorCode` (one per subdomain)
- Exception extends category base + implements `MessageResolvable`
- Constructor captures relevant context (IDs, names, etc.)

## Time Handling

- **Local time matters + business logic depends on timezone** → store `LocalDateTime` + separate `timezone` column (e.g., `VARCHAR zone_id`); reconstruct `ZonedDateTime` in the domain object by combining them
- **Timezone is irrelevant / only absolute ordering matters** → store `Instant` only
- Never store `LocalDateTime` alone when the timezone context is ambiguous or unknown

```
// Case 1: timezone-aware (e.g., scheduled events, recurring sessions)
LocalDateTime localTime  -- what the user sees
String zoneId            -- e.g., "Asia/Seoul"
// domain object combines: ZonedDateTime.of(localTime, ZoneId.of(zoneId))

// Case 2: pure timestamp (e.g., createdAt, updatedAt, token expiry)
Instant instant          -- UTC epoch, no timezone needed
```

## Commit
- **Fallow Google commit convention**
