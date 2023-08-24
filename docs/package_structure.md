# 패키지 구조

`tipitapi.drawmytoday.domain` 패키지 내부의 패키지 경로입니다.

## 유저 user

서비스 사용자에 관한 패키지 입니다.

```
user
├── controller
├── domain
├── exception
├── repository
└── service
```

## 일기 diary

일기에 관한 패키지 입니다.

일기 패키지는 '일기(diary)', '이미지(image)', '프롬프트(prompt)' 도메인을 포함하고 있습니다.

일기를 작성하면, 일기 내용을 기반으로 프롬프트를 생성하고, 프롬프트를 기반으로 이미지를 생성합니다.

```

diary
├── controller
├── domain
├── dto
├── exception
├── repository
└── service

```

## 감정 emotion

일기 작성에 필요한 감정에 관한 패키지 입니다.

```
emotion
├── controller
├── domain
├── dto
├── exception
├── repository
└── service
```

## 로그인 oauth

사용자의 로그인에 관한 패키지 입니다.

```
oauth
├── controller
├── domain
├── dto
├── exception
├── properties
├── repository
└── service
```

## 광고 adreward

사용자는 광고를 시청하면 광고 데이터를 등록합니다.
특정 종류의 광고를 시청하면, 티켓을 발급합니다.

```
adreward
├── controller
├── domain
├── repository
└── service
```

## 티켓 ticket

사용자는 티켓을 사용하여, 일기를 작성할 수 있습니다.
최초 회원가입시 7개의 티켓이 부여되며, 이후에는 광고를 시청하여 티켓을 획득할 수 있습니다.

```
tree
├── domain
├── exception
├── repository
└── service
```

## CloudFlare R2 처리 r2

R2로 이미지를 업로드하고, R2로부터 일정 시간동안 유효한 Presigned URL을 발급합니다.
기존에는 aws S3를 사용헀지만, 보다 저렴한 r2를 이용하도록 마이그레이션 하였습니다.

```
r2
├── exception
└── service
```

## OPEN AI DALL-E 서비스 처리 dalle

일기 내용을 기반으로 작성된 프롬프트를 통해 OPEN AI의 DALL-E 서비스 API를 요청합니다.

```
dalle
├── dto
├── exception
└── service
```

## 관리자용 서비스 admin

관리자만 조회할 수 있는 서비스를 제공합니다.
전체 프롬프트 목록과 이미지 등을 조회할 수 있습니다.

```
admin
├── controller
├── dto
└── service
```

## 개발용 서비스 dev

실서버가 아닌, 개발 서버에서 주어진 토큰을 만료시키는 서비스를 제공합니다.
프로필 설정을 통해 실서버에서는 해당 API를 사용할 수 없도록 처리하였습니다.

```
dev
└── controller
```

## API 헬스 체크 health

API가 정상적으로 동작하는지 체크할 수 있는 API를 제공합니다.
팀 디스코드 채널으로 1분마다 헬스 체크 결과를 전송합니다.

```
health
└── controller
```