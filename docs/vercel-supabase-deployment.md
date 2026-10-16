# Vercel + Supabase 배포

기존 Java 17 / Spring Boot 서버를 Vercel Container Runtime으로 배포합니다. 화면, 영상, 카드 이미지와 사주 계산 엔진은 유지합니다. Container Runtime은 현재 베타입니다.

## 1. Supabase 프로젝트 만들기

1. https://supabase.com/dashboard 에서 프로젝트를 만듭니다. 한국 사용자가 중심이면 Seoul 리전을 선택할 수 있습니다.
2. SQL Editor에서 `supabase/migrations/202610160001_hosted_state.sql` 전체를 실행합니다.
3. Supabase Cron(`pg_cron`)을 활성화한 다음 `supabase/cleanup.sql`을 한 번 실행합니다. 만료된 사주 정보·고민·대화와 잠금·호출 횟수를 10분마다 삭제합니다.
4. 프로젝트 URL과 **서버용 secret key**(`sb_secret_...`)를 확인합니다. 브라우저용 publishable/anon key를 서버 비밀키 대신 넣지 마세요.

기본 테이블은 RLS를 켜고 `anon`/`authenticated` 접근을 차단합니다. Java 서버만 비밀키로 테이블과 제한된 RPC를 사용합니다. 이 구성은 Supabase Auth 회원가입을 구현한 것이 아닙니다.

`supabase/tests/`는 일회용 CI 데이터베이스 전용입니다. 실제 프로젝트에는 실행하지 않습니다.

## 2. Vercel 프로젝트 연결

1. Vercel에서 `cjw25/dajeom` 저장소를 Import합니다.
2. Root Directory는 저장소 루트로 둡니다. `Dockerfile.vercel`을 자동 감지하는 Container Runtime을 사용합니다. 일반 정적 사이트나 Next.js로 지정하지 않습니다.
3. 우선 `fix/complete-dajeom` 브랜치를 Preview 배포해 확인합니다. 현재 변경이 병합되기 전에는 기본 브랜치 배포에 포함되지 않습니다.
4. 아래 환경변수를 Preview와 Production에 각각 설정합니다. 값을 설정한 뒤 다시 배포합니다.

| 변수 | 값 |
|---|---|
| `SUPABASE_URL` | 만든 프로젝트의 `https://프로젝트ID.supabase.co` |
| `SUPABASE_SECRET_KEY` | Supabase 서버용 `sb_secret_...` 키 |
| `OPENAI_API_KEY` | 기존에 사용하던 API 키 |
| `OPENAI_MODEL` | `gpt-4o-mini` 또는 현재 사용 중인 모델 |
| `PORT` | `8080` |
| `SPRING_PROFILES_ACTIVE` | `vercel` (이미 이미지 기본값으로 지정) |

키에 `NEXT_PUBLIC_` 같은 공개 접두사를 붙이지 않습니다. 키를 GitHub에 커밋하거나 채팅에 붙일 필요가 없습니다. Preview와 Production의 데이터까지 분리하려면 각각 다른 Supabase 프로젝트를 사용합니다.

컨테이너의 HTTP 포트는 Vercel 프로젝트에서도 `PORT=8080`으로 지정해야 합니다. Settings → Functions → Function Max Duration에서 Default Max Duration을 300초로 확인하고 저장합니다. `Dockerfile.vercel`은 자동 감지되므로 `vercel.json`의 `functions` 패턴에 추가하지 않습니다. 해당 패턴은 서버리스 함수와 매칭되어 `unused_function` 오류를 일으킬 수 있습니다. Vercel 프로필은 외부 AI 요청 하나의 타임아웃을 20초로 제한해 장별 작성·검증·제한된 재시도를 요청 시간 안에 수행하도록 구성합니다.

## 3. 배포 후 확인

1. `/saju`와 `/tarot`이 열리고 이미지·영상·78장 앞면 파일을 제공하는지 확인합니다.
2. 사주 정보와 고민을 입력하면 `POST /api/saju/consultations/reading`이 HTTP 202와 진행 상태를 반환합니다. 이 시점에는 AI를 호출하지 않습니다.
3. 브라우저가 `POST /api/saju/consultations/reading/advance`를 순서대로 호출합니다. 각 요청은 다음 한 장을 작성하고 Supabase에 저장한 뒤 응답합니다.
4. 작성 중 새로고침하고 ‘이어서 읽기’를 누르면 저장된 장 다음부터 작성합니다. 새 서버 인스턴스에서도 브라우저 쿠키가 같으면 복원합니다.
5. 18개 기본 장과 선택적인 마지막 고민 풀이가 모두 완성되면 결과를 보여줍니다. 추가 질문 후 새로고침해 대화가 복원되는지도 확인합니다.
6. 다른 브라우저에서는 이전 사용자의 결과가 보이지 않아야 합니다. ‘작성 취소’는 서버에 저장된 진행 중 풀이를 삭제합니다.

창을 닫으면 다음 장을 요청하지 않으므로 작성을 일시 중단합니다. 응답을 반환한 뒤 별도 스레드에서 생성하는 방식이나 외부 큐 작업자는 사용하지 않습니다. 마지막으로 실행 중이던 요청이 끝나 저장되면 그 지점까지 남고, 강제 종료되면 저장되지 못한 한 장부터 재시도합니다. 실행 중 요청의 잠금은 최대 5분 후 해제됩니다.

저장된 사주·고민·대화는 시작 후 1시간까지만 조회·이용할 수 있습니다. Cron 설정 시 만료 행을 10분 간격으로 물리 삭제합니다. Supabase 백업 보존은 별도의 프로젝트 설정을 따릅니다. 기기 보관은 사용자가 선택한 최근 5개 결과를 기존처럼 localStorage에 저장합니다. 다른 기기로 동기화되지 않습니다.

## 공개 API 기본 호출 제한

한국 날짜 기준의 제한을 Supabase RPC에서 원자적으로 적용합니다. IP는 해시로만 저장합니다. 제한 초과는 429, 저장소가 연결되지 않으면 503이며 AI 호출은 진행하지 않습니다.

| 요청 | IP당 / 일 | 전체 / 일 |
|---|---:|---:|
| 새 종합풀이 접수 | 10 | 100 |
| 장별 작성·재시도 | 400 | 4,000 |
| 추가 상담 | 60 | 1,000 |
| 타로 풀이 | 60 | 1,000 |

이는 요청 횟수 제한이며 금액 한도를 보장하지는 않습니다. 운영 사용량에 맞춰 `HostedVisitorFilter`의 제한을 조정하고 API 제공자의 사용량도 확인합니다. Vercel 프로필에서는 이전의 일괄 사주 생성 API를 막아 장별 저장 경로를 거치도록 합니다.

## 검증 범위

로컬 테스트는 Supabase REST를 흉내 낸 HTTP 서버로 실제 저장 클라이언트, 쿠키 격리, 새 서비스 인스턴스 복원, 장별 재시도, 잠금과 대화 복원을 검증합니다. 실제 PostgreSQL 마이그레이션·권한·잠금·예산 함수와 컨테이너 빌드는 GitHub Actions에서 별도로 검사합니다. 실제 계정 배포와 Supabase 연결 확인은 프로젝트를 만든 뒤 수행해야 합니다.

## 공식 참고 자료

- Vercel Container Runtime: https://vercel.com/docs/functions/runtimes/container
- Supabase 서버 키: https://supabase.com/docs/guides/getting-started/api-keys
- Supabase API 접근 제어: https://supabase.com/docs/guides/api/securing-your-api
- Supabase 리전: https://supabase.com/docs/guides/platform/regions
