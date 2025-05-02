FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
ARG JAR_FILE=/app/target/*.jar

COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]