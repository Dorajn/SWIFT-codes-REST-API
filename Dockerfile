FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
WORKDIR /app

COPY --from=builder /app/target/task-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/interns.csv /app/resources/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]