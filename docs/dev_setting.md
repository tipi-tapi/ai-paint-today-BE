# 개발 환경 세팅

## 인텔리제이 자동 재시작 설정

1. 설정 > 빌드, 실행, 배포 > 컴파일러 > 프로젝트 자동 빌드 체크
2. 고급 설정 > 컴파일러 > 개발된 애플리케이션이 현재 실행 중인 경우에도 auto-make가 시작되도록 허용 체크

### profile 환경변수 설정 :

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 혹은 스크린 샷과 같이 접속
   <img width="863" alt="스크린샷 2023-01-07 오후 4 06 05" src="https://user-images.githubusercontent.com/42285463/211137975-87d0e79c-7f8b-4640-9eae-0ad03d68fef5.png">
2. Active profiles에 develop 입력 ( 개발용의 경우 develop, production의 경우 prod 입력 )
   <img width="1042" alt="스크린샷 2023-01-07 오후 4 07 21" src="https://user-images.githubusercontent.com/42285463/211138359-e071c6ff-6fa5-432e-87e0-101e759b6037.png">

### 환경 변수 추가 및 변경 시 ( production ) :

- application-dev.yml에 해당하는 환경변수를 추가하고, 환경변수 구조 파악을 위하여 application-prod.yml에 추가되는 환경변수의 이름을 추가.
- 실제 환경변수 값을 Github Secrets에 업데이트

### 인텔리제이에 환경변수 추가 :

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 ( profile 환경변수 설정 섹션 참고 )
2. Modify Options > Environment Variables 체크
3. 해당하는 환경변수 추가

### 테스트 시 환경변수를 템플릿으로 설정 :

테스트를 수행할 때마다, 환경변수를 설정하는 작업을, 인텔리제이에서 템플릿으로 설정하면 테스트마다 설정하지 않아도 됩니다.

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 혹은 스크린 샷과 같이 접속
   <img width="863" alt="스크린샷 2023-01-07 오후 4 06 05" src="https://user-images.githubusercontent.com/42285463/211137975-87d0e79c-7f8b-4640-9eae-0ad03d68fef5.png">
2. 좌측 하단 Edit configuration templates 클릭
   ![스크린샷 2023-02-08 오후 4 34 00](https://user-images.githubusercontent.com/42285463/217463831-b6ff6405-ffd6-4783-83c4-30db0bdfa8f0.png)
3. JUnit/Gradle을 선택한 후, 테스트 실행시와 동일한 설정값을 입력하고, 환경변수 입력한다. 그리고 테스트 설정을 설정할 범위를 'All in directory'에
   명세한다.
   ![스크린샷 2023-02-08 오후 4 34 47](https://user-images.githubusercontent.com/42285463/217464007-55927e00-94db-41bb-b1f7-56de17f3358e.png)

## 배포

main 브랜치에 새로운 커밋을 푸시하면, Github actions를 통해 ECS로 배포됨

## 참고사항

EC2가 아닌 로컬에서 실행할 때, `com.amazonaws.SdkClientException: Failed to connect to service endpoint:` 해결법

1. 해당 구문은 EC2 메타데이터를 읽다가 이슈가 발생한 것으로 EC2 인스턴스가 아닌 환경에서 실행할 때에는 의미 없는 에러임
2. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 ( profile 환경변수 설정 섹션 참고 )
3. Modify Options > VM Options 체크
4. VM Options 섹션에 `-Dcom.amazonaws.sdk.disableEc2Metadata=true` 추가
5. 설정을 완료한 뒤, Springboot을 실행하면 `EC2 Instance Metadata Service is disabled` 라는 구문이 뜨면서 EC2 메타데이터 서비스를
   제외하고 실행할 수 있음