# 프로젝트 wiki

아래 링크에서 모든 프로젝트의 위키(트러블 슈팅 포함)를 확인할 수 있습니다!
[프로젝트 위키](https://knowing-jester-927.notion.site/655e8b180b3e452b89d13d8a95f4c997?v=7c03ec759b7743bd9f6ce1e3b0edc278)

아래는 대표적인 트러블 슈팅 두가지 - 일기 암호화, 일기 생성 비동기 처리에 관한 내용입니다 !

## 트러블 슈팅 - 일기 암호화

[전문](https://knowing-jester-927.notion.site/6541157188e3438b930df10ded6c1fba?pvs=4)

db에서 diary 테이블을 조회할 경우 팀원들의 일기를 그대로 볼 수 있는 문제점이 있었다.
해당 문제를 해결하고자 일기를 저장할 때 암호화하여 저장하고, 조회할 때 복호화하여 응답하는 방식을 도입하였다.

### 해결 방안

암호화, 복호화 알고리즘은 속도가 빠르고 안정적이라는 AES 알고리즘을 사용하였다.
암호화, 복호화 로직을 처리하는 유틸 클래스를 만들었다.

암호화, 복호화 로직을 처리하는 유틸 클래스는 도메인이 사용하면 좋을지, 서비스 클래스들이 사용하는게 좋을지 고민이 되었다.

서비스단에서 유틸 클래스를 사용해서 암호화해서 저장하고, 복호화해서 조회하는 방식을 사용하려 했으나,
DiaryService 클래스의 모든 메서드들은 Diary의 note(일기 내용)를 조회하려면 복호화해야 한다는 사실을 알아야 하기에 의존성이 높아지는 단점이 있었다.
만약 여러 사람과 협업한다면 일기 내용을 가져올 때 복호화해야 한다는 사실을 모두가 알고 있어야 한다.

따라서 엔티티에서 일기 내용을 조회하거나 저장할 때, 암호화, 복호화 Util 클래스를 사용하여 구현하는 방향으로 바꾸었다.
서비스단에서 엔티티의 일기 내용 필드를 사용할 때 일기 내용 필드가 암호화 되었다는 사실을 몰라도 되도록, 캡슐화하였다.

## 트러블 슈팅 - 일기 생성시 DALL-E 호출을 비동기적으로 처리해야하는 이슈

[전문](https://magenta-ming.tistory.com/20)

사용자는 일기 생성을 최종적으로 요청하면, 약 9초 뒤에 생성된 그림 일기를 조회할 수 있다.
그동안 사용자는 광고를 시청하거나 로딩 화면에서 대기한다.

일기를 생성하는 과정에서 OPEN AI의 DALL-E의 API를 호출해 이미지를 생성하는데, 이떄 평균적으로 약 8초 정도의 응답 시간이 소요된다.
이 부분은 동시에 일기를 생성할 때, 서버에 동시 처리 요청이 누적하며 증가할 수도 있고, 요청이 일정 수를 초과할 경우, 일기 생성이 아닌 일반 조회 API 요청 등도 처리할 수
없다는 잠재적 위험이 있다.
그리고, 분당 DALL-E API 요청량을 제어하지 못한다.

### 해결 방안 설계

따라서 일기 생성 요청을 비동기적으로 처리하는 방법을 설계하였다.
비동기적으로 처리하는 방법에도 여러가지가 있었지만, 현재 개발 상황을 고려해서 메시지 큐를 사용하는 방안을 선택하였다.
일기 생성에 대한 요청을 큐에 보관하고, 큐에서 정책에 따라 별도의 서버로 메시지(요청)을 전달하는 구조다.

메시지 큐를 선택한 이유는 아래와 같다.

1. 일기 생성에 대한 성공 실패 처리가 유연해진다.
2. DALL-E API에 대한 요청량을 조정할 수 있다.
3. 일기를 생성하는데에 소요되는 평균 9초의 대기시간이 필요 없다. 유저는 알림을 기다리면 된다.
4. DALL-E API가 아닌 Stable-Diffusion API 등 다른 외부 이미지 생성형 AI 서비스를 쓰게되더라도 실 서비스의 영향이 거의 없다.
5. 당연히, DALL-E API에 대한 요청을 비동기적으로 처리해, 다른 서비스에 영향이 가지 않도록 처리할 수 있다.

따라서 AWS SQS와 AWS Lambda를 사용하여 비동기적으로 처리하는 방법을 설계하였다.
![아키텍처 설계도](https://github.com/tipi-tapi/ai-paint-today-BE/assets/42285463/790a544c-c833-49cc-af33-ae3a12ebafd5)
해당 아키텍처는 클라이언트와 협의가 필요하기에 아직 도입되지 않았지만, 지속적으로 논의하여 개선해나가고자 한다.

