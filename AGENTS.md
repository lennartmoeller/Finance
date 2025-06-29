# Agents Instructions for Finance Application

This Agents.md file provides comprehensive guidance for AI agents working with this codebase.

## Project Structure

- **Backend (`backend/`)**  
  This is a Spring Boot project using Maven. The `pom.xml` defines dependencies such as Spring Boot 3.5.3, Lombok, MapStruct, MariaDB, H2 (for tests), and other plugins for formatting and code coverage.  
  Source code resides in `src/main/java/com/lennartmoeller/finance/`, organized into layers:
  - `controller/` – REST endpoints. Example: `AccountController` exposes CRUD routes for accounts.
  - `service/` – business logic. `TransactionService` fetches filtered transactions and performs persistence operations.
  - `repository/` – Spring Data repositories for JPA entities.
  - `dto/` and `mapper/` – Data transfer objects and MapStruct mappers.
  - `model/` – JPA entities representing accounts, transactions, categories, etc.

  Tests are under `src/test/java` and use JUnit and Mockito. Example tests verify controller behavior and model defaults. The `mvnw` wrapper is included to run Maven commands.

- **Frontend (`frontend/`)**  
  A React + TypeScript application built with Webpack. Key settings appear in `package.json` (scripts for development and build) and `webpack.config.js` (entry point `src/index.tsx`, TS loader, Babel config, dev server with proxy to the backend).

  The main entry point renders the React app with routing and global styling.  
  UI structure is organized into:
  - `components/` – reusable UI elements (buttons, forms, tables).
  - `skeleton/` – the overall layout; `routes.tsx` defines dashboard, transactions, and stats pages.
  - `views/` – feature pages (DashboardView, TrackingView, StatsView). For example, `TrackingView` loads data via custom hooks and shows account lists and transaction tables.
  - `services/` – wrappers around axios and React Query for API calls. Example: `useAccounts` fetches account data from `/api/accounts`.
  - `types/` – TypeScript models mirroring backend DTOs.
  Styling uses styled-components with a central `theme.ts`.

- **End-to-End Tests (`e2e-tests/`)**  
  Playwright tests verify browser behavior, such as page titles and navigation. Configuration resides in `playwright.config.ts`.

- **Other Files**
  - `Makefile` contains a task to enable git hooks.
  - `backend/src/main/resources/import.sql` seeds sample data on startup.

## Coding Guidelines

To ensure consistency and maintainability in the codebase, please adhere to the following guidelines:

- Follow common coding best practices
- Follow clean code principals
- Coding style should be as consistent as possible over the whole codebase

### Java

- Use @Nullable and @NotNull annotations to indicate nullable and non-nullable fields
- Use Lombok annotations to reduce boilerplate code:
  - Use @RequiredArgsConstructor instead of `@NoArgsConstructor` and `@AllArgsConstructor`
  - Don't use field injection like `@Autowired` and use constructor injection instead
- Use the Stream API in favor of traditional for-loops for better readability and performance
- Use Optionals to handle potential null values gracefully

### Java Tests

- Always write unit tests for new features and bug fixes
- Aim for 100% test coverage
- Make sure that the tests are testing functions completely and covering all edge cases

## Pull Request Guidelines

When an AI agent helps create a PR, please ensure it:

1. Follows the Conventional Commits specification (https://www.conventionalcommits.org)
2. Includes a clear description of the changes
3. References any related issues that the PR addresses
4. Ensures all tests pass for generated code
6. Keeps PRs focused on a single concern

## Programmatic Checks for OpenAI Codex

Before submitting generated changes, the following checks must pass before generated code can be merged:

### Backend

```bash
cd backend
chmod +x ./mvnw

# apply code formatting
./mvnw spotless:apply

# check tests and aim for 100% test coverage
./mvnw test
```
