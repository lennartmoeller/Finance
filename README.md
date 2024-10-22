# Finance

## Run the Project

### Prerequisites

- MariaDB database with credentials in hand
- IntelliJ IDEA installed (or do run configurations manually)
- Docker installed (only for production)
- Java 21 installed (only for development)
- Node.js 18 installed (only for development)

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

### Production Deployment

To start the project in production mode, start the <u>Production</u> run configuration in IntelliJ IDEA or run the following command:

```sh
docker-compose --env-file env/prod.env up
```

## Dependency Version Checking

### Maven

To ensure that your project's dependencies are up-to-date, use the `versions-maven-plugin`. This plugin helps you check for and update any outdated dependencies.

#### Checking for Updates

To check for updates to all your **dependencies**, run the following command:

```sh
./mvnw versions:display-dependency-updates
```

You can also check for updates to your Maven **plugins** by running the following command:

```sh
./mvnw versions:display-plugin-updates
```

To check for updates to the **parent versions** specified in your `pom.xml`, use the following command:

```sh
./mvnw versions:display-parent-updates
```

#### Automatic Updates
If you want the plugin to automatically update your `pom.xml`, you can use the following commands:

```sh
./mvnw versions:update-parent
./mvnw versions:update-properties
./mvnw versions:update-child-modules
```