## Run in Development Mode

### Prerequisites

- IntelliJ IDEA installed
- Java 21 installed
- Node.js 18 installed
- Docker installed
- [Environment variables](environment-variables.md) set up

### Instructions

In Development mode you have different things running separately:
- Frontend React server with Live Reload enabled
- Backend Spring Boot server
- MariaDB database in a Docker container

To run the project in development mode, follow these steps:

1. Install the frontend dependencies:
   ```sh
   npm install
   ```
2. Start the database with the <u>Database</u> run configuration in IntelliJ IDEA or run the following command:
   ```sh
   docker compose --env-file env/dev.env -f docker/docker-compose.db.yml up
   ```
3. Start the backend server with the <u>Dev Backend</u> run configuration in IntelliJ IDEA.
4. Start the frontend development server with the <u>Dev Frontend</u> run configuration in IntelliJ IDEA.
