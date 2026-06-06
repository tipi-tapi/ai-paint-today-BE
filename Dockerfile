# syntax=docker/dockerfile:1

### Stage 1: layered 추출 + CDS 아카이브 생성 ###
# CDS 아카이브는 classpath 경로에 민감하므로 빌드/런타임 WORKDIR을 /app으로 통일한다.
FROM eclipse-temurin:21-jre-jammy AS cds
WORKDIR /build
COPY build/libs/draw-my-today-0.0.1-SNAPSHOT.jar boot.jar
# Spring Boot 3.3 layered jar 추출 (실행 런처 + lib/). 빈 /app 으로 추출한다.
RUN java -Djarmode=tools -jar boot.jar extract --destination /app

WORKDIR /app
# CDS 트레이닝 런: context를 한 번 refresh 하며 로드된 클래스를 app.jsa에 아카이빙한다.
# 아래 더미 값은 config placeholder 충족용일 뿐이며, 실제 값은 런타임에 Cloud Run이 주입한다.
RUN profile=dev \
    FIRESTORE_PROJECT_ID=dummy FIRESTORE_DATABASE_ID="(default)" \
    GOOGLE_APPLICATION_CREDENTIALS=/dev/null \
    CONSOLE_LOG_PATTERN="%d %-5level %logger - %msg%n" \
    ENCRYPTOR_SECRET_KEY=00000000000000000000000000000000 \
    PRESIGNED_IMAGE_EXPIRATION_ADMIN_DIARIES=3600 \
    JWT_SECRET=0000000000000000000000000000000000000000000000000000000000000000 \
    STABILITY_API_KEY=dummy NEGATIVE_PROMPT=dummy \
    KARLO_API_URL=http://dummy KARLO_DEFAULT_STYLE=dummy KAKAO_API_KEY=dummy \
    OPENAI_API_KEY=dummy DALLE_API_URL=http://dummy \
    GPT_CHAT_COMPLETIONS_PROMPT=dummy GPT_CHAT_COMPLETIONS_REGENERATE_PROMPT=dummy \
    AWS_ACCESS_KEY_ID=dummy AWS_SECRET_ACCESS_KEY=dummy AWS_S3_BUCKET=dummy \
    R2_ACCESS_KEY_ID=dummy R2_SECRET_ACCESS_KEY=dummy R2_BUCKET_NAME=dummy R2_ACCOUNT_ID=dummy R2_CUSTOM_DOMAIN=http://dummy \
    GOOGLE_CLIENT_ID=dummy GOOGLE_CLIENT_SECRET=dummy GOOGLE_TOKEN_URL=http://dummy GOOGLE_USER_INFO_URL=http://dummy GOOGLE_REDIRECT_URI=http://dummy GOOGLE_DELETE_ACCOUNT_URL=http://dummy \
    APPLE_IOS_CLIENT_ID=dummy APPLE_IOS_TEAM_ID=dummy APPLE_IOS_KEY_ID=dummy APPLE_IOS_PRIVATE_KEY=dummy APPLE_IOS_TOKEN_URL=http://dummy APPLE_IOS_DELETE_ACCOUNT_URL=http://dummy \
    timeout 180 java -XX:ArchiveClassesAtExit=app.jsa -Dspring.context.exit=onRefresh -jar boot.jar || true
# 아카이브가 생성되지 않았으면 빌드 실패시켜 무음 성능 저하를 방지한다.
RUN test -f app.jsa

### Stage 2: 런타임 ###
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=cds /app/ ./
EXPOSE 8080
# -XX:SharedArchiveFile: CDS 아카이브 사용 (시작 시간 단축)
# -XX:TieredStopAtLevel=1: 짧은 수명 컨테이너용 빠른 시작 (min-instances=0 환경)
ENTRYPOINT ["java", \
  "-XX:SharedArchiveFile=app.jsa", \
  "-XX:+TieredCompilation", \
  "-XX:TieredStopAtLevel=1", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:+UseContainerSupport", \
  "-jar", "boot.jar"]
