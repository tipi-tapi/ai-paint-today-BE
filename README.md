<div align="center">
  <br>
  <h2> AI 그림 일기 </h2>
  <h1> 오늘 하루를 그려줘 🎨 </h1>
  <strong>API 서버 레포지토리</strong>
</div>
<br>

- [프로젝트 소개](#프로젝트-소개)
    * [프로젝트 기능](#프로젝트-기능)
- [기술 스택](#기술-스택)
- [서버 아키텍처](#서버-아키텍처)
- [배포 파이프라인](#배포-파이프라인)
- [ERD](#erd)
- [기여자](#기여자)
- [프로젝트 wiki](#프로젝트-wiki)
- [패키지 구조](#패키지-구조)
- [컨벤션과 협업 전략](#컨벤션과-협업-전략)
- [API 에러 코드](#api-에러-코드)
- [개발 환경 설정](#개발-환경-설정)
- [실행](#실행)
    * [서버 실행](#서버-실행)
    * [데이터베이스 실행](#데이터베이스-실행)

## 프로젝트 소개

'오늘 하루를 그려줘'는 하루동안 느낀 감정과 일상을 기록하면, AI가 멋진 그림으로 당신의 하루를 그려주는 서비스입니다.

오늘의 감정과 일기를 작성하면 AI가 일기 내용을 바탕으로 그림 일기를 생성해요!

[🤖 안드로이드 플레이스토어 🤖](https://play.google.com/store/apps/details?id=kr.co.devstory.draw_my_today)

[🍎 IOS AppStore 🍎](https://apps.apple.com/kr/app/%EC%98%A4%EB%8A%98-%ED%95%98%EB%A3%A8%EB%A5%BC-%EA%B7%B8%EB%A0%A4%EC%A4%98-ai-%EA%B7%B8%EB%A6%BC%EC%9D%BC%EA%B8%B0/id6447301941)

![프로젝트 소개](https://github.com/tipi-tapi/ai-paint-today-BE/assets/42285463/d5b03ee7-4682-4700-b8eb-243c20e9ee07)

### 프로젝트 기능

[서비스 세부 기능](/docs/service_detail.md)

## 기술 스택

- Java 11
- Gradle 7.6.1
- Spring Boot 2.7.11
- Spring Data JPA
- Spring Security
- QueryDSL 5.0.0
- MySQL 8.0.33
- JUnit 5, Mockito
- Jacoco 0.8.8
- AWS(EC2, RDS, S3), Cloudflare(R2)
- GitHub Actions
- DALL-E 2
- 협업 : Notion, Discord, Google Meet

## 서버 아키텍처

![서버 아키텍처](https://github.com/tipi-tapi/ai-paint-today-BE/assets/42285463/e10fc250-3a0b-4c94-9e8b-22f9cf4db2ed)

## 배포 파이프라인

![CD 아키텍처](https://github.com/tipi-tapi/ai-paint-today-BE/assets/42285463/040e09ce-a317-409b-9098-d8e092d6263b)

## ERD

![ERD](https://github.com/tipi-tapi/ai-paint-today-BE/assets/42285463/69af9d53-677a-4efb-bd8c-5742484401e3)

## 기여자

| Avatar                                                                                         | Name | Team        | 개발 기간         |
| ---------------------------------------------------------------------------------------------- |------|-------------|---------------|
| <img src="https://avatars.githubusercontent.com/u/42285463?v=4" width="100px" height="100px"/> | 마민지  | 프로그라피 8기 4팀 | 2023.04 ~ ing |
| <img src="https://github-production-user-asset-6210df.s3.amazonaws.com/42285463/262602249-7bf7dad5-6a54-4985-9b24-3a58d68dd3be.png" width="100px" height="100px"/> | 최혁   | 프로그라피 8기 4팀 | 2023.04 ~ ing |

## 프로젝트 wiki

프로젝트를 경험하면서 알게된 지식, 경험을 정리한 위키입니다.

[프로젝트 위키](/docs/dev_wiki.md)

## 패키지 구조

[패키지 구조 설명](/docs/package_structure.md)

## 컨벤션과 협업 전략

어떤 컨벤션을 가지고 협업하였는 작성하였습니다. ( 로깅 컨벤션 포함 )

[컨벤션 & 협업 전략](docs/convention.md)

## API 에러 코드

팀 노션 링크에 정리하였습니다.
[API 에러 코드](https://knowing-jester-927.notion.site/API-0ebd3f6402954bc6bcc4f75141887d14?pvs=4)

## 개발 환경 설정

[개발 환경 문서](/docs/dev_setting.md)

## 실행

### 서버 실행

```shell
$ ./gradlew clean build
$ java -jar /build/libs/draw-my-today-0.0.1-SNAPSHOT.jar
```

### 데이터베이스 실행

docker-compose를 통해 로컬 DB를 실행할 수 있습니다.

```shell
# mysql 실행
$ docker-compose up --build
```

```shell
# mysql 종료
$ docker-compose down
```