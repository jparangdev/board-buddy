# Game & GameSession Domain TODO

> PRD 3.3 GameType + 3.4 GameSession 기반
> 기존 패턴(Group 도메인)을 따라 Clean Architecture 레이어별로 구현

---

## 1. Domain Layer

### 1.1 Game (게임 종류) 엔티티
- [x] `Game` 도메인 엔티티 생성
  - `id`: Long
  - `name`: String (게임 이름, e.g. "Splendor", "Catan")
  - `minPlayers`: int (최소 인원)
  - `maxPlayers`: int (최대 인원)
  - `scoreStrategy`: ScoreStrategy (HIGH_WIN / LOW_WIN)
  - `createdAt`: LocalDateTime
- [x] `ScoreStrategy` enum 생성 (HIGH_WIN, LOW_WIN)
- [x] `Game.create()` 팩토리 메서드
- [x] `GameRepository` 인터페이스 (domain layer)
  - `save(Game): Game`
  - `findById(Long): Optional<Game>`
  - `findAll(): List<Game>`
  - `existsByName(String): boolean`

### 1.2 GameSession (게임 세션) 엔티티
- [x] `GameSession` 도메인 엔티티 생성
  - `id`: Long
  - `groupId`: Long (소속 모임)
  - `gameId`: Long (게임 종류)
  - `playedAt`: LocalDateTime (플레이 일시)
  - `createdAt`: LocalDateTime
- [x] `GameSession.create()` 팩토리 메서드

### 1.3 GameResult (게임 결과) 엔티티
- [x] `GameResult` 도메인 엔티티 생성
  - `id`: Long
  - `sessionId`: Long (소속 세션)
  - `userId`: Long (참여 유저)
  - `score`: Integer (nullable - 순위만 기록하는 경우)
  - `rank`: int (순위)
- [x] `GameResult.create()` 팩토리 메서드

### 1.4 Repository 인터페이스 (domain layer)
- [x] `GameSessionRepository` 인터페이스
  - `save(GameSession): GameSession`
  - `findById(Long): Optional<GameSession>`
  - `findAllByGroupId(Long): List<GameSession>`
- [x] `GameResultRepository` 인터페이스
  - `saveAll(List<GameResult>): List<GameResult>`
  - `findAllBySessionId(Long): List<GameResult>`
  - `findAllByUserId(Long): List<GameResult>`

### 1.5 Domain Exception
- [x] `GameNotFoundException`
- [x] `GameSessionNotFoundException`
- [x] `DuplicateGameNameException`

---

## 2. Application Layer

### 2.1 Game Use Cases
- [x] `GameQueryUseCase` 인터페이스
  - `getGameList(): List<Game>` - 전체 게임 목록 조회
  - `getGameDetail(Long gameId): Game` - 게임 상세 조회
- [x] `GameCommandUseCase` 인터페이스
  - `createGame(String name, int minPlayers, int maxPlayers, ScoreStrategy): Game` - 게임 등록

### 2.2 GameSession Use Cases
- [x] `GameSessionQueryUseCase` 인터페이스
  - `getSessionsByGroup(Long groupId): List<GameSession>` - 모임별 세션 목록
  - `getSessionDetail(Long sessionId): GameSession` - 세션 상세 (결과 포함)
  - `getSessionResults(Long sessionId): List<GameResult>` - 세션 결과 조회
- [x] `GameSessionCommandUseCase` 인터페이스
  - `createSession(Long groupId, Long gameId, LocalDateTime playedAt, List<ResultInput> results): GameSession` - 세션 생성 + 결과 저장

### 2.3 Service 구현
- [x] `GameManagementService` (@Service, @Transactional(readOnly=true))
  - implements `GameQueryUseCase`, `GameCommandUseCase`
- [x] `GameSessionManagementService` (@Service, @Transactional(readOnly=true))
  - implements `GameSessionQueryUseCase`, `GameSessionCommandUseCase`
  - 세션 생성 시 모임 멤버 검증 (참여자가 모임에 속하는지)
  - 순위 자동 계산 로직 (ScoreStrategy에 따라 HIGH_WIN/LOW_WIN 정렬)

### 2.4 Application Repository 인터페이스
- [ ] application layer에도 동일 Repository 인터페이스 배치 (domain과 sync)

---

## 3. Infrastructure Layer (Persistence)

### 3.1 DDL (database-schema.sql)
- [x] `games` 테이블 추가
- [x] `game_sessions` 테이블 추가
- [x] `game_results` 테이블 추가

### 3.2 JPA Entity
- [x] `GameJpaEntity`
- [x] `GameSessionJpaEntity`
- [x] `GameResultJpaEntity`

### 3.3 Mapper
- [x] `GameMapper` (@Component, toEntity/toDomain)
- [x] `GameSessionMapper` (@Component, toEntity/toDomain)
- [x] `GameResultMapper` (@Component, toEntity/toDomain)

### 3.4 JPA Repository (Spring Data)
- [x] `GameJpaRepository` extends JpaRepository
- [x] `GameSessionJpaRepository` extends JpaRepository
- [x] `GameResultJpaRepository` extends JpaRepository

### 3.5 Repository 구현체
- [x] `GameRepositoryImpl` (@Repository)
- [x] `GameSessionRepositoryImpl` (@Repository)
- [x] `GameResultRepositoryImpl` (@Repository)

---

## 4. Presentation Layer (API)

### 4.1 Game API
| Method | Endpoint | Description | Priority |
|--------|----------|-------------|----------|
| `GET` | `/api/v1/games` | 게임 종류 목록 조회 | P0 |
| `GET` | `/api/v1/games/{id}` | 게임 상세 조회 | P0 |
| `POST` | `/api/v1/games` | 게임 종류 등록 | P0 |

- [x] `GameController` (@RestController, @RequestMapping("/api/v1/games"))
- [x] `GameDto` (Request/Response records)
  - `CreateRequest`: name, minPlayers, maxPlayers, scoreStrategy
  - `Response`: id, name, minPlayers, maxPlayers, scoreStrategy, createdAt
  - `GameListResponse`: List<Response> games
- [x] `GameDtoMapper` (@Component)

### 4.2 GameSession API
| Method | Endpoint | Description | Priority |
|--------|----------|-------------|----------|
| `POST` | `/api/v1/groups/{groupId}/sessions` | 게임 세션 생성 + 결과 저장 | P0 |
| `GET` | `/api/v1/groups/{groupId}/sessions` | 모임별 세션 목록 조회 | P0 |
| `GET` | `/api/v1/groups/{groupId}/sessions/{sessionId}` | 세션 상세 + 결과 조회 | P0 |

- [x] `GameSessionController` (@RestController, @RequestMapping("/api/v1/groups/{groupId}/sessions"))
- [x] `GameSessionDto` (Request/Response records)
  - `CreateRequest`: gameId, playedAt, List<ResultInput> results
  - `ResultInput`: userId, score (nullable)
  - `Response`: id, groupId, gameId, gameName, playedAt, createdAt
  - `DetailResponse`: Response + List<ResultResponse> results
  - `ResultResponse`: userId, nickname, userTag, score, rank
  - `SessionListResponse`: List<Response> sessions
- [x] `GameDtoMapper`에 세션 관련 매핑 메서드 통합

### 4.3 Controller Test
- [x] `GameControllerTest` (@WebMvcTest)
  - 게임 목록 조회 테스트
  - 게임 상세 조회 테스트
  - 게임 등록 테스트 (with csrf)
- [x] `GameSessionControllerTest` (@WebMvcTest)
  - 세션 생성 테스트 (with csrf)
  - 세션 목록 조회 테스트
  - 세션 상세 조회 테스트

### 4.4 GlobalExceptionHandler
- [x] `GameNotFoundException` -> 404
- [x] `GameSessionNotFoundException` -> 404
- [x] `DuplicateGameNameException` -> 409

---

## 5. Frontend

### 5.1 Type 정의
- [x] `frontend/src/types/game.ts`
  - `Game`: id, name, minPlayers, maxPlayers, scoreStrategy, createdAt
  - `GameSession`: id, groupId, gameId, gameName, playedAt, createdAt
  - `GameResult`: userId, nickname, userTag, score, rank
  - `GameSessionDetail`: GameSession + results: GameResult[]
  - Request/Response 타입

### 5.2 API Service
- [x] `frontend/src/services/game.ts`
  - `getGames(): Promise<Game[]>`
  - `getGameById(id): Promise<Game>`
  - `createGame(request): Promise<Game>`
- [x] `frontend/src/services/gameSession.ts`
  - `createSession(groupId, request): Promise<GameSession>`
  - `getSessionsByGroup(groupId): Promise<GameSession[]>`
  - `getSessionDetail(groupId, sessionId): Promise<GameSessionDetail>`

### 5.3 Pages & Components
- [x] **게임 목록 페이지** (`GameListPage.tsx`)
  - 등록된 게임 카드 목록 (이름, 인원수, 점수 방식)
  - 게임 등록 버튼 + 모달
- [x] **게임 기록 생성 플로우** (`CreateSessionPage.tsx`)
  - Step 1: 게임 종류 선택
  - Step 2: 참여 멤버 선택 (모임 멤버 중)
  - Step 3: 스코어 입력
  - Step 4: 결과 확인 + 저장
- [x] **세션 목록** (GroupDetailPage에 섹션 추가)
  - 모임 상세 페이지에서 게임 기록 목록 표시
  - 각 세션 카드: 게임 이름, 플레이 일시
- [x] **세션 상세** (`SessionDetailPage.tsx`)
  - 게임 정보, 플레이 일시
  - 결과 테이블: 순위, 유저, 점수 (1/2/3위 색상 하이라이트)

### 5.4 Routing
- [x] `/games` -> GameListPage
- [x] `/groups/:groupId/sessions/new` -> CreateSessionPage
- [x] `/groups/:groupId/sessions/:sessionId` -> SessionDetailPage

---

## 6. 구현 순서 (권장)

1. ~~**Domain Layer** (Game -> GameSession -> GameResult)~~ DONE
2. ~~**Infrastructure DDL** (database-schema.sql)~~ DONE
3. ~~**Infrastructure JPA** (Entity, Mapper, Repository)~~ DONE
4. ~~**Application Layer** (UseCase, Service)~~ DONE (application repo sync 제외)
5. ~~**Presentation API** (Controller, DTO, Test)~~ DONE
6. ~~**Frontend** (Types -> Service -> Pages)~~ DONE
