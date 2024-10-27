## Dependency Version Checking

### Frontend (npm)

To check for updates to all your dependencies, run the following command:

```sh
ncu
```

If you want the plugin to automatically update your `package.json`, you can use the following command:

```sh
ncu -u
```

### Backend (Maven)

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