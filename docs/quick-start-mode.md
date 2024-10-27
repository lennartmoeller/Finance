## Quick Start Mode

### Prerequisites

- Docker installed

### Instructions

#### Step 1: Create an Environment File

Create an environment file `env/qs.env` with the following variables:

```properties
DATABASE_HOST=mariadb
DATABASE_PORT=3306
DATABASE_NAME=finance-qs
DATABASE_USER=user
DATABASE_PASS=password
```

These variables will connect you to the local database. You can also use an external database by changing the values.

#### Step 2: Start the Project

Start the <u>Quick Start</u> run configuration in IntelliJ IDEA or run the following command:

```sh
docker compose --env-file env/qs.env -f docker/docker-compose.qs.yml up
```

You can access the application at [http://localhost:80](http://localhost:80).
