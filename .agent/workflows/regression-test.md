---
description: How to run regression tests for Board Buddy
---

# Regression Test Workflow

// turbo-all
Follow these steps to perform a full regression test of the application.

## 1. Prerequisites
Ensure you have Docker running (for DB/Redis) and the necessary JDK and Node.js versions installed.

## 2. Start Backend
Run the backend API server in the background or a separate terminal.
```bash
./gradlew :backend:boot:api-server:bootRun
```

## 3. Start Frontend
Run the frontend development server.
```bash
cd frontend && npm run dev
```

## 4. Execute Test Scenarios
Refer to [regression_test.md](file:///Users/jparangdev/Workspace/board-buddy/testcases/regression_test.md) and perform each step using the browser tool or manual verification.

### Scenarios to Cover:
1.  **User Account Lifecycle**: Create and delete a temporary account.
2.  **User Registration (User B)**: Register a secondary user for group testing.
3.  **Main User Login (User A)**: Log in as the primary tester.
4.  **Game Creation**: Register board games with different strategies.
5.  **Group Creation**: Create a group and invite members.
6.  **Record Game Session**: Log a play session and verify history.
7.  **Group Reordering**: Verify drag-and-drop persistence.
8.  **Enhanced Strategies**: Test Cooperative and Win/Lose game results.

## 5. Reporting
If any step fails:
1. Mark the failure in the test report.
2. Investigate the cause (logs, browser console).
3. Fix the issue and re-run the affected scenario.
