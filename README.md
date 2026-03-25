# Clinica  — Backend

Backend del **Sistema de Gestión de Salud Integral** para Clínica . Construido con Spring Boot 3.5 siguiendo la **Arquitectura Hexagonal (Ports & Adapters)**, con autenticación JWT, migraciones Flyway y documentación OpenAPI automática.

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Módulos](#módulos)
- [Requisitos Previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Base de Datos y Migraciones](#base-de-datos-y-migraciones)
- [Seguridad y Autenticación](#seguridad-y-autenticación)
- [API y Documentación](#api-y-documentación)
- [Comandos Útiles](#comandos-útiles)

---

## Tecnologías

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Acceso a datos |
| PostgreSQL | 15+ | Base de datos relacional |
| Flyway | 11.x | Migraciones de esquema |
| JJWT | 0.12.3 | Generación y validación de tokens JWT |
| MapStruct | 1.5.5 | Mapeo entre entidades y DTOs (compile-time) |
| Lombok | latest | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.9 | Documentación Swagger automática |
| Maven | 3.9+ | Build y gestión de dependencias |

---

## Arquitectura

El proyecto sigue el patrón **Hexagonal (Ports & Adapters)**, organizando cada módulo de negocio de forma independiente:

```
src/main/java/com/clinica/salud/
├── modules/
│   └── [modulo]/
│       ├── domain/
│       │   ├── model/          ← Entidades de negocio con lógica propia
│       │   └── port/           ← Interfaces de repositorio (inversión de dependencias)
│       ├── application/
│       │   ├── dto/            ← Request/Response DTOs
│       │   └── usecase/        ← Casos de uso / lógica de aplicación
│       └── infrastructure/
│           ├── persistence/    ← Entidades JPA, repos Spring Data, mappers, adaptadores
│           ├── config/         ← Configuración específica del módulo
│           └── web/            ← Controladores REST con @PreAuthorize
└── shared/
    ├── exception/              ← Excepciones de dominio y handler global
    ├── response/               ← Envelope ApiResponse<T>
    └── security/               ← JWT filter, JwtService, SecurityConfig
```

**Regla clave:** El dominio nunca depende de la infraestructura. Los puertos (interfaces) del dominio son implementados por adaptadores de infraestructura.

**Respuesta estándar** de todos los endpoints:
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { ... },
  "timestamp": "2026-03-24T10:00:00"
}
```

---

## Módulos

| Módulo | Descripción |
|---|---|
| `auth` | Autenticación, login, registro de usuarios, menú dinámico |
| `security` | Gestión de roles, permisos y auditoría |
| `patients` | CRUD de pacientes y perfil clínico |
| `agenda` | Citas médicas, disponibilidad de doctores |
| `clinical` | Notas clínicas e historial médico |
| `exam` | Órdenes de exámenes y registro de resultados |
| `prescription` | Recetas médicas y dispensación |
| `billing` | Facturación, pagos y resumen de caja |
| `inventory` | Movimientos de inventario y alertas de stock bajo |
| `catalog` | Servicios médicos, especialidades, medicamentos, sedes, CIE-10 |
| `hrm` | Gestión de recursos humanos (doctores/personal) |
| `examinations` | Examinaciones médicas |
| `reports` | Reportes del sistema |

---

## Requisitos Previos

- **Java 17** (JDK)
- **Maven 3.9+** (o usar el wrapper `./mvnw` incluido)
- **PostgreSQL 15+** corriendo localmente

---

## Configuración

### 1. Base de datos

Crea la base de datos en PostgreSQL:

```sql
CREATE DATABASE salud_db;
```

### 2. Variables de entorno / application.properties

Crea el archivo `src/main/resources/application.properties` basándote en el siguiente template:

```properties
# Aplicacion
spring.application.name=salud-backend
server.port=9090

# Base de datos PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/salud_db
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD_AQUI
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# JWT (usa una clave secreta larga y segura)
app.jwt.secret=TU_JWT_SECRET_AQUI_MIN_32_CHARS
app.jwt.expiration=3600000
app.jwt.refresh-expiration=604800000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.url=/api-docs
springdoc.swagger-ui.enabled=true

# CORS (ajusta al origen de tu frontend)
app.cors.allowed-origins=http://localhost:4200

# Logging
logging.level.com.clinica=INFO
logging.level.org.springframework.security=WARN
```

> **Nunca subas `application.properties` con credenciales reales al repositorio.**

### 3. Perfil de desarrollo (opcional)

El perfil `dev` usa la base de datos `salud_db`. Para activarlo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Ejecución

```bash
# Compilar y empaquetar (omite tests)
./mvnw clean package -DskipTests

# Ejecutar la aplicación
./mvnw spring-boot:run

# Ejecutar con perfil dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La aplicación estará disponible en: `http://localhost:9090`

---

## Base de Datos y Migraciones

Flyway aplica automáticamente las migraciones al arrancar. Los scripts están en `src/main/resources/db/migration/`:

| Versión | Descripción |
|---|---|
| V1 | Tablas de seguridad (users, roles, permissions, menu_items) |
| V2 | Tablas de catálogo (services, specialties, medications, sedes) |
| V3 | Tablas de pacientes y perfil clínico |
| V4 | Tablas de agenda (appointments) |
| V5 | Tablas de facturación y pagos |
| V6 | Tablas de inventario |
| V7 | Seed de ítems del menú |
| V8 | Tablas de recetas/prescripciones |
| V9 | Tablas de órdenes de exámenes |

**Reglas importantes:**
- Nunca modificar archivos de migración ya ejecutados
- Para cambios de esquema, siempre crear un nuevo archivo `V10__descripcion.sql`
- Para resetear la BD en desarrollo: ejecutar `src/main/resources/db/scripts/reset_schema.sql`

```bash
# Ejecutar migraciones manualmente
./mvnw flyway:migrate

# Reparar checksums si se modificó una migración por error
./mvnw flyway:repair
```

---

## Seguridad y Autenticación

El sistema usa **JWT stateless** con control de acceso basado en permisos.

### Flujo de autenticación

```
POST /api/auth/login
  → Devuelve: { accessToken, refreshToken, user, permissions, menu }

Authorization: Bearer <accessToken>  (en todas las peticiones protegidas)
```

### Claims del token JWT

| Claim | Descripción |
|---|---|
| `sub` | Username del usuario |
| `role` | Rol asignado |
| `permissions` | Lista de permisos del rol |
| `sedeId` | Sede (sucursal) a la que pertenece |

### Control de acceso en controladores

```java
@PreAuthorize("hasAuthority('PATIENTS_READ')")
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<PatientResponse>> getPatient(...) { ... }
```

Los permisos son sembrados via Flyway (`V1__create_security_tables.sql`) y gestionados a través de las tablas `roles` y `permissions`.

### Usuario administrador por defecto

Al arrancar la aplicación por primera vez, `AuthBootstrap` siembra un usuario administrador con todos los permisos. Consulta `src/main/java/com/clinica/salud/modules/auth/infrastructure/config/AuthBootstrap.java` para los valores por defecto.

---

## API y Documentación

Una vez levantada la aplicación, la documentación interactiva está disponible en:

- **Swagger UI:** `http://localhost:9090/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9090/api-docs`

### Endpoints principales por módulo

| Módulo | Base Path |
|---|---|
| Auth | `/api/auth` |
| Usuarios | `/api/users` |
| Roles | `/api/roles` |
| Pacientes | `/api/patients` |
| Agenda | `/api/appointments` |
| Notas Clínicas | `/api/clinical-notes` |
| Exámenes | `/api/exam-orders` |
| Recetas | `/api/prescriptions` |
| Facturación | `/api/billing` |
| Inventario | `/api/inventory` |
| Servicios | `/api/services` |
| Especialidades | `/api/specialties` |
| Medicamentos | `/api/medications` |
| CIE-10 | `/api/cie10` |
| Sedes | `/api/sedes` |
| Auditoría | `/api/audit-logs` |

---

## Comandos Útiles

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar un test específico
./mvnw test -Dtest=AppointmentServiceTest

# Compilar y generar mappers MapStruct
./mvnw compile

# Limpiar build
./mvnw clean

# Ver árbol de dependencias
./mvnw dependency:tree
```

---

## Estructura del Proyecto

```
salud-backend/
├── src/
│   ├── main/
│   │   ├── java/com/clinica/salud/
│   │   │   ├── SaludBackendApplication.java
│   │   │   ├── modules/          ← Módulos de negocio
│   │   │   └── shared/           ← Código transversal
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── db/
│   │           ├── migration/    ← Scripts Flyway (V1-V9)
│   │           └── scripts/      ← Scripts utilitarios
│   └── test/
│       └── java/com/clinica/salud/
├── .gitignore
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## Licencia

Proyecto privado — Clínica . Todos los derechos reservados.
