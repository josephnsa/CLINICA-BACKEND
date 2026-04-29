# Plan Actualizado: Frontend en Firebase Hosting + Backend en Cloud Run

## 1) Estado actual

| Componente | Estado | Detalle |
|---|---|---|
| API Gateway | Listo | `https://clinica-gateway-doyh1s4q.uc.gateway.dev` operativo |
| Backend en Cloud Run | Listo | Servicio `salud-backend` privado detras de gateway |
| Frontend Angular | Listo para migracion | Pipeline y deploy ya ajustados a Firebase Hosting |
| Firebase | Activado | Proyecto `pe-axelior-clinapp-dev` habilitado |
| CI/CD Frontend | Listo | Trigger `release/develop` usa `cloudbuild.yaml` |
| CI/CD Backend | Activo | Se mantiene sin cambios funcionales |

## 2) Arquitectura objetivo

```text
Push a release/develop (frontend)
  -> Cloud Build
  -> npm ci + ng build (production)
  -> firebase deploy --only hosting
  -> https://pe-axelior-clinapp-dev.web.app

Push a release/develop (backend)
  -> Cloud Build backend
  -> Cloud Run privado
  -> API Gateway publico
```

## 3) Decisiones clave

- Frontend ya no se publica en Cloud Run.
- Firebase Hosting es el frontend oficial.
- Backend se mantiene en Cloud Run privado detras de API Gateway.
- Dominio personalizado (`subdominio.axelior.ai`) se mapeara en Firebase cuando DNS este listo.

## 4) Cambios aplicados

### Frontend

- `cloudbuild.yaml` actualizado para deploy a Firebase Hosting.
- `firebase.json` creado con `public: dist/Modernize/browser` y rewrite SPA.
- `.firebaserc` creado con proyecto `pe-axelior-clinapp-dev`.
- `scripts/gcp/setup.sh` actualizado para permisos de Firebase.

### Backend

- `scripts/gcp/gcp.env` actualizado con:
  - `https://pe-axelior-clinapp-dev.web.app`
  - `https://pe-axelior-clinapp-dev.firebaseapp.com`
  - `https://subdominio.axelior.ai`

## 5) Verificacion final esperada

- Push de frontend a `release/develop` publica en Firebase Hosting.
- `https://pe-axelior-clinapp-dev.web.app` carga sin errores de rutas.
- OAuth Google funciona desde dominios de Firebase.
- Frontend consume API Gateway sin errores CORS.
- Push backend no afecta frontend.