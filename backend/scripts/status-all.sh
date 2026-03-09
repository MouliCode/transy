#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

SERVICES=(
  "api-gateway:8080"
  "config-server:8888"
  "device-service:8083"
  "discovery-server:8761"
  "signaling-service:8082"
  "transfer-service:8085"
)

process_command() {
  local pid="$1"
  ps -p "$pid" -o command= 2>/dev/null || true
}

is_managed_java_pid() {
  local pid="$1"
  local cmd
  cmd="$(process_command "$pid")"
  [[ -n "$cmd" ]] || return 1
  [[ "$cmd" == *java* ]] || return 1
  [[ "$cmd" == *"$ROOT_DIR"* ]]
}

for item in "${SERVICES[@]}"; do
  service="${item%%:*}"
  port="${item##*:}"
  pid="$(lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true)"
  if [[ -n "$pid" ]]; then
    if is_managed_java_pid "$pid"; then
      echo "$service: RUNNING (managed pid=$pid, port=$port)"
    else
      echo "$service: PORT IN USE (unmanaged pid=$pid, port=$port)"
    fi
  else
    echo "$service: STOPPED (port=$port not listening)"
  fi
done
