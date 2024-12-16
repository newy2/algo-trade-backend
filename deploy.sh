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

# Auto Scaling 그룹의 인스턴스 개수 조회
INSTANCE_COUNT=$(aws autoscaling describe-auto-scaling-groups \
  --auto-scaling-group-names $AUTO_SCALING_GROUP_NAME \
  --query "length(AutoScalingGroups[0].Instances)" \
  --output text)

# 진행중인 배포가 있는 경우 종료 (인스턴스 개수가 1개를 초과하는 경우)
echo "[실행중인 EC2 인스턴스 개수] $AUTO_SCALING_GROUP_NAME: $INSTANCE_COUNT instance(s)"
if (( INSTANCE_COUNT > 1 )); then
  echo "진행중인 배포가 있습니다. 스크립트를 종료합니다."
  exit 1
else
  echo "진행중인 배포가 없습니다. 배포 스크립트를 진행합니다."
fi

# 이미지 빌드
IMAGE_NAME="$APP_ENV"-webflux-server
docker build -t $IMAGE_NAME -f api-server/web-flux/Dockerfile --platform linux/amd64 .

# 이미지 푸시
ECR_URL=$(aws ssm get-parameter \
  --name "/code/delivery/$APP_ENV/backend/ecr/repository/url" \
  --query "Parameter.Value" \
  --output text)
aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_URL
docker tag "$IMAGE_NAME":latest "$ECR_URL":latest
docker push "$ECR_URL":latest