FROM eclipse-temurin:21-jdk-alpine

# 1. Crear un grupo y un usuario no-root llamado 'appuser'
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

VOLUME /tmp

# 2. Copiar el JAR desde la ruta específica de construcción de Gradle
ARG JAR_FILE=applications/app-service/build/libs/*.jar
COPY ${JAR_FILE} auth.jar

ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Cambiar al usuario no-root que creamos
USER appuser

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar auth.jar" ]