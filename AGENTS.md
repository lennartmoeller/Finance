# Agents Instructions for Finance Application

This Agents.md file provides comprehensive guidance for AI agents working with this codebase.

## Project Structure

### Backend (`backend/`)

This is a Spring Boot project using Maven. Key settings appear in `pom.xml`. Source code resides in `src/main/java/com/lennartmoeller/finance/`, organized into layers:

- `FinanceApplication.java` – application entry point that bootstraps Spring.
- `controller/` – REST endpoints. Example: `AccountController` exposes CRUD routes for accounts.
- `service/` – business logic, e.g., transaction handling and CSV imports.
- `repository/` – Spring Data repositories for JPA entities.
- `dto/` and `mapper/` – Data transfer objects and MapStruct mappers.
- `model/` – JPA entities representing accounts, transactions, categories, etc.
- `converter/` – JPA attribute converters.
- `csv/` – bank statement parsers that import external CSV formats.
- `projection/` – Spring Data projections for aggregated read models.
- `util/` – shared helpers such as date ranges and smoothing utilities.

Tests are under `src/test/java` and use JUnit and Mockito. Shared fixtures live in `src/test/java/.../testbuilder/`, and additional resources sit in `src/test/resources`.

Configuration defaults are defined in `src/main/resources/application.properties`. The `mvnw` wrapper is included to run Maven commands.

### Frontend (`frontend/`)

A React + TypeScript application built with Webpack. Key settings appear in `package.json` and `webpack.config.cjs` alongside `tsconfig.json`.

UI structure is organized into:

- `index.tsx` – app entry point that renders the React app with routing and global styling.
- `components/` – reusable UI elements (buttons, forms, tables).
- `skeleton/` – layout shell and route definitions (see `routes.tsx`).
- `views/` – feature pages such as DashboardView, TrackingView, and StatsView.
- `services/` – API hooks built on axios and React Query.
- `types/` – TypeScript models mirroring backend DTOs.
- `config/` – React Query client and other runtime configuration.
- `hooks/` – shared React hooks such as DOM measurement helpers.
- `mapper/` – client-side mappers that reshape API payloads.
- `styles/` – global styles, theming, and scrollbar utilities for styled-components. The theme shape is typed via `styled.d.ts`, so changes to the theme should update that declaration file as well.
- `utils/` – date, money, and formatting helpers shared across the UI.

Styling uses styled-components with a central `theme.ts`.

Module imports can target the `@` alias configured in both `webpack.config.cjs` and `tsconfig.json`, which points to `frontend/src/`.

### Other Files

Deployment and runtime support lives in `docker-compose.yml`, `backend/Dockerfile`, `frontend/Dockerfile` and `frontend/nginx.conf`.

Static assets reside in `frontend/assets/`.

.git-hooks, .github, e2e-tests/*, .gitignore, Makefile can be ignored.

## Coding Guidelines

To ensure consistency and maintainability in the codebase, please adhere to the following guidelines:

- Follow common coding best practices.
- Follow clean code principals.
- Coding style and code architecture should be as consistent as possible over the whole codebase.
- Solutions should be code-efficient. Avoid unnecessary redundancy. Better refactor existing code instead of adding new code.

### Java

- Use @Nullable annotations to indicate nullable fields. Avoid @NonNull annotations.
- Use Lombok annotations to reduce boilerplate code:
  - Use `@RequiredArgsConstructor` instead of `@NoArgsConstructor` and `@AllArgsConstructor`
  - Don't use field injection like `@Autowired` and use constructor injection instead
- Use the Stream API in favor of traditional for-loops for better readability and performance
- Use Optionals to handle potential null values gracefully
- Do not unnecessarily use fully qualified class names, use import statements instead
- Do not use the var reserved type name, use explicit types instead

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

## Programmatic Checks

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

### Frontend

```bash
cd frontend

# lint and fix the codebase
npm run lint

# apply code formatting
npm run format
```
