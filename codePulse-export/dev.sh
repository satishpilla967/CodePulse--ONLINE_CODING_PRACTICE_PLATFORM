#!/usr/bin/env bash
# One-command local dev runner: infra (MySQL/Redis/Judge0) + backend + frontend.
#
# Usage: ./dev.sh
# Stop everything with Ctrl+C (containers keep running in the background — see
# `docker compose down` if you want to fully stop them too).
set -euo pipefail
cd "$(dirname "$0")"

COMPOSE="docker compose"
if ! $COMPOSE version >/dev/null 2>&1; then
    if command -v docker-compose >/dev/null 2>&1; then
        COMPOSE="docker-compose"
    else
        echo "Neither 'docker compose' nor 'docker-compose' is available. Install Docker Desktop or the docker-compose-plugin package and try again." >&2
        exit 1
    fi
fi

if [ ! -f .env ]; then
    echo "No .env found — creating one from example.env with local-dev defaults."
    cp example.env .env
fi

# Load .env into this shell so the readiness check below and mvnw can see the same values.
set -a
# shellcheck disable=SC1091
source .env
set +a

echo "==> Starting MySQL, Redis, and Judge0 (docker compose)..."
$COMPOSE up -d

echo "==> Waiting for MySQL to accept connections..."
tries=0
until docker exec codepulse-mysql mysqladmin ping -h 127.0.0.1 -u root -p"${DATABASE_PASSWORD:-mysql}" --silent >/dev/null 2>&1; do
    tries=$((tries + 1))
    if [ "$tries" -gt 60 ]; then
        echo "MySQL did not become ready in time. Check 'docker compose logs mysql'." >&2
        exit 1
    fi
    sleep 2
done
echo "    MySQL is ready."

echo "==> Running database migrations (Flyway)..."
./mvnw -q flyway:migrate

echo "==> Starting backend (Spring Boot) and frontend (Vite) together."
echo "    Backend:  http://localhost:8080"
echo "    Frontend: http://localhost:5173"
echo "    Press Ctrl+C to stop both."
echo

cleanup() {
    echo
    echo "==> Stopping backend and frontend (containers from docker compose are left running)..."
    jobs -p | xargs -r kill 2>/dev/null || true
}
trap cleanup EXIT INT TERM

./mvnw spring-boot:run &
BACKEND_PID=$!

(cd js && pnpm install && pnpm dev) &
FRONTEND_PID=$!

wait "$BACKEND_PID" "$FRONTEND_PID"
