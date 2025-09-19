FROM openjdk:21-jdk-slim AS build

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml for dependency resolution
COPY pom.xml ./

# Download dependencies (this layer is cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN addgroup --system --gid 1001 finance && \
    adduser --system --uid 1001 --ingroup finance finance

# Copy the built JAR from build stage with correct ownership
COPY --from=build --chown=finance:finance /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Switch to non-root user
USER finance

# Run the application with JVM optimizations for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]