# Programmatic Checks

After generating code changes and before completing any task, run the following checks to ensure code quality and correctness:

## Backend

```bash
cd backend
chmod +x ./mvnw

# check tests and aim for 100% test coverage
./mvnw test

# apply code formatting
./mvnw spotless:apply
```

## Frontend

```bash
cd frontend
npm ci

# check for TypeScript errors
npm run check

# lint and fix the codebase
npm run lint

# apply code formatting
npm run format
```
