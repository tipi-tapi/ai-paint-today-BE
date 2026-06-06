# 운영 배포 DB 마이그레이션 런북 (RDS → Firestore 싱크)

운영 전환 시점에 RDS(MySQL)의 데이터를 Firestore로 동기화하는 절차다. 이번 전환은
**id 스키마(int→String) 변경을 동반**하므로 사전 동기화는 전체 재적재(full reload)로 하고,
cutover 시점의 짧은 잔여 변경분만 delta로 반영한다. 자세한 스키마 이슈는 아래
"ID 스키마 전환" 섹션 참고.

## 전제 / 핵심 원칙

- **Firestore는 cutover 전까지 애플리케이션이 write하지 않는다.** 신규(Firestore) 백엔드가
  아직 트래픽을 받지 않으므로, 지금 차분을 미리 넣어도 안전하다. 단 이 전제가 깨지면 안 된다 —
  **cutover 전까지 그 어떤 writer(신규 백엔드 staging 인스턴스 포함)도 운영 Firestore
  (`draw-my-today` / `draw-my-today-db`)를 건드리면 안 된다.**
- 적재는 전부 `set()` 기반이라 **멱등**하다. 같은 데이터로 여러 번 돌려도 결과가 같고, 중간
  실패 시 동일 명령 재실행으로 복구된다. baseline을 잘못 잡아도 데이터는 깨지지 않으며 delta가
  커질 뿐이다.
- **소프트 삭제** 모델이라 삭제는 `deletedAt`이 채워진 "변경 row"로 전파된다. RDS row를
  물리 삭제(hard delete)한 경우는 `set()`으로 제거할 수 없어 동기화되지 않는다(비교 스크립트가
  `EXCESS`로 탐지).
- **ticket / adReward는 마이그레이션 대상이 아니다.** 추출·적재 모두 제외하며, 이미 Firestore에
  적재돼 있던 기존 데이터는 삭제하지 않고 그대로 둔다.

## ⚠️ ID 스키마 전환 (int → String)

운영 `draw-my-today-db`의 레거시 데이터는 모든 id 필드가 **int**(예: `userId: 1`)이지만,
애플리케이션이 가정하는 스키마(`draw-my-today-db-dev`)는 id 필드가 **String**이다.
(`emotionId`만 예외로 양쪽 모두 int — emotion은 값으로 쿼리하지 않는 마스터 데이터.)

- **앱 코드 변경**: `FirestoreIdUtils.toStorageType`가 항상 String을 반환하도록 변경됨.
  emotion 저장은 전용 `toNumericStorageType`(int 유지). bulk load 매퍼도 동일하게
  user/diary/image/prompt id는 String, emotionId는 int로 적재한다.
- **반드시 함께 배포**: 새 앱(String)만 배포하면 int 레거시 데이터 쿼리가 실패하고, 운영만
  String으로 변환하면 구 앱(Long 쿼리)이 실패한다. **"전체 재적재(String 변환) + 새 앱 배포"를
  같은 유지보수 창에서 끝낸 뒤** 트래픽을 연다.
- **스키마 전환은 full reload 필수**: 모든 레거시 문서의 id 타입을 바꿔야 하므로 이 전환만큼은
  delta가 아니라 전체 재적재로 한다(RDS 데이터 자체는 안 바뀌어 delta가 "변환 필요"를 감지 못 함 →
  일부만 String이 되어 int/String 혼재로 깨짐). 전체 String 전환 이후의 후속 동기화는
  String↔String이라 delta 사용 가능.

## 관련 스크립트 / 코드

| 위치 | 역할 |
|---|---|
| `draw-my-today-script/extract_prod.py` | RDS(bastion SSH 터널 경유) 전체 추출 → `prod-data.json` |
| `draw-my-today-script/build_delta.py` | 이전 `prod-data.json`과 새 추출본을 비교 → `prod-data-delta.json` (전체 String 전환 이후의 후속 동기화용) |
| `draw-my-today-script/compare_firestore.py` | 추출 스냅샷과 Firestore 실제 적재 상태 대조(완료 검증, String id 기준) |
| `draw-my-today-script/inspect_schema.py` | 임의 DB의 샘플 문서·필드 타입 덤프(스키마 점검, read-only) |
| `draw-my-today-script/delete_test_data.py` | 앱이 운영에 만든 테스트 데이터(비-RDS id) 삭제 (dry-run 기본, `--apply`) |
| `BulkLoadJob` (`bootRunBulkLoad` gradle task) | JSON을 Firestore에 적재 (`BULK_LOAD_DELTA_MODE`로 차분 모드) |

> Python 스크립트는 모두 **uv**로 실행한다. 의존성 설치는 `uv sync` 한 번이면 된다.

## 사전 준비 (서비스 중단 없이)

```bash
# 1) 스크립트 의존성 설치
cd ../temp/draw-my-today-script   # draw-my-today-script 레포 경로
uv sync

# 2) RDS / bastion 접속 정보 설정 (.env)
cp .env.example .env   # 최초 1회
#   DB_HOST/PORT/USER/PASSWORD/NAME + BASTION_HOST/USER/KEY_PATH 채움

# 3) Firestore 자격증명 (적재 + 비교 공통)
export GOOGLE_APPLICATION_CREDENTIALS=/Users/qraft/work/ai-paint-today-BE/key.json
#   key.json 이 운영 프로젝트(draw-my-today) 서비스계정인지 확인:
jq -r '.client_email, .project_id' /Users/qraft/work/ai-paint-today-BE/key.json
#
#   ★ 중요: GOOGLE_APPLICATION_CREDENTIALS 가 셸에 export 되어 있지 않으면
#     gcloud 사용자 ADC 로 폴백되어 403 PERMISSION_DENIED 가 난다. 매 셸에서 export 할 것.
#
#   ★ 중요: 이 신원(SA)에 Firestore 데이터 읽기/쓰기 역할이 있어야 한다.
#     배포 전용 SA(예: github-actions-deploy)는 run.admin / artifactregistry.writer 만 있어
#     Firestore 권한이 없으므로 bulk load(write)·compare(read) 모두 403 이 난다.
#     roles/datastore.user (읽기+쓰기) 를 부여한다:
gcloud projects add-iam-policy-binding draw-my-today \
  --member="serviceAccount:$(jq -r '.client_email' /Users/qraft/work/ai-paint-today-BE/key.json)" \
  --role="roles/datastore.user"
#     (보안상 한 번성 작업이면 마이그레이션 종료 후 remove-iam-policy-binding 으로 회수 권장.
#      또는 전용 migration SA / 본인 계정 ADC 에 동일 역할을 부여해 사용.)
#
#   현재 사용 중인 신원/프로젝트는 compare_firestore.py 가 시작 시 로그로 출력한다.

# 4) Firestore 복합 인덱스 선반영 (빌드에 시간이 걸리므로 미리)
firebase deploy --only firestore:indexes --project draw-my-today

# 5) (권장) 롤백 대비 현재 Firestore 스냅샷 백업
gcloud firestore export gs://<백업버킷>/pre-migration-$(date +%Y%m%d) --project draw-my-today
```

환경변수 레퍼런스:

| 변수 | 용도 | 값 |
|---|---|---|
| `GOOGLE_APPLICATION_CREDENTIALS` | Firestore 인증 | `./key.json` |
| `BULK_LOAD_FORCE_PRODUCTION` | 운영 적재 안전장치 해제 | `true` |
| `BULK_LOAD_DELTA_MODE` | 차분 모드(카운트 검증 생략) | `true` |
| `FIRESTORE_PROJECT_ID` | 운영 프로젝트 | `draw-my-today` |
| `FIRESTORE_DATABASE_ID` | 운영 DB (named DB — `(default)` 아님!) | `draw-my-today-db` |
| `SAMPLE_DATA_PATH` | 적재 입력 파일 | `./prod-data-delta.json` |

---

## 단계별 절차

전략: **지금 큰 차분을 미리 넣어 drift를 줄여두고(1~3단계), cutover 때 짧은 다운타임 동안
남은 작은 차분만 반영(4~6단계)** 한다.

baseline 체이닝(중요): 각 차분의 `--old`는 **직전에 적재한 추출본**이어야 다음 delta가 최소화된다.

```
원본 prod-data.json --[delta A]--> step2 추출본 --[delta B]--> step4 추출본(최종)
```

### Phase A — 사전 동기화 (서비스 가동 중)

#### 1. 현재 drift 정량화

```bash
cd ../temp/draw-my-today-script
uv run python extract_prod.py                 # → prod-data.json (새 추출)
uv run python compare_firestore.py --data ./prod-data.json --database draw-my-today-db
```

이 시점엔 불일치가 정상이다(마지막 적재 이후 밀린 양). 얼마나 밀렸는지 파악하는 단계다.
추출 로그의 `All FK integrity validations passed` 와 `metadata.totalCounts` 를 확인한다.

#### 2. 전체 재적재 (String 변환 + 사전 동기화)

스키마 전환(int→String)이므로 **delta가 아니라 full reload**다. `BULK_LOAD_DELTA_MODE`를
주지 않고 String 매퍼로 전체 적재하면, 같은 doc.id를 덮어쓰며 id 필드가 String으로 제자리
전환된다. (이 시점엔 신규 백엔드가 운영 Firestore를 read/write하지 않으므로 안전.)

```bash
cp ./prod-data.json /Users/qraft/work/ai-paint-today-BE/

cd /Users/qraft/work/ai-paint-today-BE
BULK_LOAD_FORCE_PRODUCTION=true \
FIRESTORE_PROJECT_ID=draw-my-today \
FIRESTORE_DATABASE_ID=draw-my-today-db \
GOOGLE_APPLICATION_CREDENTIALS=./key.json \
SAMPLE_DATA_PATH=./prod-data.json \
./gradlew bootRunBulkLoad
#   끝에 "=== All verifications PASSED ===" 확인 (full reload는 카운트 검증도 수행)
```

> **이 `prod-data.json`(step2 추출본)을 step4 delta의 baseline으로 보관한다.**
> 데이터량(수십만 문서)에 따라 수~수십 분 소요될 수 있다.

#### 3. 동기화 검증

```bash
cd ../temp/draw-my-today-script
uv run python compare_firestore.py --data ./prod-data.json --database draw-my-today-db
```

`=== SYNC OK ===` (exit 0) 이어야 한다. 비교 입력은 **delta 파일이 아니라 step2의 full
추출본** `prod-data.json` 이다(delta를 넘기면 기존 diary 전부가 MISSING으로 잡힘).

### Phase B — Cutover (서비스 중단)

#### 4. 서비스 중단 후 최종 차분 적재

```bash
# (1) 일기앱을 점검/중단 페이지로 전환 — 이 시점 이후 RDS write가 없어야 함

# (2) 최종 추출 (반드시 write 중단 이후)
cd ../temp/draw-my-today-script
uv run python extract_prod.py                 # → prod-data.json (최종)

# (3) 차분 B 생성 (baseline = step2 추출본)
uv run python build_delta.py \
  --old /path/to/step2-prod-data.json \
  --new ./prod-data.json \
  --out ./prod-data-delta.json
cp ./prod-data-delta.json /Users/qraft/work/ai-paint-today-BE/

# (4) 적재
cd /Users/qraft/work/ai-paint-today-BE
BULK_LOAD_DELTA_MODE=true BULK_LOAD_FORCE_PRODUCTION=true \
FIRESTORE_PROJECT_ID=draw-my-today FIRESTORE_DATABASE_ID=draw-my-today-db \
GOOGLE_APPLICATION_CREDENTIALS=./key.json \
SAMPLE_DATA_PATH=./prod-data-delta.json \
./gradlew bootRunBulkLoad
```

#### 5. 최종 검증

```bash
cd ../temp/draw-my-today-script
uv run python compare_firestore.py --data ./prod-data.json --database draw-my-today-db   # === SYNC OK === 확인
```

#### 6. 새 앱 배포 후 서비스 재개

- **새 앱(String id 코드) 배포가 이 재적재와 짝이다.** String 변환된 운영 데이터는 String id
  코드로만 정상 쿼리된다. cutover 창에서 step4(String 재적재) 완료 후, String id 코드가 담긴
  이미지로 운영 백엔드를 배포한 뒤 트래픽을 연다. (구 앱이 붙어 있으면 Long으로 쿼리해 깨짐)
- 신규(Firestore) 백엔드가 **`TZ=Asia/Seoul`** 적용 + String id 코드 이미지인지 확인:
  ```bash
  gcloud run services describe <서비스명> --region asia-northeast3 \
    --format="yaml(spec.template.spec.containers[0].env)"
  ```
- **`JWT_SECRET` 결정**: 기존 운영 값을 **그대로 유지**하면 재로그인 없이 전환된다(앱 JWT는
  무상태라 secret만 같으면 기존 토큰 유효). 새 값으로 바꾸면 전원 재로그인이 발생한다.
- 백엔드 URL을 신규 Firestore 백엔드로 전환하고 서비스 재개.
- 스모크 테스트: 로그인 / 토큰 refresh(`/oauth2/refresh`) / 월간·단건 일기 조회 / 일기 생성
  (신규 write가 Firestore에 적재되는지) / 티켓 화면.

---

## 검증 스크립트가 보는 것

`compare_firestore.py` 는 **추출 스냅샷(prod-data.json)** 과 **Firestore 실제 상태** 를 대조한다.

- 비교 항목: 전체 users/diaries/images 총개수, **유저당 diary 수**, **diary당 image 수**
  (diary 문서의 비정규화 `imageCount` 정합까지 검증).
- 로더와 동일 필터 적용: `is_test=false` 제외, soft-deleted 포함.
- 리포트(양방향):
  - `MISSING` — JSON엔 있으나 Firestore에 없음/부족 → 적재 미반영. delta 적재 재실행.
  - `EXCESS` — Firestore에만 있음 → hard-delete 잔재 또는 이상 write. 수동 확인.
  - `MISMATCH` — 개수 불일치.
- 종료 코드: SYNC OK → `0`, 불일치 → `1`.

---

## 롤백

전환 직후 치명 이슈 발견 시 **백엔드 URL을 구 백엔드로 되돌린다.** RDS는 Phase B 중단 이후
write가 없었으므로 데이터 손실 없이 즉시 복구된다. Firestore 자체가 오염된 경우 사전 준비
5번의 export 스냅샷으로 import 복구한다.

---

## 고려사항 요약

| 항목 | 핵심 |
|---|---|
| ID 스키마 | id 필드 int→String 전환(emotionId만 int 유지). 앱 String 코드 배포와 String 재적재를 **같은 창에서 함께**. 전환은 full reload(혼재 방지) |
| 자격증명/IAM | GAC export 필수(미설정 시 사용자 ADC 폴백 → 403). 신원에 `roles/datastore.user` 필요(배포 전용 SA엔 없음) |
| DB id | 운영=`draw-my-today-db`, dev=`draw-my-today-db-dev` (둘 다 `(default)` 아님) |
| 순서 엄수 | 최종 추출은 반드시 서비스 중단(=RDS write 동결) 이후 |
| Firestore writer | cutover 전까지 운영 Firestore에 쓰는 주체가 없어야 함 |
| baseline 체이닝 | 각 delta의 `--old`는 직전 적재 추출본. 매 추출본을 다음 baseline으로 보관 |
| 비교 입력 | delta 파일이 아니라 적재에 쓴 full 추출본을 `--data`로 |
| 멱등성 | 재실행/실패 복구 안전. baseline 실수해도 데이터 안전(느려질 뿐) |
| 삭제 동기화 | soft delete는 자동 전파. hard delete는 `EXCESS`로 수동 처리 |
| ticket/adReward | 미적재. 기존 데이터는 보존 |
| TZ | 신규 백엔드는 `TZ=Asia/Seoul` 이미지여야 시간 정합(bulk load는 KST 고정) |
| JWT_SECRET | 기존 유지 = 무중단 / 변경 = 전원 재로그인 |
| 인덱스 | 복합 인덱스 미리 deploy(빌드 시간 고려) |
| 대용량 비교 | `collection_group("images")` 전체 스트리밍은 시간이 걸릴 수 있음(일회성) |
