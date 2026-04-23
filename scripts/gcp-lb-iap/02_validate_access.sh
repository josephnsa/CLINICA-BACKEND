#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

if [[ -f "scripts/gcp-lb-iap/lb-iap.env" ]]; then
  # shellcheck source=/dev/null
  source "scripts/gcp-lb-iap/lb-iap.env"
else
  # shellcheck source=/dev/null
  source "scripts/gcp-lb-iap/lb-iap.env.example"
fi

: "${PROJECT_ID:?PROJECT_ID requerido}"
: "${REGION:?REGION requerido}"
: "${RUN_SERVICE:?RUN_SERVICE requerido}"
: "${LB_HOSTNAME:?LB_HOSTNAME requerido}"

echo ">>> 1) Verificar que Cloud Run NO es público"
gcloud run services get-iam-policy "$RUN_SERVICE" \
  --region="$REGION" \
  --project="$PROJECT_ID" \
  --format="table(bindings.role,bindings.members)"

echo ""
echo ">>> 2) Probar endpoint run.app sin credenciales (esperado: 401/403)"
RUN_URL="$(gcloud run services describe "$RUN_SERVICE" --region="$REGION" --project="$PROJECT_ID" --format='value(status.url)')"
curl -sS -o /dev/null -w "run.app status=%{http_code}\n" "${RUN_URL}/api/auth/google/login"

echo ""
echo ">>> 3) Probar host LB/IAP sin sesión (esperado: redirect/401/403 de IAP)"
curl -sS -o /dev/null -w "lb+iap status=%{http_code}\n" "https://${LB_HOSTNAME}/api/auth/google/login"

echo ""
echo ">>> 4) Ver logs recientes de acceso en Cloud Run"
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=${RUN_SERVICE}" \
  --project="$PROJECT_ID" \
  --limit=20 \
  --format='table(timestamp,httpRequest.requestMethod,httpRequest.status,textPayload)'

echo ""
echo "Validación completada."
