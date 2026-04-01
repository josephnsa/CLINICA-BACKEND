# 04 — CI/CD: Despliegue automático con Cloud Build + GitHub

**Proyecto:** `pe-axelior-clinapp-dev`  
**Branch disparador:** `release/develop`  
**Comportamiento:** Cada push a `release/develop` dispara automaticamente un pipeline que construye, sube y despliega la nueva version en Cloud Run.

---

## Arquitectura del pipeline CI/CD

```
GitHub (push a release/develop)
        |
        v
Cloud Build Trigger
        |
        v
cloudbuild.yaml
        |
        +-- Paso 1: chmod +x mvnw
        +-- Paso 2: ./mvnw clean package -DskipTests
        +-- Paso 3: docker build
        +-- Paso 4: docker push a Artifact Registry
        +-- Paso 5: gcloud run deploy
        +-- Paso 6: curl /actuator/health (verificacion)
        |
        v
Cloud Run (salud-backend) — nueva revision activa
```

---

## Prerequisitos

Antes de configurar el trigger, asegurate de que:

1. **Cloud Build API esta habilitada:**
```bash
gcloud services enable cloudbuild.googleapis.com --project=pe-axelior-clinapp-dev
```

2. **El repositorio GitHub esta conectado a Cloud Build** (ver seccion 2).

3. **La Service Account de Cloud Build tiene los permisos necesarios** (ver seccion 3).

4. **El archivo `cloudbuild.yaml` existe en la raiz del proyecto** (esta incluido en el repositorio).

---

## Seccion 1: Habilitar APIs necesarias

```bash
gcloud services enable \
  cloudbuild.googleapis.com \
  run.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 2: Conectar GitHub a Cloud Build

### Opcion A — Via consola web de GCP (recomendada la primera vez)

1. Ir a: https://console.cloud.google.com/cloud-build/triggers
2. Seleccionar proyecto `pe-axelior-clinapp-dev`
3. Clic en **"Conectar repositorio"**
4. Seleccionar **GitHub (app de Cloud Build)**
5. Autorizar la instalacion de la GitHub App en tu cuenta/organizacion
6. Seleccionar el repositorio del backend
7. Clic en **"Conectar"**

### Opcion B — Via gcloud CLI

```bash
# Listar las conexiones disponibles (si ya conectaste antes)
gcloud builds connections list \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Si no hay conexion, crearla (requiere OAuth con GitHub)
# Este paso abrira una URL para autorizar en GitHub
gcloud builds connections create github clinica-github-connection \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 3: Permisos del Service Account de Cloud Build

Cloud Build usa la Service Account del proyecto: `[PROJECT_NUMBER]@cloudbuild.gserviceaccount.com`

### Obtener el numero de proyecto

```bash
export PROJECT_NUMBER=$(gcloud projects describe pe-axelior-clinapp-dev \
  --format="value(projectNumber)")
echo "Project Number: ${PROJECT_NUMBER}"
export CLOUDBUILD_SA="${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com"
echo "Cloud Build SA: ${CLOUDBUILD_SA}"
```

### Asignar permisos necesarios

```bash
export PROJECT_NUMBER=$(gcloud projects describe pe-axelior-clinapp-dev --format="value(projectNumber)")
export CLOUDBUILD_SA="${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com"

# Permiso para desplegar en Cloud Run
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/run.admin"

# Permiso para subir imagenes a Artifact Registry
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/artifactregistry.writer"

# Permiso para actuar como la Service Account del servicio Cloud Run
gcloud iam service-accounts add-iam-policy-binding \
  clinica-backend-sa@pe-axelior-clinapp-dev.iam.gserviceaccount.com \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/iam.serviceAccountUser" \
  --project=pe-axelior-clinapp-dev

# Permiso para leer secretos (si el cloudbuild.yaml los necesita durante el build)
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/secretmanager.secretAccessor"

# Permiso para escribir logs de build
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/logging.logWriter"

# Permiso para subir artefactos del build
gcloud projects add-iam-policy-binding pe-axelior-clinapp-dev \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/storage.objectAdmin"
```

### Verificar permisos asignados

```bash
gcloud projects get-iam-policy pe-axelior-clinapp-dev \
  --flatten="bindings[].members" \
  --filter="bindings.members:${CLOUDBUILD_SA}" \
  --format="table(bindings.role)"
```

---

## Seccion 4: Crear el trigger de Cloud Build

El trigger se conecta a la rama `release/develop` y usa el `cloudbuild.yaml` de la raiz del repositorio.

### Comando exacto para crear el trigger

```bash
gcloud builds triggers create github \
  --name="deploy-release-develop" \
  --description="Auto-deploy a Cloud Run en cada push a release/develop" \
  --region=us-central1 \
  --repo-name="NOMBRE_DEL_REPO_EN_GITHUB" \
  --repo-owner="USUARIO_O_ORG_DE_GITHUB" \
  --branch-pattern="^release/develop$" \
  --build-config="cloudbuild.yaml" \
  --substitutions="_IMAGE_TAG=latest,_REGION=us-central1,_SERVICE_NAME=salud-backend,_AR_REPO=clinica-backend,_CLOUD_SQL_INSTANCE=pe-axelior-clinapp-dev:us-central1:clinica-postgres,_DB_NAME=salud_db,_DB_USER=salud_app,_CORS_ALLOWED_ORIGINS=https://tu-frontend.com" \
  --project=pe-axelior-clinapp-dev
```

> Reemplaza `NOMBRE_DEL_REPO_EN_GITHUB` y `USUARIO_O_ORG_DE_GITHUB` con los valores reales del repositorio.

### Alternativa: crear trigger con archivo JSON de configuracion

Si prefieres una configuracion reproducible, crear el archivo `scripts/gcp/trigger-config.json`:

```json
{
  "name": "deploy-release-develop",
  "description": "Auto-deploy a Cloud Run en cada push a release/develop",
  "github": {
    "owner": "USUARIO_O_ORG_DE_GITHUB",
    "name": "NOMBRE_DEL_REPO_EN_GITHUB",
    "push": {
      "branch": "^release/develop$"
    }
  },
  "filename": "cloudbuild.yaml",
  "substitutions": {
    "_IMAGE_TAG": "latest",
    "_REGION": "us-central1",
    "_SERVICE_NAME": "salud-backend",
    "_AR_REPO": "clinica-backend",
    "_CLOUD_SQL_INSTANCE": "pe-axelior-clinapp-dev:us-central1:clinica-postgres",
    "_DB_NAME": "salud_db",
    "_DB_USER": "salud_app",
    "_CORS_ALLOWED_ORIGINS": "https://tu-frontend.com"
  }
}
```

Y luego crear el trigger:

```bash
gcloud builds triggers import \
  --source=scripts/gcp/trigger-config.json \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 5: Verificar el trigger creado

```bash
# Listar todos los triggers
gcloud builds triggers list \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Ver detalles del trigger especifico
gcloud builds triggers describe deploy-release-develop \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 6: Ejecutar el trigger manualmente (sin hacer push)

Util para probar la configuracion antes de hacer un push real:

```bash
gcloud builds triggers run deploy-release-develop \
  --region=us-central1 \
  --branch=release/develop \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 7: Monitorear las ejecuciones del pipeline

### Ver ejecuciones recientes

```bash
# Listar las ultimas 10 ejecuciones
gcloud builds list \
  --limit=10 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Ver ejecuciones de un trigger especifico
gcloud builds list \
  --filter="substitutions.TRIGGER_NAME=deploy-release-develop" \
  --limit=10 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Ver logs de una ejecucion especifica

```bash
# Obtener el BUILD_ID de la ejecucion
BUILD_ID="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

# Ver logs en tiempo real
gcloud builds log ${BUILD_ID} \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev

# Stream de logs
gcloud builds log ${BUILD_ID} \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev \
  --stream
```

### En la consola web

1. Ir a: https://console.cloud.google.com/cloud-build/builds
2. Seleccionar proyecto `pe-axelior-clinapp-dev`
3. Ver el historial de builds con estado (SUCCESS/FAILURE)
4. Clic en cualquier build para ver los logs paso a paso

---

## Seccion 8: Notificaciones de fallo del pipeline

### Configurar notificacion por email en caso de fallo

```bash
# Crear canal de notificacion por email
gcloud alpha monitoring channels create \
  --channel-labels="email_address=tu-email@ejemplo.com" \
  --type=email \
  --display-name="Email alertas CI/CD" \
  --project=pe-axelior-clinapp-dev

# Obtener el ID del canal creado
gcloud alpha monitoring channels list --project=pe-axelior-clinapp-dev
```

Alternativamente, en la consola web:
1. Cloud Build → Configuracion → Notificaciones
2. Agregar canal (email, Slack, Pub/Sub)

---

## Seccion 9: Variables de sustitucion del pipeline

El `cloudbuild.yaml` usa variables de sustitucion para ser flexible y reutilizable. Estas se configuran en el trigger y se pueden sobreescribir en cada ejecucion.

| Variable | Valor por defecto en el trigger | Descripcion |
|---|---|---|
| `_REGION` | `us-central1` | Region de GCP |
| `_SERVICE_NAME` | `salud-backend` | Nombre del servicio Cloud Run |
| `_AR_REPO` | `clinica-backend` | Nombre del repositorio Artifact Registry |
| `_IMAGE_TAG` | `latest` | Tag de la imagen Docker |
| `_CLOUD_SQL_INSTANCE` | `pe-axelior-clinapp-dev:us-central1:clinica-postgres` | Connection name de Cloud SQL |
| `_DB_NAME` | `salud_db` | Nombre de la base de datos |
| `_DB_USER` | `salud_app` | Usuario de la base de datos |
| `_CORS_ALLOWED_ORIGINS` | URL del frontend | Origenes CORS permitidos |

Variables automaticas de Cloud Build (disponibles sin configurar):

| Variable | Descripcion |
|---|---|
| `$COMMIT_SHA` | Hash del commit que disparo el build (usado como tag de imagen) |
| `$SHORT_SHA` | Primeros 7 caracteres del hash |
| `$BRANCH_NAME` | Nombre del branch (`release/develop`) |
| `$BUILD_ID` | ID unico del build actual |
| `$PROJECT_ID` | ID del proyecto GCP |

---

## Seccion 10: Estrategia de versionado de imagenes

El `cloudbuild.yaml` tagea la imagen Docker con dos tags:

1. `$COMMIT_SHA` — tag unico por commit (permite rollback preciso a cualquier commit)
2. `latest` — siempre apunta al ultimo build exitoso

Ejemplo de imagenes en Artifact Registry despues de varios deploys:

```
salud-backend:a1b2c3d4...   (commit SHA completo)
salud-backend:b5e6f7a8...   (commit SHA completo)
salud-backend:latest        (apunta al ultimo)
```

Para hacer rollback a un commit especifico:
```bash
gcloud run deploy salud-backend \
  --image=us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:a1b2c3d4 \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Seccion 11: Modificar o eliminar el trigger

### Actualizar una variable de sustitucion

```bash
# Por ejemplo, cambiar la URL de CORS
gcloud builds triggers update deploy-release-develop \
  --substitutions="_CORS_ALLOWED_ORIGINS=https://nuevo-frontend.com" \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Deshabilitar el trigger temporalmente

```bash
gcloud builds triggers disable deploy-release-develop \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Rehabilitar el trigger

```bash
gcloud builds triggers enable deploy-release-develop \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

### Eliminar el trigger

```bash
gcloud builds triggers delete deploy-release-develop \
  --region=us-central1 \
  --project=pe-axelior-clinapp-dev
```

---

## Consideraciones de seguridad del pipeline

1. **No incluir credenciales en `cloudbuild.yaml`** — los secretos se inyectan via `--set-secrets` en el paso de deploy, igual que en el despliegue manual.

2. **El `cloudbuild.yaml` esta en el repositorio** y es visible para todo el equipo. Solo contiene comandos y referencias a variables, nunca valores sensibles.

3. **Principio de menor privilegio** — la Service Account de Cloud Build solo tiene los permisos minimos necesarios (run.admin, artifactregistry.writer, iam.serviceAccountUser).

4. **Auditoria** — cada ejecucion de Cloud Build queda registrada en Cloud Audit Logs. Se puede auditar quien/que disparo cada deploy.

---

*Documento generado el 2026-03-31 para el proyecto `pe-axelior-clinapp-dev`*
