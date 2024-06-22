FROM becarrillo/abadod-mssql:tagname
RUN (/opt/mssql/bin/sqlservr --accept-eula & ) \
    | grep -q "Service Broker manager has started" && /opt/mssql-tools/bin/sqlcmd \
    && /opt/mssql-tools/bin/sqlcmd - S127.0.0.1 -Usa -P1364975-fR15 \


FROM openjdk:17-jdk-slim
ARG REGISTRY_SERVICE_JAR_FILE=registry-service/registry-service.0.0.1.jar
COPY ${REGISTRY_SERVICE_JAR_FILE} registry-service-jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "registry-service.jar"]


FROM openjdk:17-jdk-slim
ARG CONFIG_SERVER_JAR_FILE=config-server/config-server.0.0.1.jar
COPY ${CONFIG_SERVER_JAR_FILE} config-server.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "config-server.jar"]


FROM openjdk:17-jdk-slim
ARG USUARIO_APP_JAR_FILE=usuario-app/target/usuario-app.0.0.1.jar
COPY ${USUARIO_APP_JAR_FILE} usuario-app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "usuario-app.jar"]


FROM openjdk:17-jdk-slim
ARG SERVICIO_APP_JAR_FILE=servicio-app/target/servicio-app.0.0.1.jar
COPY ${SERVICIO_APP_JAR_FILE} servicio-app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "servicio-app.jar"]


FROM openjdk:17-jdk-slim
ARG AGENDAMIENTO_APP_JAR_FILE=agendamiento-app/target/agendamiento-app.0.0.1.jar
COPY ${AGENDAMIENTO_APP_JAR_FILE} agendamiento-app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "agendamiento-app.jar"]


FROM openjdk:17-jdk-slim
ARG COMPRAS_APP_JAR_FILE=compras-app/target/compras-app.0.0.1
COPY ${COMPRAS_APP_JAR_FILE} compras-app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "compras-app.jar"]



