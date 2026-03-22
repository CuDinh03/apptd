#!/usr/bin/env zsh
# Chạy Adminer để xem Postgres: http://localhost:8081
# Cần Docker Desktop (hoặc Docker Engine) đã cài và đang chạy.

set -e
cd "$(dirname "$0")/.."
if ! command -v docker >/dev/null 2>&1; then
  echo "Chưa có lệnh 'docker'. Cài Docker Desktop: https://www.docker.com/products/docker-desktop/"
  exit 1
fi
docker compose up -d
echo "Mở trình duyệt: http://localhost:8081"
if command -v open >/dev/null 2>&1; then
  open "http://localhost:8081"
fi
