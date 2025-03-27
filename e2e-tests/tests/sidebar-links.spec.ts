import { test, expect } from '@playwright/test';

test('navigate to stats page', async ({ page }) => {
  await page.goto('http://localhost/');
  await page.getByRole('link', { name: 'Stats' }).click();
  await expect(page).toHaveURL('http://localhost/stats');
});
