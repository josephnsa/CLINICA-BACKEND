#!/bin/bash
# =============================================================
# 01_setup_infra.sh
# Crea Cloud SQL, usuario, BD y secretos en Secret Manager
# Ejecutar UNA SOLA VEZ desde Cloud Shell
# =============================================================
set -e

# ── Cargar variables ──────────────────────────────────────────
source "$(dirname "$0")/gcp.env"

echo ">>> [1/6] Creando instancia Cloud SQL..."
gcloud sql instances create "$CLOUD_SQL_INSTANCE_NAME" \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region="$REGION" \
  --storage-type=SSD \
  --storage-size=20GB \
  --storage-auto-increase \
  --backup-start-time=03:00 \
  --no-assign-ip \
  --project="$PROJECT_ID"

echo ">>> [2/6] Creando base de datos..."
gcloud sql databases create "$DB_NAME" \
  --instance="$CLOUD_SQL_INSTANCE_NAME" \
  --project="$PROJECT_ID"

echo ">>> [3/6] Creando usuario de aplicacion..."
gcloud sql users create "$DB_USER" \
  --instance="$CLOUD_SQL_INSTANCE_NAME" \
  --password="$DB_PASSWORD" \
  --project="$PROJECT_ID"

echo ">>> [4/6] Guardando secretos en Secret Manager..."
echo -n "$DB_PASSWORD" | gcloud secrets create DB_PASSWORD \
  --data-file=- --project="$PROJECT_ID" 2>/dev/null || \
  echo -n "$DB_PASSWORD" | gcloud secrets versions add DB_PASSWORD \
  --data-file=- --project="$PROJECT_ID"

echo -n "$JWT_SECRET" | gcloud secrets create JWT_SECRET \
  --data-file=- --project="$PROJECT_ID" 2>/dev/null || \
  echo -n "$JWT_SECRET" | gcloud secrets versions add JWT_SECRET \
  --data-file=- --project="$PROJECT_ID"

echo ">>> [5/6] Creando Service Account..."
gcloud iam service-accounts create "$SA_NAME" \
  --display-name="Clinica Backend SA" \
  --project="$PROJECT_ID" 2>/dev/null || echo "SA ya existe, continuando..."

SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

echo ">>> [6/6] Asignando permisos al Service Account..."
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"

echo ""
echo "============================================="
echo "✅ Infraestructura lista."
echo "CLOUD_SQL_CONNECTION_NAME:"
gcloud sql instances describe "$CLOUD_SQL_INSTANCE_NAME" \
  --project="$PROJECT_ID" --format="value(connectionName)"
echo "============================================="
