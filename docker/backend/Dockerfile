# First build the application.

FROM maven:3.9.9 AS build

WORKDIR /app

COPY ./src/main/java ./src/main/java
COPY ./src/main/resources ./src/main/resources
COPY ./pom.xml .

RUN mvn clean package -Dmaven.test.skip=true

# Now run the application.

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
