# dajeom · 다점

Spring Boot 4.1 / Java 17 기반 사주·타로 웹 서비스입니다. 고민을 고르고 현재 상황을 알려주면, 계산된 사주 근거로 대화형 상담을 이어갑니다.

## 기능

- `/`: 홈, 사주·타로 진입
- `/saju`: 고민 선택 → 질문·현재 상황 입력 → 생년월일 → 맞춤 첫 답변 → 후속 질문
- 고민: 나 알아보기, 연애·관계, 취업·이직, 돈·일상, 인간관계, 마음·생활. 계산 근거가 있으면 지나온 흐름으로 주제를 바꿀 수 있습니다.
- 답변 길이 선택(핵심만/차분히/자세히), 사주팔자·오행 패널, 후속 질문 추천, 요약 공유
- 선택한 경우에만 기기에 상담 기록 저장(최근 5개), 저장 기록 열람·삭제
- 전체 풀이 버튼에서 7개 장문을 필요할 때 생성하며, 같은 세션에서 재요청하면 결과를 재사용합니다.
- `/saju/report`: 기존 영상형 종합 사주 화면
- `/tarot`: 상담 정보 → 9가지 배열법 → 78장 중 카드 선택 → 정·역방향 AI 풀이 → 다시 뽑기
- 사주 풀이: 성향·기질, 지나온 흐름, 가족·인간관계, 연애·결혼, 재물, 일·성취, 건강·생활
- 한국 음력·윤달과 절기 계산은 Time4J를 사용합니다.

출생시간을 모르면 세 기둥과 여섯 글자의 오행을 표시하고 장문풀이는 생성하지 않습니다. 근거가 부족해 생성이 차단된 항목은 `UNAVAILABLE`로 표시하며, 다른 항목의 실제 생성 결과는 유지합니다. AI가 계산되지 않은 영역을 임의로 채우지 않습니다.

## 실행

Java 17이 필요합니다. MySQL은 필요하지 않습니다. API 키는 서버의 `OPENAI_API_KEY` 환경변수 또는 Git에서 제외된 프로젝트 루트 `.env`에 설정합니다. `.env.example`은 변수 이름을 보여주는 예시입니다. `OPENAI_MODEL`로 모델을 바꿀 수 있습니다.

```powershell
.\gradlew.bat bootRun
```

macOS/Linux에서는 `./gradlew bootRun`을 사용합니다. 기본 주소는 `http://localhost:8080`이며 `PORT` 환경변수로 포트를 바꿀 수 있습니다. 기존 외부 설정 파일은 Spring Boot의 `--spring.config.additional-location`으로 읽을 수 있습니다.

## 검증·패키징

```powershell
.\gradlew.bat test bootJar
java -jar build/libs/dajeom-0.0.1-SNAPSHOT.jar
```

자동 테스트는 외부 AI를 모킹하며 API 키나 DB 없이 실행됩니다. 양력·음력, 출생시간 모름, 잘못된 날짜, 페이지 경로, 카드 중복·범위 검증과 풀이 응답을 확인합니다. GitHub Actions에서도 같은 검증을 실행합니다.

대화형 상담은 첫 답변에 AI를 한 번 호출합니다. 사주 계산 결과와 검증된 해석 재료를 서버 세션에 보관하고 후속 질문에 재사용합니다. 사용자 입력이나 이전 AI 답변을 새로운 명리 근거로 사용하지 않습니다. 세운·월운·일운·두 사람 궁합 계산은 제공하지 않으며, 특정 날짜의 예측을 임의로 생성하지 않도록 지시합니다.

세션은 최대 1시간, 첫 답변을 포함해 12회까지 사용할 수 있습니다. 서버 메모리 세션이므로 재시작하면 사라집니다. 실패한 답변은 기록·횟수에 반영하지 않습니다. 자동 테스트는 단일 호출, 대화 연속성·세션 분리, 실패 후 재시도, 차단 주제, 입력 길이, 질문 횟수, 전체 풀이 캐시도 확인합니다.

## Docker 실행

```sh
docker build -t dajeom .
docker run --rm -p 8080:8080 --env-file .env dajeom
```

키는 이미지에 포함하지 않습니다. 호스팅 환경에서는 환경변수로 주입합니다. 공개 배포 시 HTTPS와 API 사용량 제한은 호스팅의 프록시·접근 제어 정책에 맞춰 설정하세요.

## API

- `POST /api/saju/consultations`: `birth`(기존 사주 입력), `name`, `topic`, `depth`, 선택적인 `situation`과 `question`으로 상담을 시작합니다. `topic`은 `SELF`, `LOVE`, `CAREER`, `WEALTH`, `RELATIONSHIPS`, `WELLBEING`, `PAST`; `depth`는 `SHORT`, `STANDARD`, `DEEP`입니다.
- `POST /api/saju/consultations/messages`: `message`, `topic`, `depth`로 후속 질문을 보냅니다. 클라이언트가 사주 근거나 이전 대화를 덮어쓸 수 없습니다.
- `GET /api/saju/consultations/current`: 현재 세션의 상담을 읽습니다. 상담이 없으면 204를 반환합니다.
- `POST /api/saju/consultations/report`: 같은 계산 결과로 7개 전체 풀이를 지연 생성·캐시합니다.
- `DELETE /api/saju/consultations/current`: 현재 서버 세션의 상담을 삭제합니다.

상담 API 응답에는 공개 사주·오행, 실제 대화, 생성 가능 주제, 남은 질문 횟수가 포함됩니다. 서버의 원본 해석 재료나 API 키는 응답에 포함하지 않습니다. 세션 만료는 410, 동시 요청은 409, 횟수 초과는 429, 외부 생성 오류는 503을 반환합니다.

`POST /api/saju/calculate`: `calendarType` (`SOLAR`/`LUNAR`), `birthDate` (`YYYY-MM-DD`), `birthTime` (`HH:mm` 또는 `null`), `gender` (`MALE`/`FEMALE`), `leapMonth`를 받습니다. 응답은 `pillars`, `fiveElements`, 순서가 있는 `interpretations` (`order`, `title`, `content`, `status`)입니다.

`POST /api/tarot/reading`: `spreadId`, `cards` (`id`: 0–77, `reversed`: boolean), 선택적인 `name`, `question`, `relationshipStatus`, `jobStatus`를 받습니다. 서버는 배열법과 카드 수·중복·범위를 검증하고 카드 이름·위치와 `content`를 반환합니다.

API 키는 브라우저에 전달하지 않습니다. 사용자별 계정·결제·DB의 영구적인 결과 보관은 구현 범위에 포함하지 않습니다. 기기 저장은 현재 브라우저의 localStorage이며 다른 기기와 동기화되지 않습니다. 생년월일 원본은 저장 기록과 공유 데이터에 직접 포함하지 않습니다.
