# DEPLOY_GCP.md — Guía de Despliegue en Google Cloud Platform

**Proyecto:** `salud-backend` (Spring Boot 3.5 · Java 17 · PostgreSQL · Flyway · JWT)  
**GCP Project ID:** `pe-axelior-clinapp-dev`  
**Región recomendada:** `us-central1`  
**Puerto de la aplicación:** `9090`

---

## Tabla de Contenidos

1. [Preparación del Ambiente](#1-preparación-del-ambiente)
2. [Seguridad — Credenciales antes de desplegar](#2-seguridad--credenciales-antes-de-desplegar)
3. [Build del Proyecto con Maven](#3-build-del-proyecto-con-maven)
4. [Dockerfile y Contenedor Docker](#4-dockerfile-y-contenedor-docker)
5. [Artifact Registry — Subida de imagen](#5-artifact-registry--subida-de-imagen)
6. [Cloud SQL — PostgreSQL gestionado](#6-cloud-sql--postgresql-gestionado)
7. [Secret Manager — Credenciales seguras](#7-secret-manager--credenciales-seguras)
8. [Cloud Run — Despliegue de la aplicación](#8-cloud-run--despliegue-de-la-aplicación)
9. [Variables de entorno y CORS](#9-variables-de-entorno-y-cors)
10. [Flyway en producción](#10-flyway-en-producción)
11. [Logs y Monitoreo](#11-logs-y-monitoreo)
12. [Health Checks y Alertas](#12-health-checks-y-alertas)
13. [Errores comunes y soluciones](#13-errores-comunes-y-soluciones)
14. [Checklist de Go-Live](#14-checklist-de-go-live)

---

## 1. Preparación del Ambiente

### 1.1 Herramientas requeridas

| Herramienta | Versión mínima | Verificación |
|---|---|---|
| Java JDK | 17 | `java -version` |
| Maven | 3.9+ (o mvnw) | `./mvnw -version` |
| Docker Desktop | 24+ | `docker --version` |
| gcloud CLI | latest | `gcloud version` |

#### Instalación de gcloud CLI (si no lo tienes)

```bash
# Windows — descargar el instalador desde:
# https://cloud.google.com/sdk/docs/install

# Verificar instalación
gcloud version
gcloud components update
```

### 1.2 Autenticación y configuración de GCP

```bash
# Login con tu cuenta Google
gcloud auth login

# Establece el proyecto activo
gcloud config set project pe-axelior-clinapp-dev

# Verifica configuración
gcloud config list

# Credenciales para Docker/SDK (Application Default Credentials)
gcloud auth application-default login
```

### 1.3 Habilitación de APIs necesarias

```bash
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com \
  cloudbuild.googleapis.com \
  cloudresourcemanager.googleapis.com \
  --project=pe-axelior-clinapp-dev
```

> Espera ~2 minutos hasta que todas las APIs estén activas.

### 1.4 Configuración de Docker con GCP

```bash
# Autenticar Docker con Artifact Registry (región us-central1)
gcloud auth configure-docker us-central1-docker.pkg.dev

# Verificar que funciona
docker info | grep -i "registry"
```

### 1.5 Validación local del backend

```bash
# Asegúrate de que levanta correctamente en local
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Verifica health check
curl http://localhost:9090/actuator/health

# Verifica Swagger
# Abre: http://localhost:9090/swagger-ui.html
```

---

## 2. Seguridad — Credenciales antes de desplegar

> **CRÍTICO:** El archivo `application.properties` contiene credenciales reales.
> **Nunca** debe subirse a Git ni incluirse en la imagen Docker.

### Credenciales detectadas que DEBEN moverse a Secret Manager:

- `spring.datasource.password` → actualmente en texto plano
- `app.jwt.secret` → clave JWT expuesta

### Verifica que .gitignore cubre estos archivos:

```bash
# Asegúrate de que application.properties está en .gitignore
grep "application.properties" .gitignore || echo "application.properties" >> .gitignore
```

### Crea application-prod.properties (template sin credenciales):

```properties
# Aplicacion
spring.application.name=salud-backend
server.port=9090

# Cloud SQL via Unix Socket (Cloud SQL Auth Proxy integrado en Cloud Run)
spring.datasource.url=jdbc:postgresql:///${DB_NAME}?cloudSqlInstance=${CLOUD_SQL_INSTANCE}&socketFactory=com.google.cloud.sql.postgres.SocketFactory
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=3600000
app.jwt.refresh-expiration=604800000

# Swagger — deshabilitar en producción si no es necesario
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false

# CORS
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}

# Logging — reducir en producción
logging.level.com.clinica=INFO
logging.level.org.springframework.security=WARN
```

---

## 3. Build del Proyecto con Maven

```bash
# Limpia artefactos previos y compila (omite tests para CI rápido)
./mvnw clean package -DskipTests -B

# Verifica que el JAR fue generado
ls -lh target/salud-backend-*.jar

# Resultado esperado:
# target/salud-backend-0.0.1-SNAPSHOT.jar  (~60-100 MB)
```

> El flag `-B` (batch mode) suprime el output interactivo, ideal para CI/CD.

---

## 4. Dockerfile y Contenedor Docker

El `Dockerfile` incluido en el proyecto usa **multi-stage build**:

- **Stage 1 (builder):** JDK 17 Alpine — compila el proyecto con Maven
- **Stage 2 (runtime):** JRE 17 Alpine — imagen mínima sin herramientas de compilación
- **Usuario no-root:** `appuser` por seguridad
- **JVM optimizada para contenedores:** `-XX:+UseContainerSupport`

### Construir la imagen localmente

```bash
docker build -t salud-backend:local .
```

### Probar el contenedor localmente

```bash
# Asume que tienes PostgreSQL corriendo en localhost:5432
docker run --rm \
  -p 9090:9090 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/salud_db" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="tu_password_local" \
  -e APP_JWT_SECRET="3cfa76ef14937c1c0ea519f8fc057a80fcd04a7e3d01d59c0dc8b9d1c5f0e3a1" \
  salud-backend:local

# Verifica que responde
curl http://localhost:9090/actuator/health
```

### Inspeccionar tamaño y capas

```bash
docker images salud-backend:local
docker history salud-backend:local
```

---

## 5. Artifact Registry — Subida de imagen

### 5.1 Crear repositorio en Artifact Registry

```bash
gcloud artifacts repositories create clinica-backend \
  --repository-format=docker \
  --location=us-central1 \
  --description="Imágenes Docker del backend Clínica Salud" \
  --project=pe-axelior-clinapp-dev
```

### 5.2 Definir la URL completa de la imagen

```bash
# Formato: REGION-docker.pkg.dev/PROJECT_ID/REPO_NAME/IMAGE_NAME:TAG
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"
export IMAGE_TAG="v1.0.0"  # Cambia por versión semántica real
```

### 5.3 Build y push a Artifact Registry

```bash
# Build con tag final
docker build -t ${IMAGE_URL}:${IMAGE_TAG} -t ${IMAGE_URL}:latest .

# Push de ambos tags
docker push ${IMAGE_URL}:${IMAGE_TAG}
docker push ${IMAGE_URL}:latest

# Verificar que la imagen está en el registry
gcloud artifacts docker images list \
  us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend
```

---

## 6. Cloud SQL — PostgreSQL gestionado

> **¿Por qué Cloud SQL?** PostgreSQL totalmente gestionado con backups automáticos, alta disponibilidad, y conexión segura vía Unix socket desde Cloud Run (sin IP pública expuesta).

### 6.1 Crear instancia Cloud SQL

```bash
gcloud sql instances create clinica-postgres \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --storage-type=SSD \
  --storage-size=20GB \
  --storage-auto-increase \
  --backup-start-time=03:00 \
  --enable-bin-log \
  --no-assign-ip \
  --project=pe-axelior-clinapp-dev

# ⏳ Esperar 5-10 minutos hasta que la instancia esté RUNNABLE
gcloud sql instances describe clinica-postgres --project=pe-axelior-clinapp-dev
```

> `--no-assign-ip`: Sin IP pública. Cloud Run se conecta via Cloud SQL Auth Proxy interno (Unix socket). Más seguro.

### 6.2 Crear base de datos y usuario

```bash
# Crear la base de datos
gcloud sql databases create salud_db \
  --instance=clinica-postgres \
  --project=pe-axelior-clinapp-dev

# Crear usuario de aplicación (NO usar el usuario postgres directamente)
gcloud sql users create salud_app \
  --instance=clinica-postgres \
  --password="GeneraUnaPasswordSegura2025!" \
  --project=pe-axelior-clinapp-dev

# Obtener el Connection Name (lo necesitas para Spring Boot)
gcloud sql instances describe clinica-postgres \
  --project=pe-axelior-clinapp-dev \
  --format="value(connectionName)"

# Resultado esperado:
# pe-axelior-clinapp-dev:us-central1:clinica-postgres
```

### 6.3 Dependencia Maven para Cloud SQL (agregar en pom.xml)

Para que Spring Boot se conecte via socket (sin proxy externo), agrega esta dependencia:

```xml
<!-- Cloud SQL Socket Factory para PostgreSQL -->
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>postgres-socket-factory</artifactId>
    <version>1.19.0</version>
</dependency>
```

---

## 7. Secret Manager — Credenciales seguras

### 7.1 Crear los secretos

```bash
# Password de la base de datos
echo -n "GeneraUnaPasswordSegura2025!" | \
  gcloud secrets create DB_PASSWORD \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# JWT Secret (genera uno nuevo y seguro para producción)
echo -n "$(openssl rand -hex 32)" | \
  gcloud secrets create JWT_SECRET \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# Verifica los secretos creados
gcloud secrets list --project=pe-axelior-clinapp-dev
```

### 7.2 Crear Service Account para Cloud Run

```bash
# Crear la cuenta de servicio
gcloud iam service-accounts create clinica-backend-sa \
  --display-name="Clinica Backend Service Account" \
  --project=pe-axelior-clinapp-dev

export SA_EMAIL="clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com"

# Permisos mínimos necesarios
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"
```

---

## 8. Cloud Run — Despliegue de la aplicación

> **¿Por qué Cloud Run?**
> - Serverless: paga solo por peticiones reales (ideal para clínicas con tráfico variable)
> - Escala automáticamente de 0 a N instancias
> - HTTPS automático con certificado TLS incluido
> - Integración nativa con Cloud SQL via Unix socket (sin VPN ni IP pública)
> - Despliegues sin downtime con traffic splitting

### 8.1 Primer despliegue

```bash
export SA_EMAIL="clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com"
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"
export CLOUD_SQL_INSTANCE="pe-axelior-clinapp-dev:us-central1:clinica-postgres"

gcloud run deploy salud-backend \
  --image=${IMAGE_URL}:latest \
  --platform=managed \
  --region=us-central1 \
  --port=9090 \
  --memory=512Mi \
  --cpu=1 \
  --min-instances=1 \
  --max-instances=5 \
  --concurrency=80 \
  --timeout=60s \
  --service-account=${SA_EMAIL} \
  --add-cloudsql-instances=${CLOUD_SQL_INSTANCE} \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars="DB_NAME=salud_db" \
  --set-env-vars="DB_USER=salud_app" \
  --set-env-vars="CLOUD_SQL_INSTANCE=${CLOUD_SQL_INSTANCE}" \
  --set-env-vars="CORS_ALLOWED_ORIGINS=https://tu-frontend.com" \
  --set-secrets="DB_PASSWORD=DB_PASSWORD:latest" \
  --set-secrets="JWT_SECRET=JWT_SECRET:latest" \
  --allow-unauthenticated \
  --project=pe-axelior-clinapp-dev
```

> `--allow-unauthenticated`: La autenticación la maneja el JWT de la aplicación, no IAM.
> `--min-instances=1`: Evita cold starts en la primera petición.

### 8.2 Obtener la URL del servicio

```bash
gcloud run services describe salud-backend \
  --platform=managed \
  --region=us-central1 \
  --format="value(status.url)" \
  --project=pe-axelior-clinapp-dev

# Ejemplo: https://salud-backend-xxxxxxxxxx-uc.a.run.app
```

### 8.3 Verificar el despliegue

```bash
export SERVICE_URL="https://salud-backend-xxxxxxxxxx-uc.a.run.app"

# Health check
curl ${SERVICE_URL}/actuator/health

# Respuesta esperada:
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

### 8.4 Actualizar imagen (redeploy)

```bash
# Rebuilar, taggear y pushear nueva versión
docker build -t ${IMAGE_URL}:v1.1.0 -t ${IMAGE_URL}:latest .
docker push ${IMAGE_URL}:v1.1.0
docker push ${IMAGE_URL}:latest

# Redesplegar con zero downtime
gcloud run deploy salud-backend \
  --image=${IMAGE_URL}:v1.1.0 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## 9. Variables de entorno y CORS

### Variables de entorno completas del servicio

```bash
# Ver variables actuales del servicio
gcloud run services describe salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev \
  --format="yaml(spec.template.spec.containers[0].env)"

# Actualizar una variable de entorno sin redesplegar imagen
gcloud run services update salud-backend \
  --set-env-vars="CORS_ALLOWED_ORIGINS=https://app.clinica.com,https://admin.clinica.com" \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Referencia completa de variables usadas

| Variable | Descripción | Fuente |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil Spring activo | Env var |
| `DB_NAME` | Nombre de la BD | Env var |
| `DB_USER` | Usuario de BD | Env var |
| `DB_PASSWORD` | Password de BD | Secret Manager |
| `CLOUD_SQL_INSTANCE` | Connection name Cloud SQL | Env var |
| `JWT_SECRET` | Clave secreta JWT | Secret Manager |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos CORS | Env var |

---

## 10. Flyway en producción

Flyway se ejecuta automáticamente al arrancar Spring Boot. Las migraciones V1–V12 se aplicarán en Cloud SQL la primera vez.

### Consideraciones importantes:

1. **Solo hacia adelante:** Nunca modificar archivos V1–V12 ya existentes
2. **Nuevas migraciones:** Crear `V13__descripcion.sql` en adelante
3. **Verificar antes de desplegar:**

```bash
# Conectarte a Cloud SQL via proxy para inspeccionar (solo desarrollo/staging)
cloud-sql-proxy pe-axelior-clinapp-dev:us-central1:clinica-postgres &

# Conectar psql
psql "host=127.0.0.1 port=5432 dbname=salud_db user=salud_app"

# Ver estado de migraciones Flyway
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

4. **Si una migración falla en producción:**

```bash
# Reparar checksums (solo si modificaste un script por error)
./mvnw flyway:repair \
  -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/salud_db" \
  -Dflyway.user=salud_app \
  -Dflyway.password="GeneraUnaPasswordSegura2025!"
```

---

## 11. Logs y Monitoreo

### Ver logs en tiempo real

```bash
# Tail de logs del servicio
gcloud logging read \
  "resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend" \
  --limit=50 \
  --format="value(timestamp, textPayload)" \
  --project=pe-axelior-clinapp-dev \
  --freshness=10m

# Logs de errores solamente
gcloud logging read \
  'resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend AND severity>=ERROR' \
  --limit=20 \
  --project=pe-axelior-clinapp-dev
```

### En Google Cloud Console

1. Ve a **Cloud Run** → `salud-backend` → pestaña **Logs**
2. Filtra por severidad: `ERROR`, `WARNING`
3. Busca por `textPayload` para filtrar por mensaje específico

### Métricas clave a monitorear

| Métrica | Umbral de alerta sugerido |
|---|---|
| Latencia P99 | > 3 segundos |
| Tasa de errores 5xx | > 1% |
| Instancias activas | Máximo alcanzado |
| Cloud SQL connections | > 80% del máximo |

---

## 12. Health Checks y Alertas

### Endpoints de Actuator disponibles

```bash
# Health general (incluye DB)
GET /actuator/health

# Info de la aplicación
GET /actuator/info

# Métricas
GET /actuator/metrics
```

### Configurar alerta de uptime en GCP

```bash
# Crear uptime check via gcloud
gcloud monitoring uptime-check-configs create clinica-backend-health \
  --resource-type=uptime-url \
  --hostname="salud-backend-xxxxxxxxxx-uc.a.run.app" \
  --path="/actuator/health" \
  --port=443 \
  --protocol=HTTPS \
  --check-interval=60s \
  --project=pe-axelior-clinapp-dev
```

### Cloud Run Health Check (en el servicio)

Cloud Run usa el health check del contenedor definido en el `Dockerfile`:
```
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3
```

---

## 13. Errores comunes y soluciones

### ❌ `Failed to obtain JDBC Connection`
**Causa:** Cloud Run no puede conectar con Cloud SQL.  
**Solución:**
```bash
# Verifica que el servicio tiene el Cloud SQL Instance asociado
gcloud run services describe salud-backend --region=us-central1 | grep cloudsql

# Verifica permisos de la SA
gcloud projects get-iam-policy pe-axelior-clinapp-dev \
  --flatten="bindings[].members" \
  --filter="bindings.members:clinica-backend-sa"
```

### ❌ `Flyway: Found non-empty schema without schema history table`
**Causa:** BD existente sin tabla `flyway_schema_history`.  
**Solución:** `spring.flyway.baseline-on-migrate=true` (ya configurado en `application-prod.properties`).

### ❌ `Cannot access secret DB_PASSWORD`
**Causa:** Service Account no tiene permiso `secretmanager.secretAccessor`.  
**Solución:**
```bash
gcloud secrets add-iam-policy-binding DB_PASSWORD \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor" \
  --project=pe-axelior-clinapp-dev
```

### ❌ `OOMKilled` / Pod killed
**Causa:** Límite de memoria 512Mi insuficiente.  
**Solución:**
```bash
gcloud run services update salud-backend \
  --memory=1Gi \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### ❌ Cold start lento (> 30s)
**Causa:** `--min-instances=0` (escala a cero).  
**Solución:**
```bash
gcloud run services update salud-backend \
  --min-instances=1 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### ❌ CORS error desde el frontend
**Causa:** Origen del frontend no configurado correctamente.  
**Solución:** Actualiza `CORS_ALLOWED_ORIGINS` con la URL exacta (sin trailing slash):
```bash
gcloud run services update salud-backend \
  --set-env-vars="CORS_ALLOWED_ORIGINS=https://app.clinica.com" \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### ❌ `docker: permission denied` en Windows
**Causa:** Docker Desktop no está corriendo o no tienes permisos.  
**Solución:** Abrir Docker Desktop como administrador y ejecutar en PowerShell elevado.

---

## 14. Checklist de Go-Live

### Antes del despliegue

- [ ] `application.properties` NO está en Git ni en la imagen Docker
- [ ] `.dockerignore` incluye `application.properties` y `application-dev.properties`
- [ ] JWT Secret de producción generado con `openssl rand -hex 32` (distinto al de dev)
- [ ] Password de Cloud SQL diferente al de desarrollo
- [ ] APIs de GCP habilitadas: Cloud Run, Cloud SQL, Artifact Registry, Secret Manager
- [ ] Service Account creada con permisos mínimos
- [ ] `spring.jpa.show-sql=false` en producción
- [ ] `springdoc.swagger-ui.enabled=false` en producción (o protegido con auth)
- [ ] `logging.level.com.clinica=INFO` (no DEBUG) en producción
- [ ] Flyway migraciones V1–V12 probadas en entorno de staging

### Durante el despliegue

- [ ] Build Maven exitoso (`./mvnw clean package -DskipTests`)
- [ ] Imagen Docker construida y probada localmente
- [ ] Imagen pusheada a Artifact Registry con tag de versión semántica
- [ ] Cloud SQL instance en estado `RUNNABLE`
- [ ] Secretos creados en Secret Manager
- [ ] Cloud Run deploy exitoso (revision en estado `ACTIVE`)

### Después del despliegue

- [ ] `GET /actuator/health` responde `{"status":"UP"}`
- [ ] `POST /api/auth/login` retorna token JWT válido
- [ ] Flyway migraciones aplicadas correctamente (verificar `flyway_schema_history`)
- [ ] Uptime check configurado en Cloud Monitoring
- [ ] Alertas de error 5xx configuradas
- [ ] URL del servicio compartida con el equipo de frontend
- [ ] CORS verificado desde el dominio del frontend

---

## Referencia rápida de comandos

```bash
# Ver estado del servicio
gcloud run services describe salud-backend --region=us-central1 --project=pe-axelior-clinapp-dev

# Ver revisiones desplegadas
gcloud run revisions list --service=salud-backend --region=us-central1 --project=pe-axelior-clinapp-dev

# Rollback a revisión anterior
gcloud run services update-traffic salud-backend \
  --to-revisions=salud-backend-00001-xxx=100 \
  --region=us-central1 --project=pe-axelior-clinapp-dev

# Eliminar revisiones antiguas
gcloud run revisions delete salud-backend-00001-xxx \
  --region=us-central1 --project=pe-axelior-clinapp-dev

# Ver logs de una revisión específica
gcloud logging read \
  'resource.labels.revision_name="salud-backend-00002-xxx"' \
  --project=pe-axelior-clinapp-dev --limit=100
```

---

*Generado para el proyecto `pe-axelior-clinapp-dev` · Spring Boot 3.5 · Cloud Run + Cloud SQL + Secret Manager*
