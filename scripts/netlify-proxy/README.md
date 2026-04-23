# Proxy Netlify → Cloud Run (IAM sin `allUsers`)

Cuando la política de organización **no permite** `allUsers` como `roles/run.invoker` en Cloud Run, el navegador **no puede** llamar directo a `https://*.run.app`. Este patrón pone una **Netlify Function** en el mismo dominio del front: el tráfico es same-origin hacia Netlify; la función llama a Cloud Run con una **cuenta de servicio** e **ID token** (audience = URL del servicio).

> Si **`iam.disableServiceAccountKeyCreation`** impide crear la clave JSON para Netlify, usa el **BFF en Cloud Run** (ADC, sin JSON): `scripts/gcp-run-bff/README.md` y `deploy-bff.sh`.

## 1. GCP (quien tenga permisos de IAM)

Sustituye `PROJECT_ID` y la URL de Cloud Run si difieren.

```bash
export PROJECT_ID=pe-axelior-clinapp-dev
export REGION=us-central1
export SERVICE=salud-backend
export SA_NAME=netlify-run-proxy

gcloud iam service-accounts create "$SA_NAME" \
  --display-name="Netlify proxy Cloud Run" \
  --project="$PROJECT_ID"

gcloud run services add-iam-policy-binding "$SERVICE" \
  --region="$REGION" \
  --project="$PROJECT_ID" \
  --member="serviceAccount:${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/run.invoker"
```

Clave JSON para Netlify (rotar si se filtra; en producción valorar **Workload Identity Federation** sin JSON):

```bash
gcloud iam service-accounts keys create netlify-proxy-key.json \
  --iam-account="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --project="$PROJECT_ID"
```

El contenido completo de `netlify-proxy-key.json` se usará en Netlify (ver abajo).

## 2. Copiar al repo del frontend (Netlify)

Copia esta carpeta al **raíz del sitio** que despliega Netlify (donde ya tienes `package.json` del Angular):

- `netlify.toml` → raíz (o fusiona el bloque `[[redirects]]` y `[functions]` con lo que ya tengas).
- `netlify/functions/cloud-run-proxy.mjs` → misma ruta relativa.
- En el `package.json` **raíz** del sitio Netlify añade la dependencia: `"google-auth-library": "^9.0.0"` (Netlify instala desde la raíz para bundlear la función).

## 3. Variables de entorno en Netlify (Site settings → Environment variables)

| Variable | Valor |
|----------|--------|
| `CLOUD_RUN_URL` | `https://salud-backend-1073061395882.us-central1.run.app` (sin barra final) |
| `GCP_SA_JSON` | Pegar el JSON de la clave **en una sola línea** (minificado) o usar UI de “file” / secret de Netlify |

Build command del Angular no cambia; asegúrate de que Netlify ejecute `npm install` en la raíz para instalar `google-auth-library`.

## 4. Angular / front

Hoy la API apunta a la URL `.run.app`. Cámbiala a la ruta **del mismo host Netlify** que reescribe a la función:

Ejemplo: si en `netlify.toml` usas `from = "/backend-api/*"`, entonces:

`environment.prod.ts` (o el que use Netlify):

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://incandescent-sunshine-0253c2.netlify.app/backend-api'
};
```

Las peticiones deben seguir siendo `/api/...` respecto a esa base, es decir la URL final será  
`https://....netlify.app/backend-api/api/auth/...`  
(igual que antes pero el host es Netlify y el prefijo `/backend-api` dispara el proxy).

## 5. CORS en el backend

`CORS_ALLOWED_ORIGINS` en Cloud Run debe incluir el origen del front Netlify (ya lo tenías). El proxy server-to-server no depende de CORS hacia Run.

## 6. Probar

1. Deploy del front en Netlify con la función y env vars.
2. Abrir DevTools → Network: las llamadas van a `tu-sitio.netlify.app/backend-api/...` con 200 (no CORS bloqueado hacia `run.app`).
3. Cloud Run solo verá la SA como invocador autenticado.
