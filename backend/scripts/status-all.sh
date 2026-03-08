#!/usr/bin/env bash
set -euo pipefail

SERVICES=(
  "api-gateway:8080"
  "config-server:8888"
  "device-service:8083"
  "discovery-server:8761"
  "signaling-service:8082"
  "transfer-service:8085"
)

for item in "${SERVICES[@]}"; do
  service="${item%%:*}"
  port="${item##*:}"
  pid="$(lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true)"
  if [[ -n "$pid" ]]; then
    echo "$service: RUNNING (pid=$pid, port=$port)"
  else
    echo "$service: STOPPED (port=$port not listening)"
  fi
done
