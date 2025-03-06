FROM openjdk:21-slim
WORKDIR /app
COPY target/task-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/interns.csv /app/resources/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]