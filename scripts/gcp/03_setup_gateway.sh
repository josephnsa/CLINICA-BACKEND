#!/bin/bash
# =============================================================
# 03_setup_gateway.sh
# Crea Cloud API Gateway delante de salud-backend (Cloud Run privado)
# Ejecutar UNA SOLA VEZ desde Cloud Shell: bash scripts/gcp/03_setup_gateway.sh
# Prerequisito: 02_deploy.sh ya ejecutado exitosamente
# =============================================================
set -euo pipefail

source "$(dirname "$0")/gcp.env"

API_NAME="${API_NAME:-clinica-salud-api}"
GATEWAY_NAME="${GATEWAY_NAME:-clinica-gateway}"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

# ── 1. Habilitar APIs necesarias ──────────────────────────────
echo ">>> [1/6] Habilitando APIs de API Gateway..."
gcloud services enable \
  apigateway.googleapis.com \
  servicemanagement.googleapis.com \
  servicecontrol.googleapis.com \
  --project="$PROJECT_ID"

# api-config ids son inmutables en GCP: cada cambio de OpenAPI requiere un id nuevo (v2, v3, …).
# Tras habilitar APIs: si GATEWAY_CONFIG_ID no está en gcp.env, se elige el primer vN libre.
if [[ -n "${GATEWAY_CONFIG_ID:-}" ]]; then
  CONFIG_ID="$GATEWAY_CONFIG_ID"
else
  CONFIG_ID=""
  for i in $(seq 1 50); do
    cid="v${i}"
    if ! gcloud api-gateway api-configs describe "$cid" \
        --api="$API_NAME" --project="$PROJECT_ID" &>/dev/null; then
      CONFIG_ID="$cid"
      break
    fi
  done
  if [[ -z "$CONFIG_ID" ]]; then
    echo "ERROR: No hay id libre entre v1 y v50. Define GATEWAY_CONFIG_ID en scripts/gcp/gcp.env (ej. v51)." >&2
    exit 1
  fi
  echo ">>> GATEWAY_CONFIG_ID no definido en gcp.env; usando primer id libre: ${CONFIG_ID}"
fi

# ── 2. Obtener URL del backend Cloud Run ──────────────────────
echo ">>> [2/6] Obteniendo URL de salud-backend..."
BACKEND_URL="$(gcloud run services describe "$SERVICE_NAME" \
  --region="$REGION" --project="$PROJECT_ID" \
  --format='value(status.url)')"
BACKEND_URL="${BACKEND_URL%/}"
echo "    Backend: $BACKEND_URL"

# ── 3. Generar OpenAPI spec ───────────────────────────────────
echo ">>> [3/6] Generando OpenAPI spec..."
cat > /tmp/api-config.yaml << EOF
swagger: "2.0"
info:
  title: ${API_NAME}
  description: API Gateway para salud-backend en Cloud Run
  version: "1.0.0"
host: "placeholder.uc.gateway.dev"
schemes:
  - https
produces:
  - application/json

paths:
  # ── Rutas públicas (sin auth de gateway) ───────────────────
  /api/auth/login:
    post:
      operationId: authLogin
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "200":
          description: OK
    options:
      operationId: authLoginOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "204":
          description: OK

  /api/auth/google/login:
    post:
      operationId: googleLogin
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "200":
          description: OK
    options:
      operationId: googleLoginOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "204":
          description: OK

  /api/auth/refresh:
    post:
      operationId: authRefresh
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "200":
          description: OK
    options:
      operationId: authRefreshOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "204":
          description: OK

  /api/portal/auth/register:
    post:
      operationId: portalRegister
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "200":
          description: OK
    options:
      operationId: portalRegisterOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "204":
          description: OK

  /api/portal/auth/login:
    post:
      operationId: portalLogin
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "200":
          description: OK
    options:
      operationId: portalLoginOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      responses:
        "204":
          description: OK

  # ── Catch-all: todo lo demás hacia el backend ──────────────
  # ESPv2 mueve el Authorization del cliente a X-Forwarded-Authorization
  # y usa el SA token como Authorization para Cloud Run IAM.
  # Spring Boot lee X-Forwarded-Authorization para autenticar al usuario.
  /api/{path=**}:
    options:
      operationId: apiCatchAllOptions
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "204":
          description: OK
    get:
      operationId: apiGet
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "200":
          description: OK
    post:
      operationId: apiPost
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "200":
          description: OK
    put:
      operationId: apiPut
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "200":
          description: OK
    patch:
      operationId: apiPatch
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "200":
          description: OK
    delete:
      operationId: apiDelete
      x-google-backend:
        address: ${BACKEND_URL}
        path_translation: APPEND_PATH_TO_ADDRESS
      parameters:
        - in: path
          name: path
          required: true
          type: string
      responses:
        "200":
          description: OK
EOF
echo "    Spec generado en /tmp/api-config.yaml"

# ── 4. Crear API (idempotente) ────────────────────────────────
echo ">>> [4/6] Creando API '${API_NAME}'..."
gcloud api-gateway apis create "$API_NAME" \
  --project="$PROJECT_ID" 2>/dev/null || echo "    API ya existe, continuando..."

if gcloud api-gateway api-configs describe "$CONFIG_ID" \
    --api="$API_NAME" --project="$PROJECT_ID" &>/dev/null; then
  echo "    api-config '${CONFIG_ID}' ya existe; omitiendo create..."
else
  echo "    Creando api-config '${CONFIG_ID}'..."
  gcloud api-gateway api-configs create "$CONFIG_ID" \
    --api="$API_NAME" \
    --openapi-spec=/tmp/api-config.yaml \
    --project="$PROJECT_ID" \
    --backend-auth-service-account="$SA_EMAIL"
fi

# ── 5. Crear o actualizar Gateway ─────────────────────────────
echo ">>> [5/6] Creando gateway '${GATEWAY_NAME}'..."
if gcloud api-gateway gateways describe "$GATEWAY_NAME" \
    --location="$REGION" --project="$PROJECT_ID" &>/dev/null; then
  echo "    Gateway ya existe, actualizando a config '${CONFIG_ID}'..."
  gcloud api-gateway gateways update "$GATEWAY_NAME" \
    --api="$API_NAME" \
    --api-config="$CONFIG_ID" \
    --location="$REGION" \
    --project="$PROJECT_ID"
else
  gcloud api-gateway gateways create "$GATEWAY_NAME" \
    --api="$API_NAME" \
    --api-config="$CONFIG_ID" \
    --location="$REGION" \
    --project="$PROJECT_ID"
fi

# ── 6. Dar run.invoker al SA en Cloud Run ─────────────────────
echo ">>> [6/6] Asignando roles/run.invoker al SA en Cloud Run..."
gcloud run services add-iam-policy-binding "$SERVICE_NAME" \
  --region="$REGION" \
  --project="$PROJECT_ID" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/run.invoker" 2>/dev/null || \
  echo "    Binding ya existe o sin permisos (pide a org-admin: gcloud run services add-iam-policy-binding)."

# ── Resultado ─────────────────────────────────────────────────
GATEWAY_HOST="$(gcloud api-gateway gateways describe "$GATEWAY_NAME" \
  --location="$REGION" --project="$PROJECT_ID" \
  --format='value(defaultHostname)')"

echo ""
echo "============================================="
echo "API Gateway listo."
echo ""
echo "URL Gateway:  https://${GATEWAY_HOST}"
echo "URL con /api: https://${GATEWAY_HOST}/api"
echo ""
echo "Siguiente paso — actualiza el frontend:"
echo "  src/environments/environment.prod.ts:"
echo "    apiUrl: 'https://${GATEWAY_HOST}/api',"
echo ""
echo "Agrega al OAuth client en GCP Console (APIs & Services -> Credentials):"
echo "  Authorized JavaScript origins: https://${GATEWAY_HOST}"
echo ""
echo "Agrega la URL del gateway a CORS en gcp.env:"
echo "  CORS_ALLOWED_ORIGINS=...,https://${GATEWAY_HOST}"
echo "  Luego re-despliega: bash scripts/gcp/02_deploy.sh"
echo "============================================="
