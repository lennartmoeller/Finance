## Run in Development Mode

### Prerequisites

- IntelliJ IDEA installed
- Java 21 installed
- Node.js installed
- Docker installed

### Instructions

#### Step 1: Create an Environment File

Create an environment file `env/dev.env` with the following variables:

```properties
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_NAME=finance-dev
DATABASE_USER=user
DATABASE_PASS=password
```

These variables will connect you to the local database (Step 2). You can also use an external database by changing the values.

#### Step 2: Start the Local Database (optional)

To start the local database, run the <u>Database</u> run configuration in IntelliJ IDEA.

You can access phpMyAdmin at [http://localhost:8081](http://localhost:8081).

#### Step 3: Start the Backend Server

Start the backend server with the <u>Dev Backend</u> run configuration in IntelliJ IDEA.

You can access the backend at [http://localhost:8080](http://localhost:8080).

#### Step 4: Start the Frontend Development Server

Start the frontend development server with the <u>Dev Frontend</u> run configuration in IntelliJ IDEA.

You can access the frontend at [http://localhost:80](http://localhost:80).
