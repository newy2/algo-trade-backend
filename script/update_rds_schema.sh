#!/bin/bash
set -e

# 외부 입력 필수 환경변수 확인
if ! [[ "$APP_ENV" == "dev" || "$APP_ENV" == "prd" ]]; then
  echo "APP_ENV 는 'dev' 또는 'prd' 으로 입력해주세요. (APP_ENV = '$APP_ENV')" >&2
  exit 1
fi

# 필수 환경변수 확인
ACCESS_PORT=3389
EICE_ID=$(aws ssm get-parameter --name "/vpc/eice/rds-connect/id" --query "Parameter.Value" --output text)
if [[ -z $EICE_ID ]]; then echo "EICE_ID 을 찾을 수 없습니다." >&2; exit 1; fi

RDS_ADDRESS=$(aws ssm get-parameter --name "/rds/address" --query "Parameter.Value" --output text)
if [[ -z $RDS_ADDRESS ]]; then echo "RDS_ADDRESS 을 찾을 수 없습니다." >&2; exit 1; fi

RDS_USERNAME=$(aws ssm get-parameter --name "/rds/username" --query "Parameter.Value" --output text)
if [[ -z $RDS_USERNAME ]]; then echo "RDS_USERNAME 을 찾을 수 없습니다." >&2; exit 1; fi

RDS_PASSWORD=$(aws ssm get-parameter --name "/rds/password" --with-decryption --query "Parameter.Value" --output text)
if [[ -z $RDS_PASSWORD ]]; then echo "RDS_PASSWORD 을 찾을 수 없습니다." >&2; exit 1; fi

RDS_PRIVATE_IP=$(nslookup $RDS_ADDRESS | grep "Address" | tail -n 1 | awk '{print $2}')
if [[ -z $RDS_PRIVATE_IP ]]; then echo "RDS_PRIVATE_IP 을 찾을 수 없습니다." >&2; exit 1; fi

# 터널 열기
echo "Opening RDS tunnel..."
aws ec2-instance-connect open-tunnel \
  --instance-connect-endpoint-id $EICE_ID \
  --private-ip-address $RDS_PRIVATE_IP \
  --local-port $ACCESS_PORT \
  --remote-port $ACCESS_PORT &>/dev/null &

TUNNEL_PID=$!
echo "Tunnel opened with PID $TUNNEL_PID"

# 터널 상태 확인
echo "Waiting for tunnel to open..."
for i in {1..30}; do
  echo "Waiting $i"
  if nc -z localhost $ACCESS_PORT; then
    echo "Tunnel is open"
    break
  fi
  sleep 1
done

if ! nc -z localhost $ACCESS_PORT; then
  echo "Failed to establish tunnel" >&2
  kill $TUNNEL_PID || true
  exit 1
fi

# Liquibase 명령 실행
echo "Updating database schema..."
./gradlew :ddl:liquibase:update \
  -DX_APP_ENV=$APP_ENV \
  -DX_DBMS_NAME=postgresql \
  -DX_POSTGRESQL_JDBC_URL=jdbc:postgresql://localhost:$ACCESS_PORT/ \
  -DX_POSTGRESQL_USERNAME=$RDS_USERNAME \
  -DX_POSTGRESQL_PASSWORD=$RDS_PASSWORD

# 터널 닫기
echo "Closing RDS tunnel..."
kill $TUNNEL_PID || echo "Failed to close tunnel" >&2
