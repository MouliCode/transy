#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker/docker-compose.prod.yml"
ENV_FILE="$ROOT_DIR/.env.production"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-transy-prod}"

echo "Building backend artifacts..."
"$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" clean package -DskipTests

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed. JVM artifacts are built, container build skipped."
  exit 0
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker daemon is not running. JVM artifacts are built, container build skipped."
  exit 0
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Create it from .env.production.example before building containers."
  exit 1
fi

echo "Building production container images..."
docker compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" --env-file "$ENV_FILE" build

echo "Production build completed."
