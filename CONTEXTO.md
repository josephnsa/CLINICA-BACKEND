# CONTEXTO DEL PROYECTO CLÍNICA SALUD

## ARQUITECTURA
- Java 17 + Spring Boot
- Arquitectura Hexagonal (domain/application/infrastructure)
- Base de datos: PostgreSQL
- Seguridad: JWT (ya implementado)

## MÓDULOS PRIORIZADOS
1. Appointments (Citas) - Core #1
2. Clinical Notes (Historia Clínica) - Core #2
3.  Patients (Pacientes) - Base
4.  Prescriptions (Recetas)
5.  Exams (Exámenes)
6.  Billing (Facturación)
7.  Catalogos (Specialties, Services, Medications)

## PATRONES A SEGUIR
- Domain Events para comunicación entre módulos
- Value Objects para tipos de negocio
- AuditService para logging (checklist #34-36)
- BusinessException para reglas de negocio

## ENDPOINTS EXISTENTES (ver imágenes)
- /api/appointments (POST)
- /api/clinical-notes (POST)
- /api/patients/{id}/history (GET)
- /api/prescriptions (GET, POST, /{id}/dispense)
- /api/exams/orders (GET, POST, /{id}/result)
- /api/catalog/* (CRUDs)

