import { test, expect } from '@playwright/test';

test.describe('1. User Account Lifecycle', () => {
  const testEmail = `temp_user_${Date.now()}@test.com`;
  const testPassword = 'TempPass1!';
  const testNickname = 'TempUser';

  test('Register, login and delete account', async ({ page }) => {
    // 1. Navigate to `/register`
    await page.goto('/register');

    // 2. Enter Email
    await page.getByLabel(/Email/i).fill(testEmail);
    // 3. Enter Nickname
    await page.getByLabel(/Nickname/i).fill(testNickname);
    // 4. Enter Password
    // Try byLabel first, but typical register forms have password fields
    const passwordFields = await page.getByLabel(/Password/i).all();
    if (passwordFields.length >= 2) {
      await passwordFields[0].fill(testPassword);
      await passwordFields[1].fill(testPassword);
    } else {
      // Fallback to placeholder if label differs
      await page.getByPlaceholder(/Password/i).first().fill(testPassword);
      await page.getByPlaceholder(/Confirm/i).fill(testPassword);
    }

    // 6. Click "Create Account"
    await page.getByRole('button', { name: /Create Account/i }).click();

    // Check if error is displayed (dump all text)
    await page.waitForTimeout(1000);
    const allText = await page.locator('p').allTextContents();
    console.error('All p tags:', allText);

    // 7. Verify redirection to `/login`
    await expect(page).toHaveURL(/\/login/);

    // 8. Login with new credentials
    await page.getByLabel(/Email/i).fill(testEmail);
    await page.getByLabel(/Password/i).first().fill(testPassword);
    
    // 9. Click "Login"
    await page.getByRole('button', { name: /Login/i }).click();

    // 10. Verify redirection to `/groups`
    await expect(page).toHaveURL(/\/groups/);

    // 11. Click "Delete Account" (in User Menu or Footer)
    // Wait for UI to settle
    await page.waitForLoadState('networkidle');
    // Open user menu
    const userMenu = page.getByRole('button', { name: new RegExp(testNickname, 'i') });
    await userMenu.click();
    
    // Accept the confirm dialog when it appears
    page.once('dialog', dialog => dialog.accept());

    // Click Delete Account
    await page.getByRole('button', { name: /Delete Account/i }).click();

    // 13. Verify redirection to `/login`
    await expect(page).toHaveURL(/\/login/);
  });

  test('1-1. Registration — Duplicate Email', async ({ page }) => {
    // Navigate to `/register`
    await page.goto('/register');
    // First, register a new valid user
    const dupeEmail = `dupe_${Date.now()}@test.com`;
    const pwd = 'ValidPass1!';
    
    await page.getByLabel(/Email/i).fill(dupeEmail);
    await page.getByLabel(/Nickname/i).fill('DupeUser');
    
    const passwordFields = await page.getByLabel(/Password/i).all();
    if (passwordFields.length >= 2) {
      await passwordFields[0].fill(pwd);
      await passwordFields[1].fill(pwd);
    } else {
      await page.getByPlaceholder(/Password/i).first().fill(pwd);
      await page.getByPlaceholder(/Confirm/i).fill(pwd);
    }
    
    await page.getByRole('button', { name: /Create Account/i }).click();
    await expect(page).toHaveURL(/\/login/);

    // Attempt to register again with same email
    await page.goto('/register');
    await page.getByLabel(/Email/i).fill(dupeEmail);
    await page.getByLabel(/Nickname/i).fill('AnotherUser');
    if (passwordFields.length >= 2) {
      await page.locator('input[type="password"]').nth(0).fill(pwd);
      await page.locator('input[type="password"]').nth(1).fill(pwd);
    } else {
      await page.getByPlaceholder(/Password/i).first().fill(pwd);
      await page.getByPlaceholder(/Confirm/i).fill(pwd);
    }
    await page.getByRole('button', { name: /Create Account/i }).click();

    // Verify error message is shown (it might be a specific error module or simple paragraph)
    const errorMsg = page.locator('p, span').filter({ hasText: /Error|Fail|use/i });
    await expect(errorMsg.first()).toBeVisible();
  });
});

test.describe('Core Flow (Login, Game, Group, Sessions)', () => {
  // Use serial mode because the core flow scenarios build on each other's state
  test.describe.configure({ mode: 'serial' });

  let page: any;
  const uniqueId = Date.now();
  const groupName = `Board Game Crew ${uniqueId}`;
  const emptyGroupName = `Empty Group Test ${uniqueId}`;

  test.beforeAll(async ({ browser }) => {
    page = await browser.newPage();
  });

  test.afterAll(async () => {
    await page.close();
  });

  test('3. Main User Login (User A)', async () => {
    await page.goto('/login');
    await page.getByLabel(/Email/i).fill('player1@test.com');
    await page.getByLabel(/Password/i).first().fill('Test1234!');
    await page.getByRole('button', { name: /Login/i }).click();
    await expect(page).toHaveURL(/\/groups/);
  });

  test('4. Game Creation', async () => {
    await page.goto('/games');
    // Check if Terraforming Mars already exists
    const gameExists = await page.getByText('Terraforming Mars').isVisible();
    
    if (!gameExists) {
      await page.getByRole('button', { name: /\+ Add Game|Add/i }).click();
      await page.getByLabel(/Game Name/i).fill('Terraforming Mars');
      await page.getByLabel(/Min/i).fill('1');
      await page.getByLabel(/Max/i).fill('5');
      // Assume default score strategy might be High Score Wins
      // The popup has "Add Game" button, and the page header might have "+ Add Game"
      await page.locator('form').getByRole('button', { name: /Add|Submit|Save/i }).click();
    }
    
    // Verify 5 games are visible (Catan, Splendor, Love Letter, Hanabi, Terraforming Mars)
    await expect(page.getByRole('heading', { name: 'Catan', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Splendor', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Love Letter', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Hanabi', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Terraforming Mars', exact: true })).toBeVisible();
  });

  test('5. Group Creation & Population', async () => {
    await page.goto('/groups');

    // Intercept the POST /groups request to log what memberIds are sent
    let createGroupRequestBody: any = null;
    await page.route('**/api/v1/groups', async (route) => {
      if (route.request().method() === 'POST') {
        createGroupRequestBody = JSON.parse(route.request().postData() || '{}');
        console.log('NETWORK: POST /groups body:', JSON.stringify(createGroupRequestBody));
      }
      await route.continue();
    });

    // Create the group
    await page.getByRole('button', { name: '+ Create Group', exact: true }).click();
    await page.getByLabel(/Group Name/i).fill(groupName);

    // The member search input uses placeholder 'e.g. Nickname#AB12' (from t('placeholder.userTag'))
    const searchInput = page.getByLabel(/Add Members/i);
    const searchVisible = await searchInput.isVisible();
    console.log('DEBUG: Search input visible:', searchVisible);
    await expect(searchInput).toBeVisible({ timeout: 5000 });

    const playersToAdd = [
      { nickname: 'PlayerTwo', searchTag: 'PlayerTwo#PLY2' },
      { nickname: 'PlayerThree', searchTag: 'PlayerThree#PLY3' },
      { nickname: 'PlayerFour', searchTag: 'PlayerFour#PLY4' }
    ];

    for (const p of playersToAdd) {
      // Backend requires the full Nickname#DISCRIMINATOR format to find users
      await searchInput.fill(p.searchTag);
      await page.waitForTimeout(600);
      // Use data-testid selector for reliability
      const userResult = page.locator('[data-testid="user-search-result"]').filter({ hasText: p.nickname });
      const resultCount = await userResult.count();
      console.log(`DEBUG: Results for ${p.searchTag}:`, resultCount);
      await expect(userResult.first()).toBeVisible({ timeout: 5000 });
      await userResult.first().click();
      // Verify chip appears with data-testid
      const chipsAfter = await page.locator('[data-testid="member-chip"]').count();
      console.log(`DEBUG: Chips after clicking ${p.nickname}:`, chipsAfter);
      await expect(page.locator('[data-testid="member-chip"]').filter({ hasText: p.nickname })).toBeVisible({ timeout: 3000 });
    }

    // Log chip count before submitting to diagnose state
    const chipsBefore = await page.locator('[data-testid="member-chip"]').count();
    console.log('DEBUG: Chip count before submit:', chipsBefore);
    await page.locator('form').getByRole('button', { name: 'Create Group', exact: true }).click();

    // Wait for modal to close and group to appear in list
    await expect(page.getByRole('heading', { name: groupName }).first()).toBeVisible({ timeout: 10000 });
    console.log('NETWORK: Create Group request captured:', JSON.stringify(createGroupRequestBody));
    
    // Enter group
    await page.getByRole('heading', { name: groupName }).first().click();
    await page.waitForURL(/\/groups\/\d+$/);
    await page.waitForLoadState('networkidle');

    // Verify members (Total 4: Owner + 3 added)
    await page.waitForTimeout(2000); // Give it extra time
    const h2Texts = await page.locator('h2').allTextContents();
    console.log('DEBUG: H2 Texts in Detail Page:', h2Texts);
    const memberItems = await page.locator('[class*="memberName"]').allTextContents();
    console.log('DEBUG: Members found:', memberItems);

    await expect(page.getByRole('heading', { name: /Members \(4\)/i })).toBeVisible().catch(async (e) => {
       console.error('Failed to find Members (4). Current headings:', h2Texts);
       throw e;
    });
    // Verify each member appears in the member list (using specific memberName class)
    const memberList = page.locator('[class*="memberName"]');
    await expect(memberList.filter({ hasText: 'PlayerOne' })).toBeVisible();
    await expect(memberList.filter({ hasText: 'PlayerTwo' })).toBeVisible();
    await expect(memberList.filter({ hasText: 'PlayerThree' })).toBeVisible();
    await expect(memberList.filter({ hasText: 'PlayerFour' })).toBeVisible();
  });

  test('6. Record Game Sessions', async () => {
    // Record Session A: Catan
    await page.goto('/groups');
    await page.getByRole('heading', { name: groupName }).first().click();
    await page.getByRole('link', { name: /Record Game/i }).click();
    
    // Step 1: Select Game
    await page.getByRole('heading', { name: 'Catan', exact: true }).click();
    await page.getByRole('button', { name: /Next/i }).click();
    
    // Step 2: Select Players
    await page.locator('input[type="checkbox"]').nth(0).check();
    await page.locator('input[type="checkbox"]').nth(1).check();
    await page.locator('input[type="checkbox"]').nth(2).check();
    await page.getByRole('button', { name: /Next/i }).click();
    
    // Step 3: Enter Scores (High scores win)
    await page.locator('input[type="number"]').nth(0).fill('10');
    await page.locator('input[type="number"]').nth(1).fill('8');
    await page.locator('input[type="number"]').nth(2).fill('5');
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 4: Confirm session
    await page.getByRole('button', { name: /Save Session/i }).click();

    // Verify redirect
    await expect(page).toHaveURL(/\/groups\/\d+$/);

    // Record Session B: Splendor (P2 vs P4)
    await page.getByRole('link', { name: /Record Game/i }).click();
    
    // Step 1: Select Game
    await page.getByRole('heading', { name: 'Splendor', exact: true }).click();
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 2: Select Players
    await page.locator('input[type="checkbox"]').nth(1).check();
    await page.locator('input[type="checkbox"]').nth(3).check();
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 3: Enter Scores
    await page.locator('input[type="number"]').nth(0).fill('15');
    await page.locator('input[type="number"]').nth(1).fill('12');
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 4: Confirm
    await page.getByRole('button', { name: /Save Session/i }).click();
    await expect(page).toHaveURL(/\/groups\/\d+$/);
    
    // Let's add Session E: Hanabi (Cooperative)
    await page.getByRole('link', { name: /Record Game/i }).click();
    
    // Step 1: Select Game
    await page.getByRole('heading', { name: 'Hanabi', exact: true }).click();
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 2: Select Players
    await page.locator('input[type="checkbox"]').nth(0).check();
    await page.locator('input[type="checkbox"]').nth(1).check();
    await page.locator('input[type="checkbox"]').nth(2).check();
    await page.locator('input[type="checkbox"]').nth(3).check();
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 3: Enter Scores / Win status
    // Co-op has a single team score or win/loss button
    const winBtn = page.getByRole('button', { name: /^Won/i });
    if (await winBtn.isVisible()) {
      await winBtn.click();
    } else {
      await page.locator('input[type="number"]').first().fill('25');
    }
    await page.getByRole('button', { name: /Next/i }).click();

    // Step 4: Confirm
    await page.getByRole('button', { name: /Save Session/i }).click();
    await expect(page).toHaveURL(/\/groups\/\d+$/);
  });

  test('8. Dashboard Navigation & 10. Statistics', async () => {
    // Currently inside Board Game Crew group page
    const viewStatsBtn = page.getByRole('link', { name: /View Stats|📊/i });
    if (await viewStatsBtn.isVisible()) {
      await viewStatsBtn.click();
      await expect(page).toHaveURL(/\/groups\/\d+\/dashboard/);
      
      // Basic verification of stats (10)
      await expect(page.getByText(/Total Sessions/i)).toBeVisible();
      await expect(page.getByText(/Most Active/i)).toBeVisible();

      const backBtn = page.getByRole('link', { name: /Back/i });
      if (await backBtn.isVisible()) {
        await backBtn.click();
        await expect(page).toHaveURL(/\/groups\/\d+$/);
      } else {
        await page.goBack();
      }
    }
  });

  test('9. Dashboard — Empty State', async () => {
    await page.goto('/groups');
    await page.getByRole('button', { name: '+ Create Group', exact: true }).click();
    await page.getByLabel(/Group Name/i).fill(emptyGroupName);
    await page.locator('form').getByRole('button', { name: 'Create Group', exact: true }).click();
    
      await page.getByRole('heading', { name: emptyGroupName }).first().click();
    await page.getByRole('link', { name: /View Stats|📊/i }).click();
    
    await expect(page.getByText(/No game sessions/i)).toBeVisible();
  });

  test('11. Game Filtering & Search', async () => {
    await page.goto('/games');
    const searchInput = page.locator('input[placeholder*="Search"]');
    await searchInput.fill('Terraforming');
    await expect(page.getByRole('heading', { name: 'Terraforming Mars', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Catan', exact: true })).not.toBeVisible();

    await searchInput.fill('InvalidGameNameXYZ');
    await expect(page.getByText(/No games found matching/i)).toBeVisible();
  });

  test('12. Session Detail (Session Management placeholder)', async () => {
    await page.goto('/groups');
    await page.getByRole('heading', { name: groupName }).first().click();
    
    const sessionLink = page.locator('a').filter({ hasText: /Catan/i }).first();
    if (await sessionLink.isVisible()) {
      await sessionLink.click();
      await expect(page).toHaveURL(/\/groups\/\d+\/sessions\/\d+/);
      await expect(page.getByRole('heading', { name: 'Catan', exact: true })).toBeVisible();
    }
  });

  test('13. Group Management (Delete Group)', async () => {
    await page.goto('/groups');
    await page.getByRole('heading', { name: emptyGroupName }).first().click();
    
    const deleteIconBtn = page.locator('button[title="Delete group"]');
    if (await deleteIconBtn.isVisible()) {
        await deleteIconBtn.click();
        const confirmDeleteBtn = page.locator('.btn-danger').filter({ hasText: /Delete/i }).first();
        await confirmDeleteBtn.click();
        await expect(page).toHaveURL(/\/groups/);
        await expect(page.getByRole('heading', { name: emptyGroupName })).not.toBeVisible();
    }
  });

  test('14. Logout & Auth Guarding', async () => {
    await page.goto('/games');
    const userMenuBtn = page.locator('header button').filter({ hasText: /PlayerOne/i }).first();
    await userMenuBtn.click();
    
    const logoutBtn = page.locator('button').filter({ hasText: /Logout/i }).first();
    await logoutBtn.click();
    
    await expect(page).toHaveURL(/\/login/);

    await page.goto('/groups');
    await expect(page).toHaveURL(/\/login/);
  });
});
