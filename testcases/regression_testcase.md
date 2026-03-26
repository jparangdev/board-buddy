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

## 1-2. Login — Wrong Password
*   **Goal**: Verify that entering a wrong password shows an error and does not redirect.
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `player1@test.com`.
    3.  Enter Password: `WrongPassword!`.
    4.  Click "Login".
    5.  Verify an error message is displayed (e.g. "Invalid email or password").
    6.  Verify the user remains on `/login` (no redirection).

## 1-3. Logout
*   **Goal**: Verify that logging out invalidates the session and blocks access to protected pages.
*   **Prerequisites**: Logged in as any user.
*   **Steps**:
    1.  Click "Logout" in the navigation menu.
    2.  Verify redirection to `/login`.
    3.  Attempt to navigate directly to `/groups`.
    4.  Verify redirection back to `/login` (protected route guard).

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

## 4. Game List Verification & Global Game Creation
*   **Goal**: Verify pre-seeded Official games and register a new Global board game.
*   **Steps**:
    1.  Navigate to `/games`.
    *   **Note for Automation**: "Catan", "Splendor", "Love Letter", and "Hanabi" are pre-seeded in the database as Official Games. Verify they appear in the list. DO NOT attempt to create them again as it will cause a Duplicate Error. Only create "Terraforming Mars" which is not seeded.
    2.  **Add Terraforming Mars**:
        *   Click "+ Add Game" (or equivalent)
        *   Name: `Terraforming Mars`, Min: `1`, Max: `5`, Score: `High Score Wins` (HIGH_WIN)
    3.  Verify all 5 games appear in the official list.

## 4-1. Game Creation — Duplicate Name
*   **Goal**: Verify that creating a game with a name that already exists shows an error.
*   **Prerequisites**: "Catan" already exists as an official game.
*   **Steps**:
    1.  Navigate to `/games`.
    2.  Click "+ Add Game".
    3.  Enter Name: `Catan`, Min: `2`, Max: `6`, Score: `HIGH_WIN`.
    4.  Submit.
    5.  Verify an error message is displayed (e.g. "Game already exists").
    6.  Verify no duplicate entry appears in the game list.

## 5. Group Creation & Population
*   **Goal**: Create a group and send invitations to members.
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
    8.  Verify Member List contains **only** `PlayerOne` (Owner) — invited players have not yet accepted their invitations.

## 5-1. Member Search by Email
*   **Goal**: Verify that users can be found by email address (not just by userTag).
*   **Prerequisites**: Logged in as `PlayerOne`. On the Create Group modal or an invite flow.
*   **Steps**:
    1.  Navigate to `/groups` and click "+ Create Group".
    2.  In the member search input, type `player2@test.com` (full email address).
    3.  Wait for search results to appear.
    4.  Verify `PlayerTwo#PLY2` appears in the results.
    5.  Click on the result to add as a member chip.
    6.  Verify the chip is displayed with `PlayerTwo`'s nickname.
    7.  Close/cancel the modal (do not save — this test is for search only).

## 5-2. Invitation — Accept Flow (PlayerTwo, PlayerThree, PlayerFour)
*   **Goal**: Verify that invited users receive and can accept invitations, and that membership is reflected after acceptance.
*   **Prerequisites**: Section 5 is complete — `PlayerOne` has created "Board Game Crew" and sent invitations to P2, P3, P4.
*   **Steps**:

    **As PlayerTwo (`player2@test.com`):**
    1.  Log out of `PlayerOne`.
    2.  Log in as `player2@test.com` / `Test1234!`.
    3.  Navigate to `/invitations`.
    4.  Verify an invitation from `PlayerOne` for group `Board Game Crew` is listed.
    5.  Click "Accept".
    6.  Verify the invitation disappears from the list.
    7.  Navigate to `/groups` and verify `Board Game Crew` appears in the group list.

    **As PlayerThree (`player3@test.com`):**
    8.  Log out of `PlayerTwo`.
    9.  Log in as `player3@test.com` / `Test1234!`.
    10. Navigate to `/invitations` and accept the `Board Game Crew` invitation.
    11. Verify `Board Game Crew` appears in `/groups`.

    **As PlayerFour (`player4@test.com`):**
    12. Log out of `PlayerThree`.
    13. Log in as `player4@test.com` / `Test1234!`.
    14. Navigate to `/invitations` and accept the `Board Game Crew` invitation.
    15. Verify `Board Game Crew` appears in `/groups`.

    **Back as PlayerOne:**
    16. Log out of `PlayerFour`.
    17. Log in as `player1@test.com` / `Test1234!`.
    18. Navigate to `Board Game Crew` group detail page.
    19. Verify Member List now contains all four players: `PlayerOne`, `PlayerTwo`, `PlayerThree`, `PlayerFour`.

## 5-3. Invitation — Reject Flow
*   **Goal**: Verify that rejecting an invitation removes it and does not add the user to the group.
*   **Prerequisites**: `PlayerOne` is logged in and "Board Game Crew" exists.
*   **Steps**:
    1.  As `PlayerOne`, create a new group named `Reject Test Group` and invite `Tester#TST1`.
    2.  Log out and log in as `test@test.com` / `Test1234!`.
    3.  Navigate to `/invitations`.
    4.  Verify the invitation for `Reject Test Group` is listed.
    5.  Click "Reject".
    6.  Verify the invitation disappears from the list.
    7.  Navigate to `/groups` and verify `Reject Test Group` does **not** appear.

## 5-4. Invitation — Empty State
*   **Goal**: Verify the invitations page shows an empty state when there are no pending invitations.
*   **Prerequisites**: Logged in as a user with no pending invitations.
*   **Steps**:
    1.  Navigate to `/invitations`.
    2.  Verify no invitation cards are shown.
    3.  Verify an empty-state message is displayed (e.g. "No pending invitations").
    4.  Verify a "Back to Groups" button is present and navigates to `/groups`.

## 6. Record Game Sessions (Diverse Scenarios)
*   **Goal**: Record various sessions to populate dashboard data.
*   **Prerequisites**: Logged in as `PlayerOne`. All four players are members of "Board Game Crew" (Section 5-2 complete).

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

### Session F: Custom Game Creation (In-Session)
*   **Details**: Create a custom game within the group, then record a session for it.
*   **Steps**:
    1.  Click "Record Game" -> Click "+ Add Custom Game".
    2.  In the modal: Name: `House Rules Chess`, Min: `2`, Max: `10`, Score Strategy: `Low Wins` (or `LOW_WIN`).
    3.  Click "Add Game" (creates Custom Game and appears under "Custom Games").
    4.  Select `House Rules Chess` under the "Custom Games" section.
    5.  Select: `PlayerOne#PLY1`, `PlayerTwo#PLY2` for the session.
    6.  Scores (Low Wins):
        *   P1 (`PlayerOne#PLY1`): `2` (Won - lowest score)
        *   P2 (`PlayerTwo#PLY2`): `3`
    7.  Save Session.

### Session G: Rank Only (RANK_ONLY)
*   **Details**: A game where only finishing order matters, no numeric scores.
*   **Steps**:
    1.  Click "Record Game" -> Select any game (e.g. `Catan`).
    2.  Select: All Players (`PlayerOne#PLY1`, `PlayerTwo#PLY2`, `PlayerThree#PLY3`, `PlayerFour#PLY4`).
    3.  In the score strategy dropdown, change to `Rank Only` (`RANK_ONLY`).
    4.  Set Winner Count to `1`.
    5.  Drag and drop players to set finishing order:
        *   #1: `PlayerOne#PLY1`
        *   #2: `PlayerTwo#PLY2`
        *   #3: `PlayerThree#PLY3`
        *   #4: `PlayerFour#PLY4`
    6.  Verify the drag handle (⠿) is visible for each player row.
    7.  Save Session.
    8.  Verify the session appears in the group session list.

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
*   **Prerequisites**: All 7 game sessions from Step 6 (A–G) must be recorded in "Board Game Crew".
*   **Steps**:
    1.  Navigate to "Board Game Crew" Group Detail page and click "📊 View Stats".
    2.  **Verify Summary Cards**:
        *   "Total Sessions" card shows `7`.
        *   "Total Plays" card shows `23` (3+2+4+4+4+2+4).
    3.  **Verify Most Active (🎮) section**:
        *   Section is visible with horizontal progress bars.
        *   `PlayerTwo` is ranked 1st (participated in all 6 sessions A–F, plus G).
        *   `PlayerOne`, `PlayerThree`, `PlayerFour` are tied at 5 sessions (skipped in Session B or F).
        *   The 1st-place bar is visually the longest (or equal length if tied).
    4.  **Verify Most Wins (🏆) section**:
        *   Section is visible with progress bars.
        *   All four players (P1–P4) each have at least `1 W` from their respective session wins.
        *   Hanabi (Co-op, Session E) winner handling is consistent with expected co-op logic.
    5.  **Verify Win Rate (📈) section**:
        *   All players have 5–6 sessions, exceeding the 3-game minimum, so rankings are displayed.
        *   `PlayerTwo`'s win rate is lower than the others and ranks last.
        *   Progress bar widths are proportional to win rate percentages.
    6.  **Verify Total Score Ranking section**:
        *   Section is visible with progress bars.
        *   `PlayerOne` cumulative score: `10 (Catan) + 72 (TM) = 82`.
        *   `PlayerTwo` cumulative score: `8 (Catan) + 15 (Splendor) + 50 (TM) = 73`.
        *   `PlayerThree` cumulative score: `5 (Catan) + 75 (TM) = 80`.
        *   `PlayerFour` cumulative score: `12 (Splendor) + 65 (TM) = 77`.
        *   `PlayerOne` ranks 1st. `PlayerThree` 2nd. `PlayerFour` 3rd. `PlayerTwo` 4th.
        *   **Note**: WIN_LOSE, COOPERATIVE, RANK_ONLY sessions do not contribute numeric scores.
        *   The longest bar belongs to the player with the highest total.
    7.  **Verify Popular Games (🎲) section**:
        *   All 7 games (including `House Rules Chess` and the RANK_ONLY session game) appear.
        *   Each shows `1x` play count (or `2x` for games used in both a scored session and Session G if same game was selected).
    8.  **Verify Win Rate minimum threshold**:
        *   Create a new group "Win Rate Test", invite only one other player.
        *   Record 2 sessions (below the 3-game minimum for win rate ranking).
        *   Navigate to its dashboard and verify the Win Rate section shows "Need 3+ games" (instead of rankings).

## 11. Session Detail Page

*   **Goal**: Verify that a recorded session can be opened and its details are correctly displayed.
*   **Prerequisites**: Session A (Catan, 3 players) from Step 6 is recorded in "Board Game Crew".
*   **Steps**:
    1.  Navigate to "Board Game Crew" group detail page (`/groups/:id`).
    2.  Verify the session list includes the Catan session.
    3.  Click on the Catan session entry.
    4.  Verify navigation to the session detail page (`/sessions/:id` or equivalent).
    5.  Verify the following details are displayed:
        *   Game name: `Catan`
        *   Played date/time is shown
        *   Player list with scores:
            *   `PlayerOne`: `10`
            *   `PlayerTwo`: `8`
            *   `PlayerThree`: `5`
        *   Rank order: PlayerOne 1st, PlayerTwo 2nd, PlayerThree 3rd
    6.  Verify a "← Back to Group" link is present and navigates back to the group detail page.
