FROM openjdk:17-jdk-slim
# se levanta el entorno y contruye lo necesario desde el ejecutable .jar
ENV MSSQL_SERVER_DBNAME=abadod_usuarios
ENV MSSQL_SERVER_USER=sa
ENV MSSQL_SERVER_PASSWORD=1364975-fR15
ARG USUARIO_APP_JAR_FILE=usuario-app/target/usuario-app-0.0.1-SNAPSHOT.jar
COPY ${USUARIO_APP_JAR_FILE} usuario-app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "usuario-app.jar"]
