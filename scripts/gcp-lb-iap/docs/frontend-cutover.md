# Frontend cutover hacia LB + IAP

## 1) Configuración de API

En el frontend Angular (`main`), usar:

- `main/src/environments/environment.prod.ts`
- `apiUrl: 'https://api.axelior.ai/api'` (o host final de LB)

## 2) OAuth Google (orígenes/autorizados)

En Google Cloud Console -> APIs y Servicios -> Credenciales -> Cliente OAuth Web:

- Agregar en **Authorized JavaScript origins**:
  - `https://<dominio-frontend>`
  - (si aplica) `https://<dominio-lb>` cuando el flujo lo requiera
- Agregar en **Authorized redirect URIs** solo si tu frontend/SDK usa redirect URI explícita.

## 3) CORS backend

`CORS_ALLOWED_ORIGINS` del backend debe incluir el origen del frontend final.

## 4) Verificación de corte

- Login con Google completo desde el dominio final.
- Llamadas a `/api/*` responden sin error de CORS.
- No aparece 403 de Google Frontend por invocación no autenticada.
