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
  pid_file="$PID_DIR/${service}.pid"

  pid=""
  unmanaged_pid=""
  if [[ -f "$pid_file" ]]; then
    file_pid="$(cat "$pid_file" 2>/dev/null || true)"
    if [[ -n "${file_pid:-}" ]] && is_managed_java_pid "$file_pid"; then
      pid="$file_pid"
    fi
  fi

  port_pid="$(lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true)"
  if [[ -z "$pid" ]] && [[ -n "$port_pid" ]]; then
    if is_managed_java_pid "$port_pid"; then
      pid="$port_pid"
    else
      unmanaged_pid="$port_pid"
    fi
  fi

  if [[ -n "$pid" ]]; then
    echo "Stopping $service (pid=$pid, port=$port) ..."
    kill "$pid" || true
  elif [[ -n "$unmanaged_pid" ]]; then
    echo "Skipping $service: port $port is owned by unmanaged process (pid=$unmanaged_pid)."
  else
    echo "$service already stopped (port $port not listening)."
  fi

  rm -f "$pid_file"
done

echo "Stop command sent for all implemented microservices."
