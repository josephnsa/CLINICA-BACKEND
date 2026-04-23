# LB + IAP para `salud-backend-bff`

Runbook para exponer una entrada web segura (sin `allUsers` en Cloud Run), usando:

- HTTPS Load Balancer externo
- Serverless NEG -> `salud-backend-bff`
- IAP para restringir acceso (dominio/grupo corporativo)

## Requisitos

- Permisos en GCP para:
  - Compute Load Balancing (`compute.*`)
  - IAP (`iap.*`)
  - IAM bindings de proyecto (`resourcemanager.projects.setIamPolicy`)
  - Leer/actualizar Cloud Run
- Dominio/DNS (si no hay, se puede crear infraestructura y cerrar DNS después).
- Servicio BFF ya desplegado: `salud-backend-bff`.

## 1) Preparar variables

```bash
cp scripts/gcp-lb-iap/lb-iap.env.example scripts/gcp-lb-iap/lb-iap.env
```

Edita `scripts/gcp-lb-iap/lb-iap.env`:

- `LB_HOSTNAME` (ej. `api.axelior.ai`)
- `IAP_MEMBER` (ej. `domain:axelior.ai` o `group:equipo@axelior.ai`)
- `IAP_SUPPORT_EMAIL` válido para crear brand OAuth.

## 2) Provisionar LB + IAP

```bash
bash scripts/gcp-lb-iap/01_provision_lb_iap.sh
```

El script crea:

- NEG serverless regional
- Backend service global
- URL map + cert administrado + HTTPS proxy + forwarding rule
- (si es posible) Brand/Client OAuth para IAP
- IAM `roles/iap.httpsResourceAccessor` para `IAP_MEMBER`

### Notas importantes

- Si el script no puede habilitar IAP por falta de `client secret`, mostrará el comando manual.
- Si no existe DNS aún, te imprimirá la IP global del LB para crear registro A.

## 3) Ajuste de frontend

Cambiar el `apiUrl` de producción a:

`https://<LB_HOSTNAME>/api`

En este repo de referencia:

- `main/src/environments/environment.prod.ts`

Además, autorizar origen final del frontend en OAuth (Google Sign-In).

## 4) Validación

```bash
bash scripts/gcp-lb-iap/02_validate_access.sh
```

Checks esperados:

- `run.app` no público (401/403)
- Host LB/IAP responde con desafío IAP (redirect/401/403 sin sesión)
- Usuarios permitidos por IAP sí acceden vía navegador autenticado

## 5) Rollback

- Revertir `apiUrl` del frontend al endpoint anterior
- Quitar IAM de IAP para miembros agregados
- Deshabilitar IAP en backend service si fuera necesario
- Mantener LB sin tráfico mientras se corrigen recursos
