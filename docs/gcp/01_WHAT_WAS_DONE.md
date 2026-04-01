# 01 — Registro completo de lo desplegado en GCP

**Proyecto:** `salud-backend` — Spring Boot 3.5 · Java 17 · PostgreSQL 15 · Flyway · JWT  
**Fecha de despliegue inicial:** 2026-03-31  
**Responsable:** jsalazarat  
**Branch desplegado:** `release/develop`

---

## Resumen ejecutivo

El backend del sistema de gestión clínica fue desplegado exitosamente en Google Cloud Platform utilizando una arquitectura serverless completamente gestionada:

- **Cloud Run** para la ejecución del contenedor (sin servidores que administrar)
- **Cloud SQL** (PostgreSQL 15) como base de datos gestionada
- **Artifact Registry** como registro privado de imágenes Docker
- **Secret Manager** para almacenar credenciales sensibles
- **IAM Service Account** con permisos mínimos (principio de menor privilegio)

---

## Recursos GCP creados

### Proyecto GCP

| Campo | Valor |
|---|---|
| Project ID | `pe-axelior-clinapp-dev` |
| Región principal | `us-central1` |

---

### APIs habilitadas

Las siguientes APIs fueron habilitadas en el proyecto:

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

| API | Uso |
|---|---|
| `run.googleapis.com` | Cloud Run (ejecución del contenedor) |
| `sqladmin.googleapis.com` | Cloud SQL (base de datos PostgreSQL) |
| `artifactregistry.googleapis.com` | Artifact Registry (registro Docker) |
| `secretmanager.googleapis.com` | Secret Manager (credenciales) |
| `cloudbuild.googleapis.com` | Cloud Build (CI/CD automático) |
| `cloudresourcemanager.googleapis.com` | Gestión de recursos IAM |

---

### Artifact Registry

| Campo | Valor |
|---|---|
| Nombre del repositorio | `clinica-backend` |
| Formato | Docker |
| Región | `us-central1` |
| URL base | `us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend` |

**Comando de creación:**
```bash
gcloud artifacts repositories create clinica-backend \
  --repository-format=docker \
  --location=us-central1 \
  --description="Imagenes Docker del backend Clinica Salud" \
  --project=pe-axelior-clinapp-dev
```

**Imagen desplegada:**
```
us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:v1.0.0
us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:latest
```

**Comandos de build y push:**
```bash
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"

# Autenticar Docker con Artifact Registry
gcloud auth configure-docker us-central1-docker.pkg.dev

# Build Maven primero
./mvnw clean package -DskipTests -B

# Build y push de la imagen
docker build -t ${IMAGE_URL}:v1.0.0 -t ${IMAGE_URL}:latest .
docker push ${IMAGE_URL}:v1.0.0
docker push ${IMAGE_URL}:latest
```

---

### Cloud SQL

| Campo | Valor |
|---|---|
| Nombre de la instancia | `clinica-postgres` |
| Versión de PostgreSQL | `POSTGRES_15` |
| Tier (tamaño) | `db-f1-micro` |
| Región | `us-central1` |
| Almacenamiento | SSD, 20 GB, autoincremento activado |
| Backups automáticos | Diarios a las 03:00 UTC |
| IP pública | Asignada (`--assign-ip`) — ver Obstáculos |
| Connection Name | `pe-axelior-clinapp-dev:us-central1:clinica-postgres` |
| Base de datos | `salud_db` |
| Usuario de aplicación | `salud_app` |

**Comando de creación de la instancia:**
```bash
gcloud sql instances create clinica-postgres \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --storage-type=SSD \
  --storage-size=20GB \
  --storage-auto-increase \
  --backup-start-time=03:00 \
  --assign-ip \
  --project=pe-axelior-clinapp-dev
```

> Nota: Se usó `--assign-ip` en lugar de `--no-assign-ip` por restricciones de la política organizacional. Ver `02_OBSTACLES.md` para detalles.

**Creación de base de datos y usuario:**
```bash
gcloud sql databases create salud_db \
  --instance=clinica-postgres \
  --project=pe-axelior-clinapp-dev

gcloud sql users create salud_app \
  --instance=clinica-postgres \
  --password="[GESTIONADO EN SECRET MANAGER]" \
  --project=pe-axelior-clinapp-dev
```

---

### Secret Manager

Dos secretos creados para almacenar credenciales de forma segura:

| Nombre del secreto | Contenido | Versión activa |
|---|---|---|
| `DB_PASSWORD` | Password del usuario `salud_app` en Cloud SQL | `latest` |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT (producción) | `latest` |

**Comandos de creación:**
```bash
# Password de base de datos
echo -n "PASSWORD_SEGURO" | \
  gcloud secrets create DB_PASSWORD \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# JWT Secret (generado con openssl rand -hex 32)
echo -n "JWT_SECRET_VALOR" | \
  gcloud secrets create JWT_SECRET \
    --data-file=- \
    --project=pe-axelior-clinapp-dev

# Verificar secretos creados
gcloud secrets list --project=pe-axelior-clinapp-dev
```

---

### IAM — Service Account

| Campo | Valor |
|---|---|
| Nombre | `clinica-backend-sa` |
| Email | `clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com` |
| Roles asignados | `roles/cloudsql.client`, `roles/secretmanager.secretAccessor` |

**Comandos de creación:**
```bash
gcloud iam service-accounts create clinica-backend-sa \
  --display-name="Clinica Backend Service Account" \
  --project=pe-axelior-clinapp-dev

export SA_EMAIL="clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com"

# Permiso para conectarse a Cloud SQL via socket
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/cloudsql.client"

# Permiso para leer secretos de Secret Manager
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"
```

---

### Cloud Run — Servicio

| Campo | Valor |
|---|---|
| Nombre del servicio | `salud-backend` |
| Región | `us-central1` |
| URL del servicio | `https://salud-backend-1073061395882.us-central1.run.app` |
| Puerto | `9090` |
| Memoria | `512Mi` |
| CPU | `1 vCPU` |
| Instancias mínimas | `1` (evita cold starts) |
| Instancias máximas | `5` |
| Concurrencia por instancia | `80` |
| Timeout de petición | `60s` |
| Cloud SQL conectado | `pe-axelior-clinapp-dev:us-central1:clinica-postgres` |
| Service Account | `clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com` |
| Acceso público | `--allow-unauthenticated` (autenticación via JWT de la aplicación) |

**Variables de entorno configuradas en el servicio:**

| Variable | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_NAME` | `salud_db` |
| `DB_USER` | `salud_app` |
| `CLOUD_SQL_INSTANCE` | `pe-axelior-clinapp-dev:us-central1:clinica-postgres` |
| `CORS_ALLOWED_ORIGINS` | URL del frontend configurada |
| `DB_PASSWORD` | Montado desde Secret Manager (`DB_PASSWORD:latest`) |
| `JWT_SECRET` | Montado desde Secret Manager (`JWT_SECRET:latest`) |

**Comando de despliegue completo:**
```bash
export SA_EMAIL="clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com"
export IMAGE_URL="us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend"
export CLOUD_SQL_INSTANCE="pe-axelior-clinapp-dev:us-central1:clinica-postgres"

gcloud run deploy salud-backend \
  --image=${IMAGE_URL}:v1.0.0 \
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

---

## Arquitectura de conexión en producción

```
Internet
    |
    v
Cloud Run (salud-backend)
    |  SPRING_PROFILES_ACTIVE=prod
    |  application-prod.properties
    |      |
    |      +-- DB_PASSWORD  ──> Secret Manager (DB_PASSWORD)
    |      +-- JWT_SECRET   ──> Secret Manager (JWT_SECRET)
    |
    +-- Unix Socket ──> Cloud SQL Auth Proxy (integrado en Cloud Run)
                              |
                              v
                        Cloud SQL (clinica-postgres)
                        PostgreSQL 15 · salud_db · salud_app
```

La conexión a Cloud SQL se realiza mediante **Unix Socket** a través del Cloud SQL Auth Proxy que Cloud Run inyecta automáticamente cuando se configura `--add-cloudsql-instances`. La URL JDBC en `application-prod.properties` es:

```
jdbc:postgresql:///salud_db?cloudSqlInstance=pe-axelior-clinapp-dev:us-central1:clinica-postgres&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

---

## Dependencia Maven agregada al proyecto

Para que la conexión via socket funcione, se agregó al `pom.xml`:

```xml
<!-- Cloud SQL Socket Factory para PostgreSQL -->
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>postgres-socket-factory</artifactId>
    <version>1.19.0</version>
</dependency>
```

---

## Estado actual del despliegue

| Componente | Estado |
|---|---|
| Artifact Registry | Operativo |
| Cloud SQL (clinica-postgres) | Operativo |
| Secret Manager (DB_PASSWORD, JWT_SECRET) | Operativo |
| Service Account (clinica-backend-sa) | Operativo |
| Cloud Run (salud-backend) | Operativo |
| Acceso publico (allUsers IAM) | **PENDIENTE** — ver abajo |

### Pendiente: Acceso publico bloqueado por politica organizacional

La politica organizacional `constraints/iam.allowedPolicyMemberTypes` impide asignar el rol `roles/run.invoker` al principal `allUsers`. Esta restriccion fue encontrada durante el despliegue.

**Accion requerida:** El administrador de la organizacion (`hola@axelior.ai`) debe ejecutar:

```bash
gcloud beta run services add-iam-policy-binding salud-backend \
  --region=us-central1 \
  --member=allUsers \
  --role=roles/run.invoker \
  --project=pe-axelior-clinapp-dev
```

Alternativamente, si la politica no puede modificarse, se puede usar autenticacion de GCP con tokens OIDC desde el frontend.

---

## Archivos del proyecto relacionados con el despliegue

| Archivo | Descripcion |
|---|---|
| `Dockerfile` | Multi-stage build: JDK 17 builder + JRE 17 runtime (Alpine) |
| `src/main/resources/application-prod.properties` | Configuracion de produccion (variables de entorno) |
| `scripts/gcp/01_setup_infra.sh` | Script para crear infraestructura GCP (ejecutar una vez) |
| `scripts/gcp/02_deploy.sh` | Script de build + push + deploy manual |
| `scripts/gcp/gcp.env.example` | Plantilla de variables de entorno (copiar como `gcp.env`) |
| `cloudbuild.yaml` | Configuracion de Cloud Build para CI/CD automatico |

---

*Documento generado el 2026-03-31 para el proyecto `pe-axelior-clinapp-dev`*
