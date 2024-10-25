## Environment Variables

For development and production, you need to create the environment files `env/dev.env` and `env/prod.env` respectively. These files should contain the following properties:

```properties
DATABASE_USER=root
DATABASE_PASS=root
```

If you are using an external database, define the following properties:

```properties
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_NAME=finance
DATABASE_USER=root
DATABASE_PASS=root
```