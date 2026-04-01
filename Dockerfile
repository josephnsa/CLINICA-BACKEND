# ─────────────────────────────────────────────────────────────────
# STAGE 1: Build
# ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copia descriptor de dependencias primero (cache de capas Maven)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descarga dependencias sin compilar el código fuente
RUN ./mvnw dependency:go-offline -B

# Copia el código fuente y compila
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ─────────────────────────────────────────────────────────────────
# STAGE 2: Runtime (imagen mínima)
# ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copia sólo el JAR compilado desde el stage anterior
COPY --from=builder /build/target/salud-backend-*.jar app.jar

# Ajuste de permisos
RUN chown -R appuser:appgroup /app
USER appuser

# Puerto expuesto (debe coincidir con server.port=9090)
EXPOSE 9090

# Health check para Cloud Run / Kubernetes
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:9090/actuator/health || exit 1

# JVM optimizada para contenedores
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
