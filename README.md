# Finance

## Run the Project

### Prerequisites

- MariaDB database with credentials in hand
- IntelliJ IDEA installed (or do run configurations manually)
- Java 21 installed (only for development)
- Node.js 18 installed (only for development)
- Docker installed (only for production mode)

### Environment Variables

For development and production, you need to create the environment files `env/dev.env` and `env/prod.env` respectively. These files should contain the following properties:

```properties
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_NAME=finance
DATABASE_USER=root
DATABASE_PASS=root
```

### Development Mode

In Development mode you have two separate servers running: A frontend React server with Live Reload enabled and a backend Spring Boot server.

To run the project in development mode, follow these steps:

1. Install the frontend dependencies:
   ```sh
   npm install
   ```

2. Start the backend server with the <u>Dev Backend</u> run configuration in IntelliJ IDEA.

3. Start the frontend development server with the <u>Dev Frontend</u> run configuration in IntelliJ IDEA.

### Production Mode

To start the project in production mode, start the <u>Production</u> run configuration in IntelliJ IDEA or run the following command:

```sh
docker compose --env-file env/prod.env up
```

## Deploy to Azure

[Read here](docs/deployment.md) how to deploy this application to Azure.

## Dependency Version Checking

[Read here](docs/dependency-updates.md) how to update dependencies in both frontend and backend.
