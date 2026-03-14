# Regression Test Scenario
report created by korean

## 1. User Account Lifecycle (Create & Delete)
*   **Goal**: Register a temporary user, log in, and then delete the account. **(Results not recorded)**
*   **Steps**:
    1.  Navigate to `http://localhost:3000/register`.
    2.  Enter Email: `temp_user@test.com`.
    3.  Enter Nickname: `TempUser`.
    4.  Enter Password: `TempPass1!`.
    5.  Enter Confirm Password: `TempPass1!`.
    6.  Click "Create Account".
    7.  Verify redirection to `/login`.
    8.  Enter Email: `temp_user@test.com` and Password: `TempPass1!`.
    9.  Click "Login".
    10. Verify redirection to `/groups`.
    11. Click "Delete Account" (in User Menu or Footer).
    12. Confirm deletion in prompt.
    13. Verify redirection to `/login`.

## 1-1. Registration — Duplicate Email
*   **Goal**: Verify that registering with an already-used email shows an error.
*   **Steps**:
    1.  Register successfully with `newuser@test.com` / `ValidPass1` / `NewUser` (follow steps in Section 1).
    2.  Navigate to `http://localhost:3000/register` and attempt to register again with the same email `newuser@test.com`.
    3.  Verify an error message is displayed, e.g. "Email already in use: newuser@test.com".

## 2. Pre-seeded Test Users
*   **Goal**: Secondary users are auto-created by the server on startup (local/dev profile). No manual registration needed.
*   **Password for all seeded users**: `Test1234!`
*   **Test user tags** (used when searching members):

    | UserTag | Email | Password |
    |---|---|---|
    | `Tester#TST1` | test@test.com | `Test1234!` |
    | `PlayerOne#PLY1` | player1@test.com | `Test1234!` |
    | `PlayerTwo#PLY2` | player2@test.com | `Test1234!` |
    | `PlayerThree#PLY3` | player3@test.com | `Test1234!` |
    | `PlayerFour#PLY4` | player4@test.com | `Test1234!` |

## 3. Main User Login (User A)
*   **Goal**: Log in as the primary tester.
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `player1@test.com`.
    3.  Enter Password: `Test1234!`.
    4.  Click "Login".
    5.  Verify redirection to `/groups`.

## 4. Game Creation
*   **Goal**: Register board games to play.
*   **Steps**:
    1.  Navigate to `/games`.
    *   **Note for Automation**: "Catan", "Splendor", "Love Letter", and "Hanabi" are pre-seeded in the database. Verify they appear in the list. DO NOT attempt to create them again as it will cause a Duplicate Error. Only create "Terraforming Mars" which is not seeded.
    2.  **Add Terraforming Mars**:
        *   Click "+ Add Game" (or equivalent)
        *   Name: `Terraforming Mars`, Min: `1`, Max: `5`, Score: `High Score Wins`
    3.  Verify all 5 games appear in the list.

## 5. Group Creation & Population
*   **Goal**: Create a group and invite all users.
*   **Steps**:
    1.  Navigate to `/groups`.
    2.  Click "+ Create Group".
    3.  Name: `Board Game Crew`.
    4.  Search & Select Members (enter full userTag including discriminator):
        *   **Note for Automation**: For each member down below, type their tag into the search box, WAIT for the search results to appear below the input, and explicitly CLICK on the matching user card in the search results list. You must click the result to add them as a member chip. Do this for:
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
    2.  Select: `PlayerOne#PLY1`, `PlayerTwo#PLY2`, `PlayerThree#PLY3`.
    3.  Scores:
        *   P1 (`PlayerOne#PLY1`): `10` (Won)
        *   P2 (`PlayerTwo#PLY2`): `8`
        *   P3 (`PlayerThree#PLY3`): `5`
    4.  Save Session.

### Session B: Splendor (2 Players)
*   **Details**: Duel, P2 wins against P4.
*   **Steps**:
    1.  Click "Record Game" -> Select `Splendor`.
    2.  Select: `PlayerTwo#PLY2`, `PlayerFour#PLY4`.
    3.  Scores:
        *   P2 (`PlayerTwo#PLY2`): `15` (Won)
        *   P4 (`PlayerFour#PLY4`): `12`
    4.  Save Session.

### Session C: Terraforming Mars (4 Players)
*   **Details**: Heavy strategy, P3 wins close game.
*   **Steps**:
    1.  Click "Record Game" -> Select `Terraforming Mars`.
    2.  Select: All Players (`PlayerOne#PLY1`, `PlayerTwo#PLY2`, `PlayerThree#PLY3`, `PlayerFour#PLY4`).
    3.  Scores:
        *   P3 (`PlayerThree#PLY3`): `75` (Won)
        *   P1 (`PlayerOne#PLY1`): `72`
        *   P4 (`PlayerFour#PLY4`): `65`
        *   P2 (`PlayerTwo#PLY2`): `50`
    4.  Save Session.

### Session D: Love Letter (4 Players)
*   **Details**: Party game, P4 wins. (Using Win/Lose Strategy if configured, or just points).
*   **Steps**:
    1.  Click "Record Game" -> Select `Love Letter`.
    2.  Select: All Players (`PlayerOne#PLY1`, `PlayerTwo#PLY2`, `PlayerThree#PLY3`, `PlayerFour#PLY4`).
    3.  Outcome (if Win/Lose):
        *   P4 (`PlayerFour#PLY4`): `Won`
        *   Others: `Lost`
    4.  Save Session.

### Session E: Hanabi (Cooperative)
*   **Details**: Team play, perfect score.
*   **Steps**:
    1.  Click "Record Game" -> Select `Hanabi`.
    2.  Select: All Players (`PlayerOne#PLY1`, `PlayerTwo#PLY2`, `PlayerThree#PLY3`, `PlayerFour#PLY4`).
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

## 8. Dashboard Navigation

*   **Goal**: Verify the "View Stats" button in group header navigates to the dedicated dashboard page.
*   **Prerequisites**: Logged in as `PlayerOne`. "Board Game Crew" group exists.
*   **Steps**:
    1.  Navigate to "Board Game Crew" Group Detail page (`/groups/:id`).
    2.  Verify a "📊 View Stats" (or "📊 통계 보기") button is visible in the group header.
    3.  Click the button.
    4.  Verify URL changes to `/groups/:id/dashboard`.
    5.  Verify page heading shows "📊 Dashboard".
    6.  Verify group name is displayed as subtitle.
    7.  Click "← Back to Group" link.
    8.  Verify navigation returns to `/groups/:id`.

## 9. Dashboard — Empty State

*   **Goal**: Verify dashboard shows a friendly empty state when a group has no sessions.
*   **Steps**:
    1.  Navigate to `/groups` and create a new group named `Empty Group` (no additional members needed).
    2.  Open "Empty Group" detail page.
    3.  Click "📊 View Stats" button.
    4.  Verify page does **not** show any ranking sections or summary cards.
    5.  Verify an empty-state message is displayed (e.g. "No game sessions yet").
    6.  Verify a "Record Game" button (or equivalent CTA) is present in the empty state.
    7.  Click the "Record Game" button and verify navigation to the session creation page.

## 10. Dashboard Statistics Verification

*   **Goal**: Verify logic and display of group statistics with progress bar visualizations.
*   **Prerequisites**: All 5 game sessions from Step 6 must be recorded in "Board Game Crew".
*   **Steps**:
    1.  Navigate to "Board Game Crew" Group Detail page and click "📊 View Stats".
    2.  **Verify Summary Cards**:
        *   "Total Sessions" card shows `5`.
        *   "Total Plays" card shows `17` (3+2+4+4+4).
    3.  **Verify Most Active (🎮) section**:
        *   Section is visible with horizontal progress bars.
        *   `PlayerTwo` is ranked 1st (participated in all 5 sessions).
        *   `PlayerOne`, `PlayerThree`, `PlayerFour` are tied at 4 sessions.
        *   The 1st-place bar is visually the longest (or equal length if tied).
    4.  **Verify Most Wins (🏆) section**:
        *   Section is visible with progress bars.
        *   All four players (P1–P4) each have `1 W` from their respective session wins.
        *   Hanabi (Co-op, Session E) winner handling is consistent with expected co-op logic.
    5.  **Verify Win Rate (📈) section**:
        *   All players have 4–5 sessions, exceeding the 3-game minimum, so rankings are displayed.
        *   `PlayerTwo`'s win rate is lower than the others (1 win / 5 sessions ≈ 20%) and ranks last.
        *   Progress bar widths are proportional to win rate percentages.
    6.  **Verify Popular Games (🎲) section**:
        *   All 5 games (`Catan`, `Splendor`, `Terraforming Mars`, `Love Letter`, `Hanabi`) appear.
        *   Each shows `1x` play count with equal-length bars.
    7.  **Verify Win Rate minimum threshold**:
        *   Create a new group "Win Rate Test", invite only one other player.
        *   Record 2 sessions (below the 3-game minimum for win rate ranking).
        *   Navigate to its dashboard and verify the Win Rate section shows "Need 3+ games" (instead of rankings).
