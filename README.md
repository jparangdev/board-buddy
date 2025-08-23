# BoardBuddies

A small multi-module Spring Boot project for board game user management, organized with a ports-and-adapters (hexagonal) architecture.

## Project Description
BoardBuddies helps board‑game players create groups for their sessions and, inside each group, record wins/losses and scoreboards, and view player rankings over time. The goal is to make it easy to organize meetups, keep track of results, and surface competitive standings.

This README intentionally focuses on a visual overview and local execution only.

## Architecture at a Glance

### Module Topology
```mermaid
flowchart LR
    subgraph Web [web]
      C[REST Controllers]\nDTO/Mapper
    end
    subgraph Application [application]
      U[Use Cases]\nServices
    end
    subgraph Domain [domain]
      D[Domain Entities]\nPorts
    end
    subgraph DAL [dal]
      R[Persistence Adapter]\nJPA Repositories
    end
    subgraph Bootstrap [bootstrap]
      B[App Entry Point]
    end

    C --> U
    U --> D
    U --> R
    R --> D
    B --> C
```

### Project Structure (Modules)
```mermaid
mindmap
  root((boardbuddies))
    bootstrap
      BoardbuddiesApplication
    web
      adapters
        input
          rest
            controller
            dto
            mapper
    application
      service
      usecases
      repository (ports)
    domain
      entity
    dal
      config
      entity (JPA)
      repository (Spring Data)
      mapper
      resources (datasource.yml)
```

### Technology Stack (Overview)
```mermaid
graph TD
  J[Java 21]
  SB[Spring Boot 3]
  JPA[Spring Data JPA]
  L[Lombok]
  G[Gradle Wrapper]

  J --> SB
  SB --> JPA
  SB --> L
  G --> SB
```

Note: Concrete APIs, detailed requirements, and configuration how-tos are intentionally omitted as they evolve over time.

## Run Locally

- Build
```bash
./gradlew clean build
```

- Run (bootstrap module)
```bash
./gradlew :bootstrap:bootRun
```

Default port: http://localhost:8080
