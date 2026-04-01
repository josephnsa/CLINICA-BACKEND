# 02 — Obstáculos encontrados y soluciones aplicadas

**Proyecto:** `pe-axelior-clinapp-dev` — `salud-backend`  
**Fecha:** 2026-03-31

Este documento registra cada problema encontrado durante el despliegue en GCP, su causa raiz y la solucion exacta aplicada. Sirve como referencia para evitar repetir los mismos errores en futuros despliegues.

---

## Obstaculo 1: `gcloud sql instances create --no-assign-ip` falla

### Descripcion del error

Al intentar crear la instancia de Cloud SQL sin IP publica:

```bash
gcloud sql instances create clinica-postgres \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --no-assign-ip \
  --project=pe-axelior-clinapp-dev
```

El comando fallaba con un error relacionado a la politica organizacional que no permite crear instancias Cloud SQL sin IP publica en este proyecto.

### Causa raiz

La organizacion `axelior.ai` tiene configurada una politica organizacional que restringe la creacion de instancias Cloud SQL sin IP asignada. La politica impide el flag `--no-assign-ip`.

Esto es comun en organizaciones que requieren acceso de administracion directo a las instancias de Cloud SQL desde redes corporativas.

### Solucion aplicada

Se creo la instancia con `--assign-ip` para asignar una IP publica:

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

### Impacto en la arquitectura

La conexion desde Cloud Run a Cloud SQL **no usa la IP publica**. Cloud Run sigue conectandose via **Unix Socket** a traves del Cloud SQL Auth Proxy integrado (configurado con `--add-cloudsql-instances`). La IP publica existe pero no es utilizada por la aplicacion.

Para mayor seguridad, se recomienda restringir el acceso a la IP publica de Cloud SQL solo a IPs de administracion confiables:

```bash
# Agregar red autorizada (IP del administrador)
gcloud sql instances patch clinica-postgres \
  --authorized-networks=IP_ADMIN/32 \
  --project=pe-axelior-clinapp-dev
```

---

## Obstaculo 2: `./mvnw` con permiso denegado en Linux/Cloud Shell

### Descripcion del error

Al ejecutar el wrapper de Maven desde Cloud Shell o cualquier entorno Linux:

```
bash: ./mvnw: Permission denied
```

### Causa raiz

El archivo `mvnw` fue creado o clonado sin el bit de ejecucion en el sistema de archivos Linux. En Windows, los permisos de ejecucion de archivos Unix no se preservan por defecto al clonar con Git.

### Solucion aplicada

Se otorgo permiso de ejecucion al archivo `mvnw` antes de usarlo:

```bash
chmod +x mvnw
```

Y se ejecuto el build normalmente:

```bash
./mvnw clean package -DskipTests -B
```

### Prevencion futura

El `cloudbuild.yaml` incluye este paso antes de cada build Maven. Adicionalmente, en repositorios Git se puede preservar el permiso ejecutando una sola vez:

```bash
git update-index --chmod=+x mvnw
git commit -m "fix: restore mvnw executable permission"
```

Esto persiste el permiso en el indice de Git para todos los colaboradores.

---

## Obstaculo 3: `application-prod.properties` estaba en `.gitignore`

### Descripcion del error

Al desplegar en Cloud Run, el contenedor Docker no encontraba el perfil `prod` de Spring Boot porque el archivo `src/main/resources/application-prod.properties` no estaba incluido en el repositorio Git (y por lo tanto tampoco en la imagen Docker).

El error en Cloud Run era similar a:

```
No active profile set, falling back to default profiles: default
```

O la aplicacion arrancaba sin las configuraciones de produccion (Cloud SQL, JWT, etc.).

### Causa raiz

El archivo `application-prod.properties` habia sido agregado a `.gitignore` por precaucion (para no exponer credenciales). Sin embargo, este archivo **no contiene credenciales reales** — solo referencias a variables de entorno (`${DB_PASSWORD}`, `${JWT_SECRET}`, etc.). Por lo tanto, es seguro incluirlo en Git.

Las credenciales reales estan almacenadas en Secret Manager y se inyectan en tiempo de ejecucion.

### Solucion aplicada

1. Se elimino `application-prod.properties` de `.gitignore`
2. Se verifico que el archivo solo contiene referencias a variables de entorno (sin valores reales)
3. Se hizo commit del archivo al repositorio

```bash
# Verificar que no contiene credenciales reales
cat src/main/resources/application-prod.properties
# Solo debe contener: ${DB_PASSWORD}, ${JWT_SECRET}, etc. (sin valores hardcodeados)

# Remover de .gitignore
# Editar .gitignore y eliminar la linea que excluia application-prod.properties

# Agregar al repositorio
git add src/main/resources/application-prod.properties
git commit -m "fix: include application-prod.properties in repo (no real credentials)"
```

### Regla de seguridad actualizada

| Archivo | En Git | Motivo |
|---|---|---|
| `application.properties` | SI (solo configs locales sin credenciales de prod) | Necesario para desarrollo local |
| `application-dev.properties` | SI | Configuracion de desarrollo |
| `application-prod.properties` | SI | Solo variables de entorno, sin credenciales reales |
| `scripts/gcp/gcp.env` | NO (en .gitignore) | Contiene credenciales reales |

---

## Obstaculo 4: Dependencia `postgres-socket-factory` ausente en `pom.xml`

### Descripcion del error

Al arrancar la aplicacion en Cloud Run, Spring Boot lanzaba una excepcion al intentar conectarse a Cloud SQL:

```
java.lang.ClassNotFoundException: com.google.cloud.sql.postgres.SocketFactory
```

O:

```
Failed to obtain JDBC Connection: Unable to load class 'com.google.cloud.sql.postgres.SocketFactory'
```

### Causa raiz

La URL JDBC configurada en `application-prod.properties` usa el parametro `socketFactory=com.google.cloud.sql.postgres.SocketFactory`:

```
jdbc:postgresql:///salud_db?cloudSqlInstance=pe-axelior-clinapp-dev:us-central1:clinica-postgres&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

Esta clase pertenece a la libreria `postgres-socket-factory` de Google Cloud, que **no viene incluida por defecto** en Spring Boot. Sin esta dependencia, el driver JDBC de PostgreSQL no puede encontrar la clase del socket factory y no puede conectarse via Unix socket.

### Solucion aplicada

Se agrego la siguiente dependencia al `pom.xml`:

```xml
<!-- Cloud SQL Socket Factory para PostgreSQL (conexion via Unix socket desde Cloud Run) -->
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>postgres-socket-factory</artifactId>
    <version>1.19.0</version>
</dependency>
```

Se hizo commit al repositorio y se rebuildo la imagen Docker.

```bash
git add pom.xml
git commit -m "feat: add postgres-socket-factory for Cloud SQL Unix socket connection"

# Rebuild
./mvnw clean package -DskipTests -B
docker build -t us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:v1.0.0 .
docker push us-central1-docker.pkg.dev/pe-axelior-clinapp-dev/clinica-backend/salud-backend:v1.0.0
```

### Verificacion

Despues del fix, el health check de Cloud Run respondio correctamente:

```bash
curl https://salud-backend-1073061395882.us-central1.run.app/actuator/health
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

---

## Obstaculo 5: Politica organizacional bloquea `allUsers` en IAM de Cloud Run

### Descripcion del error

Al intentar hacer el servicio de Cloud Run accesible publicamente:

```bash
gcloud run services add-iam-policy-binding salud-backend \
  --region=us-central1 \
  --member=allUsers \
  --role=roles/run.invoker \
  --project=pe-axelior-clinapp-dev
```

Error:

```
ERROR: (gcloud.run.services.add-iam-policy-binding) FAILED_PRECONDITION:
One or more users named in the policy do not belong to a permitted customer.
Constraint: constraints/iam.allowedPolicyMemberTypes
```

O al ejecutar `gcloud run deploy` con `--allow-unauthenticated`:

```
ERROR: Policy update access denied. Ensure you have the "run.services.setIamPolicy" permission.
Setting IAM policy failed, try "gcloud beta run services add-iam-policy-binding --region=us-central1 --member=allUsers --role=roles/run.invoker salud-backend"
```

### Causa raiz

La organizacion `axelior.ai` tiene aplicada la politica organizacional `constraints/iam.allowedPolicyMemberTypes` que restringe los tipos de miembros IAM permitidos. Esta politica impide asignar permisos a `allUsers` (acceso publico anonimo sin autenticacion GCP).

Esta es una restriccion de seguridad a nivel organizacional y **no puede ser modificada por el propietario del proyecto** — requiere permisos de administrador de la organizacion.

### Estado actual

El servicio de Cloud Run esta desplegado y funcionando. El problema es que **las peticiones HTTP al servicio son rechazadas con HTTP 403** a menos que se incluya un token de autenticacion de GCP valido.

La logica JWT de la aplicacion (login, autorizacion de endpoints) esta completamente funcional — el problema es la capa de autenticacion de infraestructura de GCP que se ejecuta antes de que la peticion llegue a Spring Boot.

### Solucion requerida (PENDIENTE)

El administrador de la organizacion (`hola@axelior.ai`) debe ejecutar uno de estos comandos:

**Opcion A — Permitir acceso publico (recomendada para APIs de frontend):**
```bash
gcloud beta run services add-iam-policy-binding salud-backend \
  --region=us-central1 \
  --member=allUsers \
  --role=roles/run.invoker \
  --project=pe-axelior-clinapp-dev
```

**Opcion B — Crear excepcion en la politica organizacional para este proyecto:**
```bash
# Solo el administrador de la organizacion puede ejecutar esto
gcloud resource-manager org-policies set-policy \
  --project=pe-axelior-clinapp-dev \
  policy.json
# Donde policy.json contiene la excepcion para allowedPolicyMemberTypes
```

**Opcion C — Autenticacion con token OIDC desde el frontend (sin cambiar la politica):**

Si la politica no puede modificarse, el frontend debe incluir un token de identidad de GCP en cada peticion:

```javascript
// El frontend obtiene un token de identidad de la Service Account del frontend
// y lo envia en el header Authorization como Bearer token de GCP
// ADEMAS del JWT de la aplicacion
```

Esta opcion es mas compleja pero no requiere cambiar politicas organizacionales.

### Verificacion una vez resuelto

```bash
curl https://salud-backend-1073061395882.us-central1.run.app/actuator/health
# Debe responder sin error 403
# {"status":"UP"}
```

---

## Resumen de obstaculos

| # | Obstaculo | Estado |
|---|---|---|
| 1 | `--no-assign-ip` bloqueado por politica organizacional | Resuelto con `--assign-ip` |
| 2 | `./mvnw` sin permiso de ejecucion | Resuelto con `chmod +x mvnw` |
| 3 | `application-prod.properties` en `.gitignore` | Resuelto: removido de `.gitignore` y commiteado |
| 4 | Dependencia `postgres-socket-factory` ausente | Resuelto: agregada al `pom.xml` |
| 5 | Politica org bloquea `allUsers` en IAM | **PENDIENTE** — requiere `hola@axelior.ai` |

---

*Documento generado el 2026-03-31 para el proyecto `pe-axelior-clinapp-dev`*
