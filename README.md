# Finance

## Overview

Finance is a personal finance tracking application with a Spring Boot backend and a React + TypeScript frontend.

## Tech Stack

- Java 21 & Spring Boot
- Node 24, React, and TypeScript
- Maven and npm for build tooling
- Docker & Docker Compose for containerized runs

## Local Backend

Ensure Java 21 is available, then give the Maven wrapper execute permission before starting:

```bash
cd backend
chmod +x ./mvnw
./mvnw spring-boot:run
```

## Local Frontend

Ensure Node 24 is available, then run:

```bash
cd frontend
npm ci
npm run start
```

## Git Hooks

Install the shared Git hooks once per clone:

```bash
make init-git
```

## Docker

Set an environment file so the backend can read its configuration, then start the stack:

```bash
ENV_FILE="dev.env" docker compose up
```

Adjust `ENV_FILE` to point to the configuration you need.

## Updating Dependencies

Update backend dependencies with the Maven Versions Plugin tools:

```bash
cd backend
chmod +x ./mvnw
./mvnw versions:update-parent
./mvnw versions:update-properties
./mvnw versions:update-child-modules
```

Update frontend dependencies via the existing npm script:

```bash
cd frontend
npm run update
```
