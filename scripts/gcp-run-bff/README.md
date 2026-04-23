# BFF en Cloud Run (sin clave JSON)

Cuando **`iam.disableServiceAccountKeyCreation`** impide usar `GCP_SA_JSON` en Netlify, este contenedor corre en **Cloud Run** con la SA **`netlify-run-proxy`** (runtime). Obtiene **ID token** vía **metadata** (ADC) y llama a **`salud-backend`**.

Requisitos previos (ya los hiciste):

- SA `netlify-run-proxy` con **`roles/run.invoker`** sobre el servicio **`salud-backend`**.

## Desde Cloud Shell (raíz del repo `CLINICA-BACKEND`)

```bash
cd ~/CLINICA-BACKEND   # o tu ruta
bash scripts/gcp-run-bff/deploy-bff.sh
```

Opcional: fijar la URL del backend si no quieres autodetectar:

```bash
export SALUD_BACKEND_URL="https://salud-backend-1073061395882.us-central1.run.app"
bash scripts/gcp-run-bff/deploy-bff.sh
```

El script intenta **`--allow-unauthenticated`** en el **BFF** para que el navegador (Netlify) pueda llamarlo. Si la **misma política de org** lo bloquea, verás el error en el paso 4: hace falta excepción para **`salud-backend-bff`** o otra arquitectura (IAP, etc.).

## Front (Angular / Netlify)

`apiUrl` = **URL del BFF** que imprime el script (termina en `.run.app`), **sin** sufijo `/backend-api`: el BFF reenvía la misma ruta (`/api/...`) al backend.

Mantén en Cloud Run del backend **`CORS_ALLOWED_ORIGINS`** con el origen de Netlify (el BFF reenvía el header `Origin` del navegador).

## Seguridad

Este BFF **no valida** usuarios: cualquiera que pueda invocar la URL pública del BFF puede llegar al backend. La protección fuerte debe estar en **Spring** (JWT, Google login, roles). Valora rate limiting / Cloud Armor en el BFF si lo expones público.
