#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
PID_DIR="$ROOT_DIR/.pids"

SERVICES=(
  "device-service"
  "signaling-service"
)

service_port() {
  case "$1" in
    device-service) echo "8083" ;;
    signaling-service) echo "8082" ;;
    *) echo "" ;;
  esac
}

listening_pid() {
  local port="$1"
  lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true
}

mkdir -p "$LOG_DIR" "$PID_DIR"

start_service() {
  local service="$1"
  local port
  port="$(service_port "$service")"
  local pid_file="$PID_DIR/${service}.pid"
  local log_file="$LOG_DIR/${service}.log"

  local port_pid
  port_pid="$(listening_pid "$port")"
  if [[ -n "$port_pid" ]]; then
    echo "$service already running on port $port (pid=$port_pid)"
    echo "$port_pid" > "$pid_file"
    return
  fi

  if [[ -f "$pid_file" ]]; then
    rm -f "$pid_file"
  fi

  echo "Starting $service on port $port ..."
  nohup "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" -pl "$service" spring-boot:run >"$log_file" 2>&1 &

  local attempts=0
  while [[ $attempts -lt 30 ]]; do
    sleep 1
    port_pid="$(listening_pid "$port")"
    if [[ -n "$port_pid" ]]; then
      echo "$port_pid" > "$pid_file"
      echo "$service started (pid=$port_pid, port=$port, log=$log_file)"
      return
    fi
    attempts=$((attempts + 1))
  done

  echo "Failed to start $service on port $port. Check log: $log_file"
  return 1
}

for service in "${SERVICES[@]}"; do
  start_service "$service"
done

echo "All implemented microservices are running."
echo "Use scripts/status-all.sh to check status and scripts/stop-all.sh to stop all services."
