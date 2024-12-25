#!/bin/sh
set -e

# 필수 환경변수 확인
if ! [[ "$APP_ENV" == "test" || "$APP_ENV" == "prod" ]]; then
  echo "APP_ENV 는 'test' 또는 'prod' 으로 입력해주세요. (APP_ENV = '$APP_ENV')"
  exit 1
fi

# Auto Scaling 그룹명 조회
AUTO_SCALING_GROUP_NAME=$(aws autoscaling describe-auto-scaling-groups \
  --query "AutoScalingGroups[*].AutoScalingGroupName" \
  --output text)

if [ -z "$AUTO_SCALING_GROUP_NAME" ]; then
  echo "Error: AutoScalingGroup Name 을 찾을 수 없습니다."
  exit 1
fi

# Auto Scaling 그룹의 인스턴스 개수 조회
INSTANCE_COUNT=$(aws autoscaling describe-auto-scaling-groups \
  --auto-scaling-group-names $AUTO_SCALING_GROUP_NAME \
  --query "length(AutoScalingGroups[0].Instances)" \
  --output text)

if [ -z "$INSTANCE_COUNT" ]; then
  echo "Error: EC2 인스턴스 개수를 조회할 수 없습니다."
  exit 1
fi

# ECR URL 조회
ECR_URL=$(aws ssm get-parameter \
  --name "/code/delivery/$APP_ENV/backend/ecr/repository/url" \
  --query "Parameter.Value" \
  --output text)

if [ -z "$ECR_URL" ]; then
  echo "Error: ECR URL 을 찾을 수 없습니다."
  exit 1
fi

# 진행중인 배포가 있는 경우 종료 (인스턴스 개수가 1개를 초과하는 경우)
echo "[실행중인 EC2 인스턴스 개수] $AUTO_SCALING_GROUP_NAME: $INSTANCE_COUNT instance(s)"
if (( INSTANCE_COUNT > 1 )); then
  echo "Error: 진행중인 배포가 있습니다."
  exit 1
fi

# 이미지 빌드 (TODO Spring Security 구현 시, X_ADMIN_IP 제거)
IMAGE_NAME="$APP_ENV"-webflux-server
docker build \
  --build-arg X_ADMIN_IP=$(curl -s ifconfig.me) \
  -t $IMAGE_NAME \
  -f api-server/web-flux/Dockerfile \
  --platform linux/amd64 \
  .

# 이미지 푸시
aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_URL
docker tag "$IMAGE_NAME":latest "$ECR_URL":latest
docker push "$ECR_URL":latest

echo "ECR Push 완료"