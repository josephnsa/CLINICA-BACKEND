#!/bin/bash
# =============================================================
# 02_deploy.sh
# Build Maven → Docker image → Artifact Registry → Cloud Run
# Ejecutar desde Cloud Shell dentro de la carpeta del proyecto
# =============================================================
set -e

# ── Cargar variables ──────────────────────────────────────────
source "$(dirname "$0")/gcp.env"

IMAGE_URL="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPO}/${IMAGE_NAME}"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
echo ">>> [1/5] Build Maven..."
./mvnw clean package -DskipTests -B

echo ">>> [2/5] Creando repositorio en Artifact Registry (si no existe)..."
gcloud artifacts repositories create "$AR_REPO" \
  --repository-format=docker \
  --location="$REGION" \
  --project="$PROJECT_ID" 2>/dev/null || echo "Repositorio ya existe, continuando..."

echo ">>> [3/5] Autenticando Docker con Artifact Registry..."
gcloud auth configure-docker "${REGION}-docker.pkg.dev" --quiet

echo ">>> [4/5] Build y push de imagen Docker..."
docker build -t "${IMAGE_URL}:${IMAGE_TAG}" -t "${IMAGE_URL}:latest" .
docker push "${IMAGE_URL}:${IMAGE_TAG}"
docker push "${IMAGE_URL}:latest"

echo ">>> [5/5] Desplegando en Cloud Run..."
# Delimitador ^@^: si CORS_ALLOWED_ORIGINS lleva comas (varios orígenes), la coma
# no rompe el listado de variables (gcloud interpretaría comas como separador).
# GOOGLE_CLIENT_ID puede ir vacío si no usas Google Sign-In.
RUN_ENV_VARS="^@^SPRING_PROFILES_ACTIVE=prod@DB_HOST=${DB_HOST}@DB_NAME=${DB_NAME}@DB_USER=${DB_USER}@CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}@GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}"

gcloud run deploy "$SERVICE_NAME" \
  --image="${IMAGE_URL}:${IMAGE_TAG}" \
  --platform=managed \
  --region="$REGION" \
  --port=9090 \
  --memory=1Gi \
  --cpu=1 \
  --cpu-boost \
  --min-instances=0 \
  --max-instances=5 \
  --concurrency=80 \
  --timeout=60s \
  --startup-probe="initialDelaySeconds=15,timeoutSeconds=5,periodSeconds=10,failureThreshold=30,tcpSocket.port=9090" \
  --service-account="$SA_EMAIL" \
  --set-env-vars="${RUN_ENV_VARS}" \
  --set-secrets="DB_PASSWORD=DB_PASSWORD:latest,JWT_SECRET=JWT_SECRET:latest" \
  --allow-unauthenticated \
  --project="$PROJECT_ID"

echo ""
echo "============================================="
echo "✅ Despliegue exitoso."
echo "URL del servicio:"
gcloud run services describe "$SERVICE_NAME" \
  --region="$REGION" --project="$PROJECT_ID" \
  --format="value(status.url)"
echo "============================================="
