FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

VOLUME /tmp

# La ruta correcta al JAR
ARG JAR_FILE=applications/app-service/build/libs/*.jar
COPY ${JAR_FILE} auth.jar

USER appuser

ENTRYPOINT [ "sh", "-c", "java -jar auth.jar" ]