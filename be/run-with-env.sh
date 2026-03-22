#!/usr/bin/env zsh
# Nạp biến từ .env.local rồi chạy Spring Boot (dùng Maven Wrapper).
# Cách dùng: từ thư mục be:  ./run-with-env.sh
# Hoặc:  ./run-with-env.sh spring-boot:run

set -e
cd "$(dirname "$0")"

if [[ ! -f .env.local ]]; then
  echo "Không thấy .env.local trong $(pwd)"
  exit 1
fi

set -a
source .env.local
set +a

exec ./mvnw "${@:-spring-boot:run}"
