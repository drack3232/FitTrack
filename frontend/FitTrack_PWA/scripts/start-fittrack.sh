#!/usr/bin/env bash
# One-command FitTrack development launcher: Spring Boot + Vite on Arch Linux.
set -Eeuo pipefail

ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
BACKEND="${FITTRACK_BACKEND_DIR:-}"
FRONTEND="${FITTRACK_FRONTEND_DIR:-$ROOT/fittrack-pwa}"
BACKEND_PORT="${FITTRACK_BACKEND_PORT:-8080}"
FRONTEND_PORT="${FITTRACK_FRONTEND_PORT:-5173}"
DB_URL="${FITTRACK_DB_URL:-jdbc:postgresql://localhost:5433/fittrack}"

if [[ -z "$BACKEND" ]]; then
  for path in "$ROOT/beckend/fittrack" "$ROOT/backend/fittrack" "$ROOT/fittrack"; do
    if [[ -f "$path/gradlew" ]]; then
      BACKEND="$path"
      break
    fi
  done
fi

if [[ ! -f "$BACKEND/gradlew" ]]; then
  echo "ERROR: Spring Boot directory not found. Set FITTRACK_BACKEND_DIR." >&2
  exit 1
fi
if [[ ! -f "$FRONTEND/package.json" ]]; then
  echo "ERROR: React directory not found. Set FITTRACK_FRONTEND_DIR." >&2
  exit 1
fi
if [[ ! -d "$FRONTEND/node_modules" ]]; then
  echo "ERROR: React dependencies missing. Run: cd \"$FRONTEND\" && npm install" >&2
  exit 1
fi
if ! command -v setsid >/dev/null; then
  echo "ERROR: setsid is required (util-linux)" >&2
  exit 1
fi

if command -v ss >/dev/null; then
  if ss -ltnH | grep -Eq ":(${BACKEND_PORT}|${FRONTEND_PORT})[[:space:]]"; then
    echo "WARNING: port ${BACKEND_PORT} or ${FRONTEND_PORT} is already listening." >&2
    echo "Stop the previous FitTrack instances before using this script." >&2
    exit 1
  fi
fi

backend_pid=""
frontend_pid=""
cleanup() {
  trap - EXIT INT TERM
  echo "Stopping FitTrack..."
  if [[ -n "$frontend_pid" ]]; then
    kill -TERM -- "-$frontend_pid" 2>/dev/null || kill -TERM "$frontend_pid" 2>/dev/null || true
  fi
  if [[ -n "$backend_pid" ]]; then
    kill -TERM -- "-$backend_pid" 2>/dev/null || kill -TERM "$backend_pid" 2>/dev/null || true
  fi
  wait 2>/dev/null || true
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

printf 'FitTrack development mode\n  Backend: %s\n  Frontend: %s\n  Database: %s\n' "$BACKEND" "$FRONTEND" "$DB_URL"
printf '  React: http://localhost:%s\n' "$FRONTEND_PORT"

setsid bash -c 'cd "$1" && exec ./gradlew bootRun --args="--server.port=$2 --spring.datasource.url=$3"' _ \
  "$BACKEND" "$BACKEND_PORT" "$DB_URL" &
backend_pid=$!

setsid bash -c 'cd "$1" && exec npm run dev -- --host 0.0.0.0 --port "$2"' _ \
  "$FRONTEND" "$FRONTEND_PORT" &
frontend_pid=$!

printf 'Press Ctrl+C to stop both processes.\n\n'
wait -n "$backend_pid" "$frontend_pid"
