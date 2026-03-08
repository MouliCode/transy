#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_DIR="$ROOT_DIR/.pids"

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
  pid_file="$PID_DIR/${service}.pid"

  pid="$(lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true)"
  if [[ -n "$pid" ]]; then
    echo "Stopping $service (pid=$pid, port=$port) ..."
    kill "$pid" || true
  else
    echo "$service already stopped (port $port not listening)."
  fi

  rm -f "$pid_file"
done

echo "Stop command sent for all implemented microservices."
