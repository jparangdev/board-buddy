# BoardBuddy

BoardBuddy helps board-game players create groups for their sessions and, inside each group, record wins/losses and scoreboards, and view player rankings over time. The goal is to make it easy to organize meetups, keep track of results, and surface competitive standings.

## Stack

**Backend:** Java 25 + Spring Boot 4.x + Gradle  
**Frontend:** React 19 + TypeScript + Vite + CSS Modules  
**Architecture:** Clean Architecture + Domain-Driven Design (DDD)

## Project Structure

```
board-buddy/
├── backend/
│   ├── boot/                    # Bootstrap module (entry point)
│   ├── presentation/            # API controllers & exception handling
│   ├── application/             # Use cases & business logic
│   ├── domain/                  # Domain entities & repository interfaces
│   └── infrastructure/          # JPA entities, mappers, persistence
└── frontend/
    ├── src/
    │   ├── pages/               # Page components (groups, sessions, profile, etc.)
    │   ├── components/          # Reusable UI components
    │   ├── services/            # API client & service layer
    │   ├── hooks/               # Custom React hooks (useAuth, useDebounce, etc.)
    │   ├── types/               # TypeScript type definitions
    │   ├── utils/               # Helper utilities
    │   ├── i18n/                # Internationalization
    │   ├── assets/              # Images, icons, etc.
    │   └── App.tsx
    ├── public/
    └── e2e/                     # End-to-end tests (Playwright)
```

## Backend Architecture

### Module Dependency Flow
```
boot → presentation → application → domain
     → infrastructure → application
```

### Key Patterns

- **Repository Pattern:** Interface definitions in both `domain` and `application` layers (keep in sync)
- **DTOs & Mappers:** DtoMapper classes in `presentation` layer, annotated `@Component`
- **Use Cases:** UseCase interfaces in `application`, Service impls annotated `@Service @Transactional`
- **Entities:** JPA entities in `infrastructure/persistence`, converted to domain models via mappers
- **Exception Handling:** Global exception handler at `presentation/api/exception/GlobalExceptionHandler.java`
- **List Responses:** DTO list wrappers pattern: `GroupListResponse { List<Response> groups }`

### Testing
- Controllers: `@WebMvcTest` + `@MockitoBean` + `@WithMockUser`
- JSON serialization: `tools.jackson.databind.json.JsonMapper` (Spring Boot 4.x)
- CSRF tokens required for POST/PUT/DELETE: `.with(csrf())`

## Frontend Architecture

- **API Client:** Custom Fetch-based (no axios), located at `src/services/api.ts`
- **Styling:** CSS Modules with design tokens (--color-*, --spacing-*, --radius-*)
- **Global Classes:** `.btn`, `.btn-primary`, `.btn-secondary`, `.btn-danger`, `.input`, `.form-group`
- **State:** React hooks + Context API
- **i18n:** Multi-language support via `src/i18n/`

## Run Locally

### Backend

Build:
```bash
cd backend
./gradlew clean build
```

Run:
```bash
./gradlew :boot:bootRun
```

Backend runs on `http://localhost:8080`

### Frontend

Install dependencies:
```bash
cd frontend
npm install
```

Start dev server:
```bash
npm run dev
```

Frontend runs on `http://localhost:5173` (Vite default)

### Full Setup
```bash
# Terminal 1: Backend
cd backend
./gradlew :boot:bootRun

# Terminal 2: Frontend
cd frontend
npm run dev
```

## Testing

### Backend
```bash
cd backend
./gradlew test
```

### Frontend
```bash
cd frontend
npm test              # Unit tests
npm run test:e2e     # Playwright E2E tests
```

## Database

Schema is defined at: `backend/infrastructure/persistence/src/main/resources/ddl/database-schema.sql`

When making schema changes, update the DDL file (Hibernate runs in validate mode).
