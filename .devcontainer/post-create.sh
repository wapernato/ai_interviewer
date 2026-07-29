#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
fi

(
  cd ai_tutor
  ./mvnw --quiet --show-version --no-transfer-progress -DskipTests dependency:go-offline
)

(
  cd frontend
  npm ci
)
