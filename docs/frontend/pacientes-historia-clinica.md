# Frontend: Pacientes e Historia Clínica
> Guía para el equipo frontend. Indica qué modificar del template AdminMart,
> qué vistas solo necesitan conectarse al backend existente,
> y qué vistas nuevas aportan utilidad real al sistema.

---

## ① Qué eliminar del template AdminMart

Estas secciones del template son demostrativas y no tienen uso en el sistema clínico:

| Elemento AdminMart | Acción |
|--------------------|--------|
| "Templates", "Help", "Hire Us" en el header | **Eliminar** — reemplazar por notificaciones y perfil de usuario |
| "CHECKOUT PRO VERSION" / "Live Preview" | **Eliminar** |
| Sección "UI Components" del sidebar | **Eliminar** |
| Sección "Forms" del sidebar | **Eliminar** — los formularios van dentro de cada módulo |
| Sección "Charts" del sidebar | **Eliminar** — los gráficos van en el Dashboard |
| Datos estáticos: "John Doe", "#ML-3467", valores hardcodeados | **Eliminar** de todos los widgets |

---

## ② Qué modificar del template (componentes reutilizables)

Estos elementos del template se quedan pero con datos reales.

### Header
| Elemento actual (estático) | Reemplazar con |
|---------------------------|----------------|
| "CHECKOUT PRO VERSION" | Nombre de la sede activa (`/api/dashboard/me → sedeName`) |
| "Live Preview" button | Campana 🔔 con badge de notificaciones (`/api/dashboard/notifications`) |
| "Cerrar sesión" | Dropdown de perfil: avatar + nombre + rol + cerrar sesión |

### Sidebar — renombrar secciones

| Sección actual AdminMart | Sección en el sistema |
|--------------------------|----------------------|
| Dashboard | Dashboard |
| *(vacío)* | **PACIENTES E HISTORIA CLÍNICA** |
| *(vacío)* | **AGENDA Y CITAS** |
| *(vacío)* | **FACTURACIÓN** |
| *(vacío)* | **FARMACIA Y PRESCRIPCIONES** |
| *(vacío)* | **LABORATORIO Y EXÁMENES** |
| *(vacío)* | **INVENTARIO** |
| *(vacío)* | **RECURSOS HUMANOS** |
| *(vacío)* | **REPORTES** |
| *(vacío)* | **CONFIGURACIÓN** |

### Dashboard — widgets estáticos → datos reales
Ver `docs/modules/dashboard.md` para el mapeo completo de cada widget.

---

## ③ Vistas que solo necesitan conectarse (backend ya existe)

Solo hace falta construir la vista y llamar al endpoint documentado en el módulo correspondiente.

### `/pacientes` — Lista de Pacientes
- **Fuente:** `docs/modules/patients.md`
- **Endpoint:** `GET /api/patients?search=&page=&size=20`
- **Vista:** Tabla paginada. Columnas: Nombre, Documento, Teléfono, Última cita, Estado.
- **Sin cambios en backend.**

### `/pacientes/nuevo` — Crear Paciente
- **Fuente:** `docs/modules/patients.md`
- **Endpoint:** `POST /api/patients`
- **Vista:** Formulario con datos personales, documento, contacto, seguro médico.
- **Sin cambios en backend.**

### `/pacientes/{id}/recetas` — Prescripciones del paciente
- **Fuente:** `docs/modules/prescription.md`
- **Endpoints:** `GET /api/prescriptions?patientId=` / `GET /api/prescriptions/{id}/pdf`
- **Vista:** Cards por receta, badge de estado, botón PDF, botones dispensar/cancelar según permiso.
- **Sin cambios en backend.**

### `/pacientes/{id}/examenes` — Órdenes de Examen
- **Fuente:** `docs/modules/exam.md`
- **Endpoints:** `GET /api/exams/orders?patientId=` / `GET /api/exams/orders/{id}/pdf`
- **Vista:** Cards por orden, sub-tabla de ítems con estado, botón PDF, botón cargar resultado.
- **Sin cambios en backend.**

### `/pacientes/{id}/triage` — Signos Vitales
- **Fuente:** `docs/modules/agenda.md`
- **Endpoints:** `GET /api/triage/patient/{id}` / `POST /api/triage`
- **Vista:** Formulario de signos vitales + tabla historial + resaltado de valores críticos (ver reglas de color abajo).
- **Sin cambios en backend.**

### `/pacientes/{id}/kardex` — Kardex de Medicación
- **Fuente:** `docs/modules/prescription.md`
- **Endpoint:** `GET /api/patients/{id}/kardex`
- **Vista:** Tabla cronológica de todos los movimientos de medicamentos del paciente (dispensaciones, devoluciones, ajustes).
- **Sin cambios en backend.**

---

## ④ Vistas nuevas que aportan utilidad real

Estas vistas no existen en AdminMart ni en las pantallas actuales. Agregan valor operativo al sistema.

---

### A. Ficha Unificada del Paciente `/pacientes/{id}`

**Por qué aporta:** Actualmente los datos del paciente están dispersos en módulos separados. Una ficha central con tabs evita que el médico/recepcionista navegue entre pantallas.

**Layout:**
```
┌──────────────────────────────────────────────────────────┐
│  [Iniciales]  Nombre Completo                            │
│               DNI: xxxxxxxx | Edad: xx años | Sede       │
│               📞 xxxxxxxxx  | ✉ email@...  | 🟢 Activo   │
└──────────────────────────────────────────────────────────┘

[ Citas: 12 ] [ Recetas: 2 ] [ Exámenes pendientes: 1⚠ ] [ Deuda: S/120⚠ ]

[Historia] [Citas] [Recetas] [Exámenes] [Pagos] [Consentimientos]
```

**Endpoints usados (todos existentes):**
```
GET /api/patients/{id}
GET /api/appointments/patient/{id}
GET /api/prescriptions?patientId={id}
GET /api/exams/orders?patientId={id}
GET /api/patients/{id}/consents
```

---

### B. Timeline de Historia Clínica `/pacientes/{id}` — Tab "Historia"

**Por qué aporta:** Vista cronológica única que muestra todo lo ocurrido con el paciente sin tener que abrir cada módulo por separado.

**Formato:**
```
● 15 Abr 2026  📋 Nota clínica — Dr. Martínez (Medicina General)
               Diagnóstico: G43.9 Migraña | [Ver detalle]

● 10 Abr 2026  🧪 Resultado listo — Hemograma Completo ✅
               Valores normales | [Ver PDF]

● 02 Abr 2026  💊 Receta #RX-A1B2 — Ibuprofeno 400mg
               Estado: DISPENSED | [Ver receta]

● 28 Mar 2026  📅 Cita ATTENDED — Consulta General
               Sede Central | Dr. Martínez
```

**Endpoints usados (todos existentes):**
```
GET /api/clinical/notes?patientId={id}
GET /api/exams/orders?patientId={id}
GET /api/prescriptions?patientId={id}
GET /api/appointments/patient/{id}
```

**Nota:** Hacer merge de los 4 arrays en el frontend, ordenar por fecha descendente, renderizar con icono/color según tipo.

---

### C. Agenda del Día (vista del médico) `/agenda/hoy`

**Por qué aporta:** El médico al iniciar turno necesita ver sus citas del día en orden, con estado actual y acceso rápido a la ficha del paciente. No existe esta vista actualmente.

**Layout:**
```
Hoy — Martes 15 Abr 2026        Dr. Carlos Martínez — Medicina General

08:00  🟡 CONFIRMED   Ana García López    DNI 45678901   [Check-in] [Ver ficha]
08:30  🟢 CHECKED_IN  Juan Pérez Torres   DNI 78901234   [Iniciar consulta]
09:00  🔵 IN_PROGRESS María Soto Flores   DNI 12345678   [Completar]
09:30  ⬜ PENDING     Carlos Ruiz Díaz    DNI 99887766   [Check-in] [Ver ficha]
10:00  🔴 NO_SHOW     (vacío)             —
```

**Endpoints usados (todos existentes):**
```
GET /api/appointments?doctorId={id}&date={hoy}&sort=start_time
PATCH /api/appointments/{id}/checkin
PATCH /api/appointments/{id}/start-consultation
PATCH /api/appointments/{id}/complete
PATCH /api/appointments/{id}/no-show
```

---

### D. Buscador Global `/buscar?q=`

**Por qué aporta:** Acceso rápido desde cualquier pantalla. Evita navegar por el sidebar cuando se busca un paciente o cita específica.

**Comportamiento:**
- Barra en el header, activa con `Ctrl+K`
- Busca en tiempo real con debounce 300ms
- Resultados agrupados: Pacientes / Citas / Facturas

**Endpoints usados (todos existentes):**
```
GET /api/patients?search={q}&size=5
GET /api/appointments?search={q}&size=5     ← si el endpoint lo soporta
GET /api/invoices?search={q}&size=5         ← si el endpoint lo soporta
```

---

### E. Panel de Alertas del Sistema `/alertas`

**Por qué aporta:** Consolida en una sola vista todas las alertas operativas que hoy están dispersas o no visibles: stock bajo, citas sin confirmar, facturas vencidas, resultados sin entregar.

**Secciones:**
```
🔴 CRÍTICO
   • 3 productos con stock en cero → [Ver inventario]

🟡 ATENCIÓN
   • 5 facturas pendientes > 7 días → [Ver facturación]
   • 2 resultados de examen sin entregar → [Ver exámenes]

🔵 INFORMACIÓN
   • 8 citas de mañana sin confirmar → [Ver agenda]
   • 1 lote de medicamento vence en 10 días → [Ver lotes]
```

**Endpoints usados (todos existentes):**
```
GET /api/inventory/alerts?sedeId=
GET /api/inventory/batches/expiring?daysAhead=15&sedeId=
GET /api/dashboard/notifications?sedeId=
```

---

## ⑤ Reglas de UX transversales (aplicar en toda la sección)

### Avatar de paciente
Sin foto: círculo de color con iniciales. El color se deriva del UUID del paciente (hash → 8 colores fijos de la paleta del sistema). Nunca usar colores aleatorios.

### Badges de estado
| Entidad | Estado | Color |
|---------|--------|-------|
| Paciente | `ACTIVE` | Verde |
| Paciente | `INACTIVE` | Gris |
| Cita | `PENDING` | Gris |
| Cita | `CONFIRMED` | Azul |
| Cita | `CHECKED_IN` | Celeste |
| Cita | `IN_PROGRESS` | Naranja |
| Cita | `ATTENDED` | Verde |
| Cita | `CANCELLED` | Rojo |
| Cita | `NO_SHOW` | Rojo oscuro |
| Receta | `ACTIVE` | Verde |
| Receta | `DISPENSED` | Azul |
| Receta | `CANCELLED` | Rojo |
| Examen ítem | `PENDING` | Naranja |
| Examen ítem | `IN_PROGRESS` | Azul |
| Examen ítem | `COMPLETED` | Verde |

### Rangos de alerta — Signos Vitales (lógica frontend pura)
| Signo | Normal (verde) | Alerta (amarillo) | Crítico (rojo) |
|-------|---------------|-------------------|----------------|
| PA sistólica | 90–130 mmHg | 130–160 | > 160 |
| FC | 60–100 lpm | 100–120 | > 120 |
| Temperatura | 36–37.5 °C | 37.5–38.5 | > 38.5 |
| SpO2 | ≥ 95 % | 90–94 % | < 90 % |

### Descarga de PDF
Usar `<a href="/api/.../{id}/pdf" download>` con el JWT en el header `Authorization: Bearer {token}`. No usar `window.open`.

### Búsqueda con debounce
300ms antes de disparar llamadas a la API. Mínimo 2 caracteres para pacientes, 3 para CIE-10.

### Permisos — ocultar (no solo deshabilitar) botones
Leer `permissions[]` del JWT decodificado. Si el permiso no está, el botón no se renderiza.

### Selector CIE-10 en notas clínicas
`GET /api/catalog/cie10?q={texto}` — mostrar como `G43.9 — Migraña sin especificar`.

---

## Referencia de módulos backend por sección

| Vista | Módulo backend |
|-------|---------------|
| Lista / Ficha paciente | `docs/modules/patients.md` |
| Citas y agenda | `docs/modules/agenda.md` |
| Notas clínicas y triage | `docs/modules/agenda.md` |
| Prescripciones y kardex | `docs/modules/prescription.md` |
| Órdenes de examen | `docs/modules/exam.md` |
| Facturas y pagos | `docs/modules/billing.md` |
| Consentimientos | `docs/modules/patients.md` |
| Dashboard y notificaciones | `docs/modules/dashboard.md` |
