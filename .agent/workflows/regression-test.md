---
description: How to run regression tests for Board Buddy
---

# Regression Test Workflow

// turbo-all
Follow these steps to perform a full regression test of the application. 
> [!IMPORTANT]
> **This workflow uses "Fail-Fast" logic.** If any step fails (especially environment setup or service startup), **STOP IMMEDIATELY**. Do not proceed to subsequent steps until the current failure is resolved.

## 1. Prerequisites
Ensure you have the necessary JDK (Java 25) and Node.js versions installed. (Note: External dependencies like DB/Redis should be accessible as per your `.env` configuration).

## 2. Start Backend
Run the backend API server directly using the local environment settings.
> [!TIP]
> Make sure your `backend/.env` file is correctly configured with `DB_*` and `REDIS_*` variables.

```bash
# From the backend directory
./gradlew :backend:boot:api-server:bootRun
```
**Verification**: Wait for "Started ApiServerApplication" in logs.
> [!WARNING]
> If the backend fails to start or crashes, **STOP THE TEST** and investigate the logs.

## 3. Start Frontend
Run the frontend development server directly in your terminal.
```bash
# From the frontend directory
npm run dev
```
**Verification**: Ensure the site is reachable at `http://localhost:3000` (or the port shown in terminal).

## 4. Execute Test Scenarios
Refer to [regression_testcase.md](file:///Users/jparangdev/Workspace/board-buddy/testcases/regression_testcase.md) and perform each step sequentially.

> [!CAUTION]
> If a scenario step fails, **STOP THE TEST**. Do not continue with further test cases as they may depend on the state created in previous steps.

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
2. **HALT all further testing**.
3. Investigate the cause (logs, browser console).
4. Fix the issue and restart the regression suite from Step 1.