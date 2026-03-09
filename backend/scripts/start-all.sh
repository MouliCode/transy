#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
PID_DIR="$ROOT_DIR/.pids"

SERVICES=(
  "api-gateway"
  "config-server"
  "device-service"
  "discovery-server"
  "signaling-service"
  "transfer-service"
)

service_port() {
  case "$1" in
    api-gateway) echo "8080" ;;
    config-server) echo "8888" ;;
    device-service) echo "8083" ;;
    discovery-server) echo "8761" ;;
    signaling-service) echo "8082" ;;
    transfer-service) echo "8085" ;;
    *) echo "" ;;
  esac
}

listening_pid() {
  local port="$1"
  lsof -t -nP -iTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -n 1 || true
}

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

mkdir -p "$LOG_DIR" "$PID_DIR"

start_service() {
  local service="$1"
  local port
  port="$(service_port "$service")"
  local pid_file="$PID_DIR/${service}.pid"
  local log_file="$LOG_DIR/${service}.log"
  local profile_arg=""

  if [[ "$service" == "transfer-service" ]]; then
    profile_arg="SPRING_PROFILES_ACTIVE=local"
  fi

  local port_pid
  port_pid="$(listening_pid "$port")"
  if [[ -n "$port_pid" ]]; then
    if is_managed_java_pid "$port_pid"; then
      echo "$service already running on port $port (pid=$port_pid)"
      echo "$port_pid" > "$pid_file"
      return
    fi
    echo "Cannot start $service: port $port is used by unmanaged process (pid=$port_pid)."
    return 1
  fi

  if [[ -f "$pid_file" ]]; then
    rm -f "$pid_file"
  fi

  echo "Starting $service on port $port ..."
  if [[ -n "$profile_arg" ]]; then
    nohup env $profile_arg "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" -pl "$service" spring-boot:run >"$log_file" 2>&1 &
  else
    nohup "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" -pl "$service" spring-boot:run >"$log_file" 2>&1 &
  fi

  local attempts=0
  while [[ $attempts -lt 30 ]]; do
    sleep 1
    port_pid="$(listening_pid "$port")"
    if [[ -n "$port_pid" ]]; then
      if ! is_managed_java_pid "$port_pid"; then
        attempts=$((attempts + 1))
        continue
      fi
      echo "$port_pid" > "$pid_file"
      echo "$service started (pid=$port_pid, port=$port, log=$log_file)"
      return
    fi
    attempts=$((attempts + 1))
  done

  echo "Failed to start $service on port $port. Check log: $log_file"
  return 1
}

failed=()
for service in "${SERVICES[@]}"; do
  if ! start_service "$service"; then
    failed+=("$service")
  fi
done

if [[ ${#failed[@]} -eq 0 ]]; then
  echo "All implemented microservices are running."
else
  echo "Failed services: ${failed[*]}"
  exit 1
fi
echo "Use scripts/status-all.sh to check status and scripts/stop-all.sh to stop all services."
