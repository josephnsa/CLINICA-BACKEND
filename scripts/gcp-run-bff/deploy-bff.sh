#!/usr/bin/env bash
# Despliega el BFF en Cloud Run (sin clave JSON: usa la SA del servicio + metadata).
# Ejecutar desde Cloud Shell en la raíz del repo: bash scripts/gcp-run-bff/deploy-bff.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

if [[ -f "scripts/gcp/gcp.env" ]]; then
  # shellcheck source=/dev/null
  source "scripts/gcp/gcp.env"
fi

PROJECT_ID="${PROJECT_ID:-pe-axelior-clinapp-dev}"
REGION="${REGION:-us-central1}"
AR_REPO="${AR_REPO:-clinica-backend}"
BFF_IMAGE_NAME="${BFF_IMAGE_NAME:-salud-backend-bff}"
BFF_SERVICE="${BFF_SERVICE:-salud-backend-bff}"
SA_PROXY="${SA_PROXY:-netlify-run-proxy}"
SA_EMAIL="${SA_PROXY}@${PROJECT_ID}.iam.gserviceaccount.com"

# URL del servicio salud-backend (sin barra final). Override: export SALUD_BACKEND_URL=...
SALUD_BACKEND_URL="${SALUD_BACKEND_URL:-}"
if [[ -z "$SALUD_BACKEND_URL" ]]; then
  SALUD_BACKEND_URL="$(gcloud run services describe salud-backend \
    --region="$REGION" --project="$PROJECT_ID" \
    --format='value(status.url)' 2>/dev/null || true)"
fi
if [[ -z "$SALUD_BACKEND_URL" ]]; then
  echo "ERROR: Define SALUD_BACKEND_URL (https://salud-backend-....run.app) o despliega salud-backend antes."
  exit 1
fi
SALUD_BACKEND_URL="${SALUD_BACKEND_URL%/}"

IMAGE_URL="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPO}/${BFF_IMAGE_NAME}"
TAG="${BFF_TAG:-latest}"

echo ">>> BFF → $SALUD_BACKEND_URL (SA runtime: $SA_EMAIL)"

echo ">>> [1/4] Build imagen (contexto: scripts/gcp-run-bff)..."
docker build -t "${IMAGE_URL}:${TAG}" -f scripts/gcp-run-bff/Dockerfile scripts/gcp-run-bff

echo ">>> [2/4] Artifact Registry..."
gcloud artifacts repositories create "$AR_REPO" \
  --repository-format=docker \
  --location="$REGION" \
  --project="$PROJECT_ID" 2>/dev/null || true

gcloud auth configure-docker "${REGION}-docker.pkg.dev" --quiet

echo ">>> [3/4] Push..."
docker push "${IMAGE_URL}:${TAG}"

echo ">>> [4/4] Deploy Cloud Run BFF (puerto 8080, invoker: intento público)..."
set +e
gcloud run deploy "$BFF_SERVICE" \
  --image="${IMAGE_URL}:${TAG}" \
  --platform=managed \
  --region="$REGION" \
  --project="$PROJECT_ID" \
  --port=8080 \
  --memory=512Mi \
  --cpu=1 \
  --min-instances=0 \
  --max-instances=10 \
  --timeout=120s \
  --service-account="$SA_EMAIL" \
  --set-env-vars="SALUD_BACKEND_URL=${SALUD_BACKEND_URL}" \
  --allow-unauthenticated
DEPLOY_RC=$?
set -e

if [[ "$DEPLOY_RC" -ne 0 ]]; then
  echo ""
  echo "Si falló por política de org (allUsers / invoker público), pide excepción solo para el servicio: $BFF_SERVICE"
  echo "O despliega sin --allow-unauthenticated y usa IAP / LB (más infra)."
  exit "$DEPLOY_RC"
fi

echo ""
echo "URL del BFF (úsala como apiUrl en el front en lugar de salud-backend directo):"
gcloud run services describe "$BFF_SERVICE" \
  --region="$REGION" --project="$PROJECT_ID" \
  --format='value(status.url)'
