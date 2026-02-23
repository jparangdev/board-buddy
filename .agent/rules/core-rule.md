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
- **Clean Architecture + DDD**
- Dependency flows inward: `infrastructure, presentation → application → domain`
- Domain layer: No framework dependencies
- Use cases: One focused operation per class

## Tech Stack
- **Java 25** + **Spring Framework**
- Use modern Java features (records, sealed classes, pattern matching)
- Constructor injection only (no field injection)

## Coding Principles
- **Evidence-based**: Research existing patterns before coding
- **No assumptions**: Investigate codebase first, don't guess
- **Immutability preferred**: Use records and final fields
- **No nulls**: Use Optional or throw domain exceptions

## Testing
- **Domain unit tests are mandatory**: Every domain entity, value object, and factory method must have a corresponding unit test in `backend/domain/src/test/`.
- Domain tests must be pure unit tests — no Spring context, no mocks, no I/O.
- Use JUnit 5 + AssertJ only in domain tests.
- When adding or modifying a domain class, add or update its unit test in the same PR.

## Database Schema Management
- **Sync DDL with Entities**: When adding or modifying domain entities that require persistence, always update `backend/infrastructure/persistence/src/main/resources/ddl/database-schema.sql` to reflect the changes.

## Commit
- **Fallow Google commit convention**
