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
Run the Playwright end-to-end test suite to cover all regression scenarios. This will automatically open the Playwright UI so you can watch the tests execute.

```bash
# From the frontend directory
npm run test:e2e
```

**Verification**: When the Playwright UI opens, click the "Run all tests" (▶️) button to start. Ensure all Playwright tests pass successfully in the UI.

> [!CAUTION]
> If a test fails, **STOP THE TEST**. Investigate the Playwright report generated (usually in `playwright-report/`) or the terminal output.

## 5. AI Feedback Loop & Reporting
If any Playwright test fails, you can leverage the AI Assistant to automatically fix the issue:
1. **HALT all further testing**.
2. Provide the Playwright error output (from the terminal or report) to the AI Assistant.
3. Example prompt: *"Playwright test failed with this error: [error logs]. Please investigate and fix it."*
4. The AI will analyze the failures, access the relevant files, and modify the code (either the test.spec.ts script or the application code) to resolve the issue.
5. Once the AI reports the fix is applied, restart the regression test: `npm run test:e2e`.
6. If all tests pass, the regression test is successfully completed!