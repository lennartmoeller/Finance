## Run in Production Mode

### Pre-requisites

- Docker installed
- [Environment variables](environment-variables.md) set up

To start the project in production mode, start the <u>Production</u> run configuration in IntelliJ IDEA or run the following command:

```sh
docker compose --env-file env/prod.env -f docker/docker-compose.yml -f docker/docker-compose.db.yml up
```
