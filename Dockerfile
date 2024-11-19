FROM eclipse-temurin:17-jdk-focal

ARG JAR_FILE=/build/libs/gitandrun-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/app.jar"]



