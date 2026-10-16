# dajeom · 다점

Spring Boot 4.1 / Java 17 기반 사주·타로 웹 서비스입니다. 기존 화면과 영상으로 입력부터 풀이까지 이어집니다.

## 기능

- `/`: 홈, 사주·타로 진입
- `/saju`: 이름 → 양력·음력 생년월일 → 출생시간 → 성별 → 사주팔자·오행 → 7개 풀이
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

## Docker 실행

```sh
docker build -t dajeom .
docker run --rm -p 8080:8080 --env-file .env dajeom
```

키는 이미지에 포함하지 않습니다. 호스팅 환경에서는 환경변수로 주입합니다. 공개 배포 시 HTTPS와 API 사용량 제한은 호스팅의 프록시·접근 제어 정책에 맞춰 설정하세요.

## API

`POST /api/saju/calculate`: `calendarType` (`SOLAR`/`LUNAR`), `birthDate` (`YYYY-MM-DD`), `birthTime` (`HH:mm` 또는 `null`), `gender` (`MALE`/`FEMALE`), `leapMonth`를 받습니다. 응답은 `pillars`, `fiveElements`, 순서가 있는 `interpretations` (`order`, `title`, `content`, `status`)입니다.

`POST /api/tarot/reading`: `spreadId`, `cards` (`id`: 0–77, `reversed`: boolean), 선택적인 `name`, `question`, `relationshipStatus`, `jobStatus`를 받습니다. 서버는 배열법과 카드 수·중복·범위를 검증하고 카드 이름·위치와 `content`를 반환합니다.

API 키는 브라우저에 전달하지 않습니다. 사용자별 계정·결제·서버 측 결과 보관은 구현 범위에 포함하지 않습니다.
