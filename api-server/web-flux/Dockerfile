FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

# Gradle 설정 파일 복사
COPY settings.gradle.kts gradle.properties /app/
COPY api-server/core/coroutine-based-application/build.gradle.kts /app/api-server/core/coroutine-based-application/
COPY api-server/core/domain/build.gradle.kts /app/api-server/core/domain/
COPY api-server/web-flux/build.gradle.kts /app/api-server/web-flux/
COPY buildSrc/src /app/buildSrc/src
COPY buildSrc/build.gradle.kts /app/buildSrc

# 의존성 캐시 빌드
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 소스 코드 복사 (변경 가능성 높음)
COPY api-server/core /app/api-server/core
COPY api-server/web-flux /app/api-server/web-flux

# web-flux 모듈 빌드
RUN gradle :api-server:web-flux:bootJar -x test --parallel

# 런타임 스테이지
#FROM amazoncorretto:21-alpine AS runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
COPY --from=builder /app/api-server/web-flux/build/libs/*SNAPSHOT.jar /app.jar

ENV X_PORT -1
ENV X_DB_URL ""
ENV X_DB_SCHEMA ""
ENV X_DB_USERNAME ""
ENV X_DB_PASSWORD ""

USER nobody
ENTRYPOINT ["java", "-jar", "/app.jar"]