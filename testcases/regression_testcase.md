# Regression Test Scenario

## 1. User Account Lifecycle (Create & Delete)
*   **Goal**: Create a temporary user, log in, and then delete the account. **(Results not recorded)**
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `temp_user@test.com`.
    3.  Enter Nickname: `TempUser`.
    4.  Click "Start Testing" (Creates account).
    5.  Verify redirection to `/groups`.
    6.  Click "Delete Account" (in User Menu or Footer).
    7.  Confirm deletion in prompt.
    8.  Verify redirection to `/login`.

## 2. Pre-seeded Test Users
*   **Goal**: Secondary users are auto-created by the server on startup (local/dev profile). No manual registration needed.
*   **Test user tags** (used when searching members):

    | UserTag | Email |
    |---|---|
    | `Tester#TST1` | test@test.com |
    | `PlayerOne#PLY1` | player1@test.com |
    | `PlayerTwo#PLY2` | player2@test.com |
    | `PlayerThree#PLY3` | player3@test.com |
    | `PlayerFour#PLY4` | player4@test.com |

## 3. Main User Login (User A)
*   **Goal**: Log in as the primary tester.
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `player1@test.com`.
    3.  Enter Nickname: `PlayerOne`.
    4.  Click "Start Testing".
    5.  Verify redirection to `/groups`.

## 4. Game Creation
*   **Goal**: Register board games to play.
*   **Steps**:
    1.  Navigate to `/games`.
    2.  **Add Catan**:
        *   Name: `Catan`, Min: `3`, Max: `4`, Score: `High Score Wins`
    3.  **Add Splendor**:
        *   Name: `Splendor`, Min: `2`, Max: `4`, Score: `High Score Wins`
    4.  **Add Terraforming Mars**:
        *   Name: `Terraforming Mars`, Min: `1`, Max: `5`, Score: `High Score Wins`
    5.  **Add Love Letter**:
        *   Name: `Love Letter`, Min: `2`, Max: `4`, Score: `Win/Lose Strategy` (or High Score if token count) -> Use `Win/Lose` for variety.
    6.  **Add Hanabi**:
        *   Name: `Hanabi`, Min: `2`, Max: `5`, Score: `Cooperative`
    7.  Verify all games appear in the list.

## 5. Group Creation & Population
*   **Goal**: Create a group and invite all users.
*   **Steps**:
    1.  Navigate to `/groups`.
    2.  Click "+ Create Group".
    3.  Name: `Board Game Crew`.
    4.  Search & Select Members (enter full userTag including discriminator):
        *   `PlayerTwo#PLY2`
        *   `PlayerThree#PLY3`
        *   `PlayerFour#PLY4`
    5.  Click "Create Group".
    6.  Verify redirection to `/groups`.
    7.  Click on `Board Game Crew`.
    8.  Verify Member List contains `PlayerOne` (Owner) and Players 2, 3, 4.

## 6. Record Game Sessions (Diverse Scenarios)
*   **Goal**: Record various sessions to populate dashboard data.

### Session A: Catan (3 Players)
*   **Details**: Classic game, P1 wins.
*   **Steps**:
    1.  Click "Record Game" -> Select `Catan`.
    2.  Select: `PlayerOne`, `PlayerTwo`, `PlayerThree`.
    3.  Scores:
        *   P1: `10` (Won)
        *   P2: `8`
        *   P3: `5`
    4.  Save Session.

### Session B: Splendor (2 Players)
*   **Details**: Duel, P2 wins against P4.
*   **Steps**:
    1.  Click "Record Game" -> Select `Splendor`.
    2.  Select: `PlayerTwo`, `PlayerFour`.
    3.  Scores:
        *   P2: `15` (Won)
        *   P4: `12`
    4.  Save Session.

### Session C: Terraforming Mars (4 Players)
*   **Details**: Heavy strategy, P3 wins close game.
*   **Steps**:
    1.  Click "Record Game" -> Select `Terraforming Mars`.
    2.  Select: All Players (`P1`, `P2`, `P3`, `P4`).
    3.  Scores:
        *   P3: `75` (Won)
        *   P1: `72`
        *   P4: `65`
        *   P2: `50`
    4.  Save Session.

### Session D: Love Letter (4 Players)
*   **Details**: Party game, P4 wins. (Using Win/Lose Strategy if configured, or just points).
*   **Steps**:
    1.  Click "Record Game" -> Select `Love Letter`.
    2.  Select: All Players.
    3.  Outcome (if Win/Lose):
        *   P4: `Won`
        *   Others: `Lost`
    4.  Save Session.

### Session E: Hanabi (Cooperative)
*   **Details**: Team play, perfect score.
*   **Steps**:
    1.  Click "Record Game" -> Select `Hanabi`.
    2.  Select: All Players.
    3.  Team Result: `Won` (or Score `25`).
    4.  Save Session.

## 7. Group Reordering
*   **Goal**: Verify drag-and-drop persistence.
*   **Steps**:
    1.  Navigate to `/groups`.
    2.  Create a dummy group "Test Group".
    3.  Drag "Test Group" above "Board Game Crew".
    4.  Refresh page.
    5.  Verify order persists.

## 8. Dashboard Verification
*   **Goal**: Verify logic and display of group statistics.
*   **Prerequisites**: All previous game sessions must be recorded.
*   **Steps**:
    1.  Navigate to "Board Game Crew" Group Detail page.
    2.  Scroll to Statistics Section (🏆 Stats).
    3.  **Verify Summary**:
        *   Total Sessions: `5`.
        *   Total Participations: `17` (3+2+4+4+4).
    4.  **Verify Most Active**:
        *   `PlayerTwo` & `PlayerFour` at top (4 sessions).
    5.  **Verify Most Wins**:
        *   `PlayerOne`: 1 Win (Catan)
        *   `PlayerTwo`: 1 Win (Splendor)
        *   `PlayerThree`: 1 Win (Terraforming Mars)
        *   `PlayerFour`: 1 Win (Love Letter)
        *   *Note*: Hanabi (Co-op) usually counts as win for everyone or separate logic.
    6.  **Verify Popular Games**:
        *   All 5 games should appear with `1 play`.
