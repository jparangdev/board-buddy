---
trigger: always_on
---

# AI Agent Rules

## Language
- All comments, docs, commit messages, test description: **English only**

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

## Database Schema Management
- **Sync DDL with Entities**: When adding or modifying domain entities that require persistence, always update `backend/infrastructure/persistence/src/main/resources/ddl/database-schema.sql` to reflect the changes.
