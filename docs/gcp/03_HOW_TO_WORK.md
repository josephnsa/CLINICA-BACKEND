# 03 — Flujo de trabajo diario del desarrollador

**Proyecto:** `salud-backend` — `pe-axelior-clinapp-dev`  
**Servicio:** `https://salud-backend-1073061395882.us-central1.run.app`

Este documento describe el flujo de trabajo completo para el dia a dia: desarrollo local, despliegue manual, revision de logs y rollback de emergencia.

---

## Requisitos previos en la maquina local

Antes de trabajar, asegurate de tener instalado:

| Herramienta | Version minima | Como verificar |
|---|---|---|
| Java JDK | 17 | `java -version` |
| Maven | Incluido via `mvnw` | `./mvnw -version` |
| Docker Desktop | 24+ | `docker --version` |
| gcloud CLI | Ultima version | `gcloud version` |
| Git | Cualquier version reciente | `git --version` |

### Configuracion inicial de gcloud (una sola vez)

```bash
# Login con tu cuenta Google corporativa
gcloud auth login

# Establecer el proyecto activo
gcloud config set project pe-axelior-clinapp-dev

# Credenciales para Application Default (usado por algunas librerias GCP)
gcloud auth application-default login

# Autenticar Docker con Artifact Registry
gcloud auth configure-docker us-central1-docker.pkg.dev

# Verificar configuracion
gcloud config list
```

---

## Desarrollo local

### Arrancar la aplicacion en local

El perfil `dev` usa la base de datos PostgreSQL local definida en `application-dev.properties`:

```bash
# Opcion 1: Con Maven Wrapper (recomendado)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Opcion 2: Con el JAR compilado
./mvnw clean package -DskipTests
java -jar target/salud-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

La aplicacion queda disponible en: `http://localhost:9090`  
Swagger UI: `http://localhost:9090/swagger-ui.html`  
Health check: `http://localhost:9090/actuator/health`

### Variables de entorno para desarrollo local

El archivo `application-dev.properties` usa PostgreSQL local. Asegurate de tener PostgreSQL corriendo con:

- Host: `localhost:5432`
- Base de datos: `salud_db`
- Usuario: `postgres`
- Password: definido en `application-dev.properties`

### Ejecutar tests

```bash
# Todos los tests
./mvnw test

# Un test especifico
./mvnw test -Dtest=AppointmentServiceTest

# Tests con reporte de cobertura
./mvnw test jacoco:report
```

### Agregar una nueva migracion de base de datos

```bash
# Crear el archivo de migracion con el siguiente numero disponible
# Verificar cual es el ultimo: ls src/main/resources/db/migration/
touch src/main/resources/db/migration/V10__descripcion_del_cambio.sql

# Escribir el SQL de la migracion
# Flyway lo aplicara automaticamente al arrancar la app
```

Regla obligatoria: **nunca modificar archivos V1..V9 existentes**. Solo crear nuevos archivos `V10__...`, `V11__...`, etc.

---

## Flujo de trabajo para un cambio tipico

### 1. Crear rama de trabajo

```bash
# Asegurarse de estar actualizado
git checkout release/develop
git pull origin release/develop

# Crear rama para el cambio
git checkout -b feature/nombre-del-cambio
```

### 2. Desarrollar y verificar localmente

```bash
# Arrancar la app en local y verificar que funciona
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar tests
./mvnw test
```

### 3. Commit y push

```bash
git add .
git commit -m "feat(modulo): descripcion del cambio"
git push origin feature/nombre-del-cambio
```

### 4. Pull Request a `release/develop`

Crear el PR en GitHub hacia la rama `release/develop`. Si CI/CD automatico esta configurado (ver `04_CICD_AUTO_DEPLOY.md`), el merge a `release/develop` disparara el despliegue automatico.

### 5. Si el despliegue es manual, seguir el flujo de la siguiente seccion.

---

## Despliegue manual a Cloud Run

Usar el script `scripts/gcp/02_deploy.sh` para el flujo completo:

### Paso 1: Preparar variables de entorno

```bash
# Copiar la plantilla de variables (solo la primera vez)
cp scripts/gcp/gcp.env.example scripts/gcp/gcp.env

# Editar el archivo con los valores reales
# IMPORTANTE: gcp.env esta en .gitignore, nunca lo subas a Git
nano scripts/gcp/gcp.env
```

El archivo `gcp.env` debe tener estos valores para produccion:

```bash
PROJECT_ID="pe-axelior-clinapp-dev"
REGION="us-central1"
CLOUD_SQL_INSTANCE_NAME="clinica-postgres"
DB_NAME="salud_db"
DB_USER="salud_app"
AR_REPO="clinica-backend"
IMAGE_NAME="salud-backend"
IMAGE_TAG="v1.1.0"          # <-- Incrementar la version en cada deploy
SERVICE_NAME="salud-backend"
SA_NAME="clinica-backend-sa"
CORS_ALLOWED_ORIGINS="https://tu-frontend.com"
```

### Paso 2: Ejecutar el script de despliegue

```bash
# Dar permiso de ejecucion (solo la primera vez)
chmod +x scripts/gcp/02_deploy.sh

# Ejecutar despliegue completo
./scripts/gcp/02_deploy.sh
```

El script realiza automaticamente:
1. Build Maven (`./mvnw clean package -DskipTests -B`)
2. Crea el repositorio en Artifact Registry (si no existe)
3. Autentica Docker con GCP
4. Construye y sube la imagen Docker
5. Despliega en Cloud Run con todas las variables de entorno y secretos

### Paso 3: Verificar el despliegue

```bash
export SERVICE_URL="https://salud-backend-1073061395882.us-central1.run.app"

# Health check
curl ${SERVICE_URL}/actuator/health

# Respuesta esperada
# {"status":"UP","components":{"db":{"status":"UP"}}}

# Verificar version desplegada
curl ${SERVICE_URL}/actuator/info
```

### Despliegue manual paso a paso (sin script)

Si necesitas control granular sobre cada paso:

```bash
export PROJECT_ID="pe-axelior-clinapp-dev"
export REGION="us-central1"
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"
export IMAGE_TAG="v1.1.0"
export CLOUD_SQL_INSTANCE="pe-axelior-clinapp-dev:us-central1:clinica-postgres"
export SA_EMAIL="clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com"

# 1. Build Maven
./mvnw clean package -DskipTests -B

# 2. Build Docker
docker build -t ${IMAGE_URL}:${IMAGE_TAG} -t ${IMAGE_URL}:latest .

# 3. Push a Artifact Registry
docker push ${IMAGE_URL}:${IMAGE_TAG}
docker push ${IMAGE_URL}:latest

# 4. Deploy en Cloud Run
gcloud run deploy salud-backend \
  --image=${IMAGE_URL}:${IMAGE_TAG} \
  --platform=managed \
  --region=${REGION} \
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
  --project=${PROJECT_ID}
```

---

## Actualizacion de variables de entorno (sin rebuildar imagen)

Para cambiar solo una variable de entorno (ej. CORS) sin necesidad de un nuevo build:

```bash
gcloud run services update salud-backend \
  --set-env-vars="CORS_ALLOWED_ORIGINS=https://nuevo-frontend.com" \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

Esto despliega una nueva revision del servicio en segundos sin downtime.

---

## Actualizacion de secretos en Secret Manager

Si necesitas rotar el password de la base de datos o el JWT secret:

```bash
# Agregar nueva version del secreto DB_PASSWORD
echo -n "NuevoPasswordSeguro2026!" | \
  gcloud secrets versions add DB_PASSWORD \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# Agregar nueva version del secreto JWT_SECRET
echo -n "$(openssl rand -hex 32)" | \
  gcloud secrets versions add JWT_SECRET \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# El servicio usa siempre "latest", por lo tanto el nuevo secreto
# se cargara en el proximo restart o deploy del servicio Cloud Run.

# Forzar un nuevo deploy para que tome el secreto actualizado
gcloud run deploy salud-backend \
  --image=us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:latest \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Revision de logs

### Logs en tiempo real (streaming)

```bash
# Stream de logs del servicio (Ctrl+C para detener)
gcloud beta run services logs tail salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Logs recientes

```bash
# Ultimos 50 logs con timestamp
gcloud logging read \
  "resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend" \
  --limit=50 \
  --format="value(timestamp,textPayload)" \
  --project=pe-axelior-clinapp-dev \
  --freshness=1h

# Solo errores (severity ERROR o superior)
gcloud logging read \
  'resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend AND severity>=ERROR' \
  --limit=30 \
  --project=pe-axelior-clinapp-dev

# Logs de arranque de la aplicacion (los ultimos 10 minutos)
gcloud logging read \
  "resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend" \
  --limit=100 \
  --freshness=10m \
  --project=pe-axelior-clinapp-dev
```

### Logs en la consola web de GCP

1. Abrir: https://console.cloud.google.com/run
2. Seleccionar proyecto `pe-axelior-clinapp-dev`
3. Clic en el servicio `salud-backend`
4. Pestaña **Logs**
5. Filtrar por severidad, texto o rango de tiempo

### Buscar un error especifico

```bash
# Buscar por texto especifico en los logs
gcloud logging read \
  'resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend AND textPayload:"NullPointerException"' \
  --limit=20 \
  --project=pe-axelior-clinapp-dev

# Logs de una revision especifica
gcloud logging read \
  'resource.labels.revision_name="salud-backend-00003-abc"' \
  --project=pe-axelior-clinapp-dev \
  --limit=50
```

---

## Estado del servicio y revisiones

```bash
# Ver estado actual del servicio
gcloud run services describe salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Ver todas las revisiones desplegadas
gcloud run revisions list \
  --service=salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Ver distribucion de trafico entre revisiones
gcloud run services describe salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev \
  --format="yaml(status.traffic)"
```

---

## Rollback a una version anterior

### Rollback inmediato (redirigir trafico a revision anterior)

```bash
# 1. Listar revisiones disponibles para encontrar el nombre de la revision anterior
gcloud run revisions list \
  --service=salud-backend \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Ejemplo de salida:
# REVISION                    ACTIVE  SERVING  LAST DEPLOYED
# salud-backend-00003-abc     yes     100%     2026-03-31T10:00:00Z
# salud-backend-00002-xyz             0%       2026-03-30T15:00:00Z
# salud-backend-00001-pqr             0%       2026-03-29T09:00:00Z

# 2. Redirigir el 100% del trafico a la revision anterior
gcloud run services update-traffic salud-backend \
  --to-revisions=salud-backend-00002-xyz=100 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# 3. Verificar que el rollback fue exitoso
curl https://salud-backend-1073061395882.us-central1.run.app/actuator/health
```

### Rollback redesplegar una imagen anterior

```bash
# Si quieres redesplegar una imagen con tag especifico
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"

gcloud run deploy salud-backend \
  --image=${IMAGE_URL}:v1.0.0 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Despliegue canary (prueba con porcentaje del trafico)

```bash
# Enviar el 10% del trafico a la nueva revision y 90% a la estable
gcloud run services update-traffic salud-backend \
  --to-revisions=salud-backend-00003-abc=10,salud-backend-00002-xyz=90 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Si va bien, migrar el 100% a la nueva revision
gcloud run services update-traffic salud-backend \
  --to-latest \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Diagnostico rapido de problemas

### La app no responde (HTTP 502/503)

```bash
# 1. Ver estado del servicio
gcloud run services describe salud-backend \
  --region=us-central1 --project=pe-axelior-clinapp-dev \
  --format="value(status.conditions)"

# 2. Ver logs de la ultima revision
gcloud logging read \
  'resource.type=cloud_run_revision AND resource.labels.service_name=salud-backend AND severity>=ERROR' \
  --limit=20 --freshness=5m --project=pe-axelior-clinapp-dev
```

### La app arranca pero no conecta a la base de datos

```bash
# Verificar que el Cloud SQL instance esta asociado
gcloud run services describe salud-backend \
  --region=us-central1 --project=pe-axelior-clinapp-dev \
  --format="yaml(spec.template.metadata.annotations)"

# Verificar permisos del Service Account
gcloud projects get-iam-policy pe-axelior-clinapp-dev \
  --flatten="bindings[].members" \
  --filter="bindings.members:clinica-backend-sa" \
  --format="table(bindings.role)"

# Verificar que Cloud SQL esta RUNNABLE
gcloud sql instances describe clinica-postgres \
  --project=pe-axelior-clinapp-dev \
  --format="value(state)"
```

### Error de secretos

```bash
# Verificar que los secretos existen
gcloud secrets list --project=pe-axelior-clinapp-dev

# Verificar que la SA puede acceder al secreto
gcloud secrets get-iam-policy DB_PASSWORD --project=pe-axelior-clinapp-dev
```

### Out of memory

```bash
# Aumentar memoria del servicio
gcloud run services update salud-backend \
  --memory=1Gi \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Comandos de referencia rapida

```bash
# URL del servicio
echo "https://salud-backend-1073061395882.us-central1.run.app"

# Health check
curl https://salud-backend-1073061395882.us-central1.run.app/actuator/health

# Ver revision activa
gcloud run services describe salud-backend \
  --region=us-central1 --project=pe-axelior-clinapp-dev \
  --format="value(status.traffic)"

# Stream de logs
gcloud beta run services logs tail salud-backend \
  --region=us-central1 --project=pe-axelior-clinapp-dev

# Estado de Cloud SQL
gcloud sql instances describe clinica-postgres \
  --project=pe-axelior-clinapp-dev --format="value(state)"

# Listar imagenes en Artifact Registry
gcloud artifacts docker images list \
  us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend \
  --project=pe-axelior-clinapp-dev
```

---

*Documento generado el 2026-03-31 para el proyecto `pe-axelior-clinapp-dev`*
