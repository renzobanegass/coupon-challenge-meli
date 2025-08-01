# --- Stage 1: Build ---
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app
COPY --chown=gradle:gradle . .

RUN gradle bootJar --no-daemon


# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
