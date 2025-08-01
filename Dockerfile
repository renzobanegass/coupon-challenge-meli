# --- Stage 1: Build ---
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

 # Copy only the files needed to download dependencies
COPY --chown=gradle:gradle gradlew gradlew.bat ./
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle build.gradle settings.gradle ./

 # Download dependencies to leverage Docker layer caching.
 # This layer is only rebuilt when build files change.
RUN ./gradlew dependencies --no-daemon

 # Copy the rest of the source code and build the application
COPY --chown=gradle:gradle src ./src
RUN ./gradlew bootJar --no-daemon

# --- Stage 2: Runtime ---

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Ensure we run as root to access /var/run/docker.sock for Testcontainers
USER root

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
