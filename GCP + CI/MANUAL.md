# Manual: Pasos Fuera de Codigo (Firebase Hosting + OAuth + DNS)

## Objetivo

Checklist manual para operar con frontend en Firebase Hosting y backend en Cloud Run/API Gateway.

## 1) Firebase Hosting

Ruta: `Firebase Console -> Hosting`.

- Crear el sitio Hosting por defecto del proyecto `pe-axelior-clinapp-dev` (si aun no existe).
- Verificar URLs:
  - `https://pe-axelior-clinapp-dev.web.app`
  - `https://pe-axelior-clinapp-dev.firebaseapp.com`

## 2) Permisos Cloud Build para deploy a Firebase

En Cloud Shell:

```bash
PROJECT_ID="pe-axelior-clinapp-dev"
PROJECT_NUMBER=$(gcloud projects describe "$PROJECT_ID" --format='value(projectNumber)')
CB_SA="${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com"

gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:${CB_SA}" \
  --role="roles/firebase.admin" \
  --condition=None
```

## 3) Trigger de frontend

Ruta: `Cloud Build -> Activadores`.

- Validar trigger `frontend-deploy-release-develop`.
- Debe apuntar a rama `release/develop` y archivo `cloudbuild.yaml`.

## 4) OAuth Google

Ruta: `Google Cloud -> APIs & Services -> Credentials -> OAuth 2.0 Client`.

En `Authorized JavaScript origins` agregar:

- `http://localhost:4200`
- `https://pe-axelior-clinapp-dev.web.app`
- `https://pe-axelior-clinapp-dev.firebaseapp.com`
- `https://subdominio.axelior.ai` (cuando exista)

Nota:
- No usar IP como origen OAuth.
- No usar URL del API Gateway como origen frontend.

## 5) CORS backend

Actualizar backend con orígenes de Firebase y redeployar backend:

- `https://pe-axelior-clinapp-dev.web.app`
- `https://pe-axelior-clinapp-dev.firebaseapp.com`
- `https://subdominio.axelior.ai` (cuando exista)

## 6) Dominio personalizado (Julio)

Ruta: `Firebase Console -> Hosting -> Add custom domain`.

Julio debe crear los registros DNS solicitados por Firebase para `subdominio.axelior.ai`.

Resultado esperado:
- Firebase emite SSL automáticamente.
- Frontend queda accesible por `https://subdominio.axelior.ai`.

## 7) Limpieza opcional de infraestructura temporal

Cuando Firebase este estable, eliminar recursos creados en la ruta temporal de LB:

- `frontend-neg`
- `frontend-backend`
- `frontend-urlmap`
- `frontend-http-proxy`
- `frontend-http-rule`

## 8) Validacion final

- Push frontend a `release/develop` despliega en Firebase Hosting.
- URL `web.app` carga login y rutas SPA.
- Login Google funciona sin `invalid_request`.
- Frontend llama al API Gateway sin CORS.
