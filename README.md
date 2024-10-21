# Finance

## Run the Application

### IntelliJ IDEA

This project comes with three run configurations for IntelliJ IDEA:

1. Frontend: Runs the React frontend for development purposes.
2. Backend Prod: Runs the Spring Boot backend in production mode.
3. Backend Test: Runs the Spring Boot backend in development mode.

Backend Prod and Backend Test need the environment files `env/prod.env` and `env/test.env` respectively. These files should contain the following properties:

```properties
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/finance
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
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