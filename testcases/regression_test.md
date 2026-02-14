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
76: 
77: ## 6. Group Reordering (Drag and Drop)
78: *   **Goal**: Change the display order of groups and verify it persists after refresh.
79: *   **Steps**:
80:     1.  Navigate to the Groups page (`/groups`).
81:     2.  Ensure at least two groups exist (e.g., "Board Game Crew" and another).
82:     3.  *Action*: Drag the second group to the top position.
83:     4.  Verify the order visually on the screen.
84:     5.  *Action*: Refresh the page (F5 or browser refresh).
85:     6.  Verify the same order is maintained after the page reloads.
86: 
87: ## 7. Enhanced Game Session Strategies
88: *   **Goal**: Record sessions using Cooperative and Win/Lose strategies.
89: *   **Steps (Cooperative)**:
90:     1.  In Group Detail, click "Record Game".
91:     2.  Select a cooperative game (e.g., `Pandemic`). Click "Next".
92:     3.  Select players. Click "Next".
93:     4.  **Step 3: Scores**:
94:         *   Observe the "Team Result" toggle.
95:         *   Toggle to "Won".
96:         *   Input scores for players.
97:         *   Click "Next" then "Save Session".
98:     2.  Verify the session appears in the group's history with the result.
99: *   **Steps (Win/Lose)**:
100:     1.  Click "Record Game".
101:     2.  Select a win/lose game (e.g., `Codenames`). Click "Next".
102:     3.  Select players. Click "Next".
103:     4.  **Step 3: Scores**:
104:         *   Observe "Won/Lost" toggles for each player.
105:         *   Set PlayerOne to "Won" and PlayerTwo to "Lost".
106:         *   Input scores.
107:         *   Click "Next" then "Save Session".
108:     2.  Verify the session result reflects who won or lost in the UI.
