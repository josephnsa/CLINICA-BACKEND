#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

if [[ -f "scripts/gcp-lb-iap/lb-iap.env" ]]; then
  # shellcheck source=/dev/null
  source "scripts/gcp-lb-iap/lb-iap.env"
elif [[ -f "scripts/gcp-lb-iap/lb-iap.env.example" ]]; then
  # shellcheck source=/dev/null
  source "scripts/gcp-lb-iap/lb-iap.env.example"
else
  echo "ERROR: No existe scripts/gcp-lb-iap/lb-iap.env(.example)"
  exit 1
fi

: "${PROJECT_ID:?PROJECT_ID requerido}"
: "${REGION:?REGION requerido}"
: "${RUN_SERVICE:?RUN_SERVICE requerido}"
: "${NEG_NAME:?NEG_NAME requerido}"
: "${BACKEND_SERVICE_NAME:?BACKEND_SERVICE_NAME requerido}"
: "${URL_MAP_NAME:?URL_MAP_NAME requerido}"
: "${HTTPS_PROXY_NAME:?HTTPS_PROXY_NAME requerido}"
: "${FORWARDING_RULE_NAME:?FORWARDING_RULE_NAME requerido}"
: "${SSL_CERT_NAME:?SSL_CERT_NAME requerido}"
: "${LB_HOSTNAME:?LB_HOSTNAME requerido}"
: "${IAP_MEMBER:?IAP_MEMBER requerido}"

if [[ -z "${PROJECT_NUMBER:-}" ]]; then
  PROJECT_NUMBER="$(gcloud projects describe "$PROJECT_ID" --format='value(projectNumber)')"
fi

echo ">>> Configurando proyecto $PROJECT_ID ($PROJECT_NUMBER)"
gcloud config set project "$PROJECT_ID" >/dev/null

echo ">>> Habilitando APIs necesarias"
gcloud services enable \
  run.googleapis.com \
  compute.googleapis.com \
  iap.googleapis.com \
  certificatemanager.googleapis.com \
  --project="$PROJECT_ID"

echo ">>> Creando Serverless NEG -> Cloud Run: $RUN_SERVICE"
gcloud compute network-endpoint-groups create "$NEG_NAME" \
  --region="$REGION" \
  --network-endpoint-type=serverless \
  --cloud-run-service="$RUN_SERVICE" \
  --project="$PROJECT_ID" 2>/dev/null || true

echo ">>> Creando Backend Service global"
gcloud compute backend-services create "$BACKEND_SERVICE_NAME" \
  --global \
  --load-balancing-scheme=EXTERNAL_MANAGED \
  --protocol=HTTP \
  --project="$PROJECT_ID" 2>/dev/null || true

gcloud compute backend-services add-backend "$BACKEND_SERVICE_NAME" \
  --global \
  --network-endpoint-group="$NEG_NAME" \
  --network-endpoint-group-region="$REGION" \
  --project="$PROJECT_ID" 2>/dev/null || true

echo ">>> URL map y SSL cert administrado"
gcloud compute url-maps create "$URL_MAP_NAME" \
  --default-service="$BACKEND_SERVICE_NAME" \
  --project="$PROJECT_ID" 2>/dev/null || true

gcloud compute ssl-certificates create "$SSL_CERT_NAME" \
  --domains="$LB_HOSTNAME" \
  --global \
  --project="$PROJECT_ID" 2>/dev/null || true

echo ">>> Target HTTPS proxy + forwarding rule"
gcloud compute target-https-proxies create "$HTTPS_PROXY_NAME" \
  --url-map="$URL_MAP_NAME" \
  --ssl-certificates="$SSL_CERT_NAME" \
  --project="$PROJECT_ID" 2>/dev/null || true

gcloud compute forwarding-rules create "$FORWARDING_RULE_NAME" \
  --global \
  --target-https-proxy="$HTTPS_PROXY_NAME" \
  --ports=443 \
  --project="$PROJECT_ID" 2>/dev/null || true

LB_IP="$(gcloud compute forwarding-rules describe "$FORWARDING_RULE_NAME" --global --format='value(IPAddress)' --project="$PROJECT_ID")"
echo ">>> IP del LB: $LB_IP"
echo ">>> Crea/actualiza DNS A: $LB_HOSTNAME -> $LB_IP"

echo ">>> Configurando OAuth consent para IAP (si no existe)"
set +e
BRAND_NAME="$(gcloud iap oauth-brands list --format='value(name)' --project="$PROJECT_ID" 2>/dev/null | head -n1)"
set -e
if [[ -z "$BRAND_NAME" ]]; then
  gcloud iap oauth-brands create \
    --application_title="${IAP_APP_TITLE:-Salud Backend BFF}" \
    --support_email="${IAP_SUPPORT_EMAIL:-}" \
    --project="$PROJECT_ID"
  BRAND_NAME="$(gcloud iap oauth-brands list --format='value(name)' --project="$PROJECT_ID" | head -n1)"
fi

echo ">>> Creando cliente OAuth IAP (si no existe uno)"
set +e
CLIENT_ID="$(gcloud iap oauth-clients list "$BRAND_NAME" --format='value(name)' --project="$PROJECT_ID" 2>/dev/null | head -n1)"
set -e

CLIENT_SECRET=""
if [[ -z "$CLIENT_ID" ]]; then
  OAUTH_CREATE_OUTPUT="$(gcloud iap oauth-clients create "$BRAND_NAME" --display_name="lb-iap-${RUN_SERVICE}" --format=json --project="$PROJECT_ID")"
  CLIENT_ID="$(echo "$OAUTH_CREATE_OUTPUT" | python -c "import sys, json; d=json.load(sys.stdin); print(d.get('name',''))")"
  CLIENT_SECRET="$(echo "$OAUTH_CREATE_OUTPUT" | python -c "import sys, json; d=json.load(sys.stdin); print(d.get('secret',''))")"
  echo ">>> Guarda este IAP_CLIENT_SECRET de forma segura (Secret Manager)."
else
  echo ">>> Ya existe un cliente OAuth de IAP. Si no tienes su secret, crea uno nuevo y úsalo abajo."
fi

if [[ -n "$CLIENT_ID" && -n "$CLIENT_SECRET" ]]; then
  echo ">>> Habilitando IAP en backend service"
  gcloud compute backend-services update "$BACKEND_SERVICE_NAME" \
    --global \
    --iap=enabled,oauth2-client-id="$CLIENT_ID",oauth2-client-secret="$CLIENT_SECRET" \
    --project="$PROJECT_ID"
else
  echo ">>> SKIP: No se habilitó IAP automáticamente (falta client secret)."
  echo ">>> Ejecuta manualmente:"
  echo "gcloud compute backend-services update \"$BACKEND_SERVICE_NAME\" --global --iap=enabled,oauth2-client-id=<CLIENT_ID>,oauth2-client-secret=<CLIENT_SECRET> --project=\"$PROJECT_ID\""
fi

echo ">>> Dando acceso IAP a $IAP_MEMBER"
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="$IAP_MEMBER" \
  --role="roles/iap.httpsResourceAccessor" \
  --condition=None >/dev/null

echo ""
echo "Provision completado (con pasos manuales si faltó IAP secret)."
echo "Host esperado: https://$LB_HOSTNAME"
