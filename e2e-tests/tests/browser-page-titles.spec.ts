import { test, expect } from '@playwright/test';

test('dashboard browser page title', async ({ page }) => {
  await page.goto('http://localhost:80');
  await expect(page.title()).resolves.toMatch('Finance | Dashboard');
});

test('transactions browser page title', async ({ page }) => {
  await page.goto('http://localhost:80/transactions');
  await expect(page.title()).resolves.toMatch('Finance | Transactions');
});

test('stats browser page title', async ({ page }) => {
  await page.goto('http://localhost:80/stats');
  await expect(page.title()).resolves.toMatch('Finance | Stats');
});
