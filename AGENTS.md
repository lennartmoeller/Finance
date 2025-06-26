# AGENTS Instructions

## Build
- **Backend**: Requires Java 21. Build with `./mvnw clean package` from the `backend` directory.
- **Frontend**: From `frontend`, run `npm ci` then `npm run build`.

## Run
- **Backend**: Run `./mvnw spring-boot:run` from `backend`. The IDE run configuration references `env/qs.env` for database credentials, but this file isn't required for local coding or testing.
- **Frontend**: Run `npm start` in `frontend`. This performs `npm install` before starting the dev server.
- **E2E Tests**: From `e2e-tests`, run `npm test`.

## Style Guidelines
### Backend (Java)
- Tabs are used for indentation with a continuation indent size of 4.

### Frontend (TypeScript/React)
- Semicolons are mandatory.
- Imports must be sorted alphabetically into the groups defined in `.eslintrc.json`.
- Prefer absolute import paths via alias `@` (`tsconfig.json` maps `@/*` to `src/*`).
