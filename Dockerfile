FROM openjdk:17-jdk-slim
ARG USUARIO_APP_JAR_FILE=target/usuario-app-0.0.1-SNAPSHOT.jar
COPY ${USUARIO_APP_JAR_FILE} usuario-app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "usuario-app.jar"]
