#!/usr/bin/env bash
set -euo pipefail

SERVICES=(
  "device-service:8083"
  "signaling-service:8082"
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
