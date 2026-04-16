# Estado del Producto — Clínica Yoselin Backend

> Última actualización: 2026-04-15

---

## Resumen Ejecutivo

| Total módulos | Completados | Parciales | Pendientes |
|:---:|:---:|:---:|:---:|
| 18 | 11 | 4 | 3 |

---

## Tabla Unificada de Módulos

### Módulos Core (Backend clínico)

| Módulo | Estado | Descripción | Endpoints principales | Doc |
|--------|--------|-------------|----------------------|-----|
| **Auth & Seguridad** | ✅ Completo | Login JWT, roles, permisos, menú dinámico, auditoría | `POST /api/auth/login` `GET /api/auth/me` | [auth-security.md](modules/auth-security.md) |
| **Catálogo** | ✅ Completo | Sedes, especialidades, servicios, médicos, medicamentos, CIE-10, tarifas | `GET /api/catalog/doctors` `GET /api/catalog/specialties` | [catalog.md](modules/catalog.md) |
| **Pacientes** | ✅ Completo | Ficha clínica, historial, consentimientos | `POST /api/patients` `GET /api/patients/search` | [patients.md](modules/patients.md) |
| **Agenda** | ✅ Completo | Citas, disponibilidad, triaje, flujo PENDING→ATTENDED | `POST /api/appointments` `GET /api/appointments/availability` | [agenda.md](modules/agenda.md) |
| **Historia Clínica** | ✅ Completo | Notas de consulta, diagnóstico CIE-10, plan de tratamiento | `POST /api/clinical-notes` `GET /api/clinical-notes/patient/{id}` | — |
| **Exámenes** | ✅ Completo | Órdenes de laboratorio/imagen, resultados, firma digital | `POST /api/exams/orders` `PATCH /api/exams/orders/{id}/sign` | — |
| **Prescripciones** | ✅ Completo | Recetas médicas, dispensación farmacia, kardex | `POST /api/prescriptions` `POST /api/prescriptions/{id}/dispense` | [prescription.md](modules/prescription.md) |
| **Facturación** | ✅ Completo | Boletas/facturas, pagos múltiples, reembolsos, caja diaria | `POST /api/invoices` `POST /api/payments` | [billing.md](modules/billing.md) |
| **Inventario** | ✅ Completo | Stock, movimientos, lotes, proveedores, órdenes de compra | `GET /api/inventory` `POST /api/inventory/movements` | — |
| **Atención al Cliente** | ✅ Completo | Reclamos, escalamiento, encuestas de satisfacción | `GET /api/customer-service/complaints` `GET /api/customer-service/surveys` | [customer-service.md](modules/customer-service.md) |
| **Reportes** | ✅ Completo | Operacional, financiero, clínico, inventario | `POST /api/reports/operational` `POST /api/reports/financial` | [reports.md](modules/reports.md) |

---

### Módulos Parciales

| Módulo | Estado | Implementado | Pendiente | Doc |
|--------|--------|-------------|-----------|-----|
| **RRHH** | ⚠️ Parcial | Empleados, horarios laborales | Control de asistencia, métricas de productividad | [hrm.md](modules/hrm.md) |
| **Facturación Electrónica SUNAT** | ⚠️ Parcial | Facturación interna completa | XML UBL 2.1, firma digital, envío a PSE | [sunat-electronica.md](modules/sunat-electronica.md) *(pendiente)* |
| **Portal del Paciente** | ⚠️ Parcial | Tabla `patient_accounts` creada (V13) | Auth paciente, mis citas, mis resultados, recordatorios, pagos online | [patient-portal.md](modules/patient-portal.md) *(pendiente)* |
| **Integraciones** | ⚠️ Parcial | — | Email/SMTP, PDF (iText), WhatsApp (Twilio), pasarela de pagos Culqi | — |

---

### Módulos Pendientes

| Módulo | Estado | Descripción | Prioridad |
|--------|--------|-------------|-----------|
| **Recordatorios automáticos** | ❌ Pendiente | `@Scheduled` + Spring Mail: recordatorio 24h antes de cita, recordatorio de medicamentos | Alta |
| **API Pública** | ❌ Pendiente | Endpoints sin autenticación para búsqueda de médicos, especialidades y disponibilidad | Media |
| **Pagos online (Culqi)** | ❌ Pendiente | Integración pasarela de pagos para portal del paciente | Media |

---

## Infraestructura y Base de Datos

| Componente | Estado | Detalle |
|------------|--------|---------|
| PostgreSQL | ✅ | Local (puerto 5432) / Cloud SQL (producción GCP) |
| Flyway Migrations | ✅ | V1–V13 ejecutadas |
| JWT Auth | ✅ | JJWT 0.12.3, tokens de 1h + refresh 7d |
| Swagger UI | ✅ | `http://localhost:9090/swagger-ui.html` |
| CI/CD | ✅ | Cloud Run + Artifact Registry (GCP) |
| Email (SMTP) | ❌ | Pendiente: Spring Mail + cuenta SMTP |
| PDF | ❌ | Pendiente: iText 7 |
| WhatsApp | ❌ | Pendiente: Twilio SDK |

---

## Roadmap de Implementación

```
Fase 1 ✅  Atención al Cliente — GET endpoints + V13 migration
Fase 2 🔄  Portal del Paciente — auth, mis citas, mis resultados
Fase 3 🔄  RRHH — control de asistencia + productividad
Fase 4A 🔄 Email + recordatorios automáticos
Fase 4B 🔄 Generación de PDF (facturas, recetas, resultados)
Fase 5  ⏳ Facturación Electrónica SUNAT (XML + firma + PSE)
Fase 4C ⏳ WhatsApp vía Twilio
Fase 6  ⏳ API Pública externa
```

---

## Tabla de Permisos por Rol

| Módulo | ADMIN | MÉDICO | ENFERMERA | RECEPCIONISTA | CAJERO | FARMACÉUTICO | PACIENTE_PORTAL |
|--------|:-----:|:------:|:---------:|:-------------:|:------:|:------------:|:---------------:|
| Seguridad | ✅ | — | — | — | — | — | — |
| Catálogo (lectura) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — |
| Pacientes | ✅ | ✅ | ✅ | ✅ | lectura | — | lectura propia |
| Agenda | ✅ | parcial | parcial | ✅ | — | — | citas propias |
| Historia Clínica | ✅ | ✅ | lectura | — | — | — | — |
| Exámenes | ✅ | crear+firmar | resultados | — | — | — | lectura propia |
| Prescripciones | ✅ | crear | — | — | — | dispensar | lectura propia |
| Facturación | ✅ | — | — | — | ✅ | — | — |
| Inventario | ✅ | — | — | — | — | ✅ | — |
| Atención al Cliente | ✅ | — | — | ✅ | — | — | — |
| Reportes | ✅ | — | — | — | — | — | — |
| RRHH | ✅ | — | — | — | — | — | — |
