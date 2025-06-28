# Agents Instructions for Finance Application

This Agents.md file provides comprehensive guidance for AI agents working with this codebase.

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
