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

## 2. User Registration (User B)
*   **Goal**: Create a second user to be invited to groups.
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `player2@test.com`.
    3.  Enter Nickname: `PlayerTwo`.
    4.  Click "Start Testing".
    5.  Verify redirection to `/groups`.
    6.  *Action*: Logout (Click User Menu -> Logout, or clear LocalStorage).

## 2. Main User Login (User A)
*   **Goal**: Log in as the primary tester.
*   **Steps**:
    1.  Navigate to `http://localhost:3000/login`.
    2.  Enter Email: `player1@test.com`.
    3.  Enter Nickname: `PlayerOne`.
    4.  Click "Start Testing".
    5.  Verify redirection to `/groups`.

## 3. Game Creation
*   **Goal**: Register a board game to play.
*   **Steps**:
    1.  Click "Games" in the navigation bar (or navigate to `/games`).
    2.  Click "+ Add Game" (or "Add First Game").
    3.  Fill Form:
        *   Name: `Catan`
        *   Min Players: `3`
        *   Max Players: `4`
        *   Score Strategy: `High Score Wins`
    4.  Click "Add Game".
    5.  Verify `Catan` appears in the list.

## 4. Group Creation
*   **Goal**: Create a group and invite User B.
*   **Steps**:
    1.  Click "Groups" in the navigation bar (or navigate to `/groups`).
    2.  Click "+ Create Group".
    3.  Fill Form:
        *   Name: `Board Game Crew`
    4.  Search Members:
        *   Type `PlayerTwo`.
        *   Select `PlayerTwo` from results.
    5.  Click "Create Group".
    6.  Verify redirection to `/groups`.
    7.  Click on `Board Game Crew`.
    8.  Verify Member List contains `PlayerOne` (Owner) and `PlayerTwo`.

## 5. Record Game Session
*   **Goal**: Record a play session of Catan.
*   **Steps**:
    1.  In the Group Detail page, click "Record Game" (or `+ Record Game`).
    2.  **Step 1: Game**: Select `Catan`. Click "Next".
    3.  **Step 2: Players**: Select `PlayerOne` and `PlayerTwo`. Click "Next".
    4.  **Step 3: Scores**:
        *   PlayerOne: `10`
        *   PlayerTwo: `8`
        *   Played At: (Keep default "Now")
        *   Click "Next".
    5.  **Step 4: Confirm**: Review details. Click "Save Session".
    6.  Verify redirection to Group Detail page.
    7.  Verify the new session appears in the "Game Sessions" list.
