# Stage 1: Build Java 21 Spring Boot Application
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper & pom from Backend folder
COPY Backend/.mvn/ .mvn
COPY Backend/mvnw Backend/pom.xml ./
RUN chmod +x mvnw

# Copy Backend source code and build package
COPY Backend/src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal Runtime Container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8085

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
