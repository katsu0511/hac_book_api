# ---- build stage ----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 1. Copy only the dependency files first
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. Dependency download (cached)
RUN ./gradlew dependencies --no-daemon

# 3. Copy the source code
COPY src src

# 4. Build
RUN ./gradlew clean bootJar --no-daemon

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
