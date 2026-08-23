package com.dajeom.diagnostic;

import com.dajeom.type.ConsultationCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 사주 장문 Story Generation 전용 진단 Logger.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * OpenAI Story Generation 과정에서 발생하는
 * 요청/응답/파싱/fallback 상태를
 * 애플리케이션 로그에서 추적하기 위한 전용 컴포넌트다.
 *
 * 이 클래스는:
 *
 * - 사주 계산을 하지 않는다.
 * - Prompt를 변경하지 않는다.
 * - OpenAI 요청을 수행하지 않는다.
 * - Story Generation 결과를 변경하지 않는다.
 *
 * 오직 진단용 로그만 담당한다.
 *
 * =================================================
 * 보안 원칙
 * =================================================
 *
 * 다음 정보는 로그에 출력하지 않는다.
 *
 * - OPENAI_API_KEY
 * - Authorization Header
 * - 전체 Prompt JSON
 * - 전체 OpenAI Response Body
 * - 전체 storyText
 * - 사용자의 생년월일 원문
 * - 사용자의 출생시간 원문
 *
 * 대신 다음과 같은 안전한 메타정보만 기록한다.
 *
 * - generationId
 * - model
 * - category
 * - available / unavailable 개수
 * - HTTP Response Body 길이
 * - OpenAI status
 * - storyText 길이
 * - elapsedMillis
 * - Exception Type
 * - Exception Message
 * - Timeline Year Contract 구성 상태
 *
 * =================================================
 * Generation ID
 * =================================================
 *
 * 동시에 여러 사주 분석 요청이 들어올 수 있으므로
 * 하나의 Story Generation 실행에 고유 번호를 부여한다.
 *
 * 예:
 *
 * STORY-1
 * STORY-2
 * STORY-3
 *
 * 이를 통해 여러 HTTP 요청의 로그가 섞여도
 * 같은 Story Generation 작업을 구분할 수 있다.
 */
@Component
public class StoryGenerationDiagnosticLogger {

    private static final Logger log =
            LoggerFactory.getLogger(
                    StoryGenerationDiagnosticLogger.class
            );

    private static final String LOG_PREFIX =
            "[STORY-DIAG]";

    private static final AtomicLong
            GENERATION_SEQUENCE =
            new AtomicLong(0L);


    /**
     * 새로운 Story Generation 진단 ID를 생성한다.
     *
     * @return STORY-1 형태의 Generation ID
     */
    public String nextGenerationId() {

        long sequence =
                GENERATION_SEQUENCE
                        .incrementAndGet();

        return "STORY-" + sequence;
    }


    /**
     * 전체 Story Generation 시작.
     */
    public void generationStarted(

            String generationId,

            String model,

            int availableCount,

            int unavailableCount

    ) {

        log.info(
                "{} generation-start generationId={}, model={}, available={}, unavailable={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                Math.max(
                        availableCount,
                        0
                ),
                Math.max(
                        unavailableCount,
                        0
                )
        );
    }


    /**
     * 특정 Category Story 생성 시작.
     */
    public void categoryStarted(

            String generationId,

            String model,

            ConsultationCategory category

    ) {

        log.info(
                "{} category-start generationId={}, model={}, category={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                )
        );
    }


    /**
     * Timeline Year Deterministic Contract 상태 진단.
     *
     * =================================================
     * 목적
     * =================================================
     *
     * PAST_LUCK / FUTURE_LUCK의 각 명리연도에
     * 공식 Deterministic Contract가 실제로 존재하는지와
     * 그 구성요소의 존재 여부를 로그로 확인한다.
     *
     * 이 메서드는:
     *
     * - 연도 판정을 다시 계산하지 않는다.
     * - BIG_FLOW를 다시 계산하지 않는다.
     * - PRIMARY 영역을 다시 선택하지 않는다.
     * - CROSS_DOMAIN_PATTERN을 다시 계산하지 않는다.
     * - TURNING_POINT를 다시 계산하지 않는다.
     *
     * 이미 Java에서 계산된 Contract 상태만 기록한다.
     */
    public void timelineYearContractState(

            String generationId,

            String model,

            ConsultationCategory category,

            int sajuYear,

            boolean contractExists,

            String overallNarrativeStatus,

            String bigFlowStatus,

            boolean primaryActiveDomainExists,

            boolean primaryPressureDomainExists,

            boolean crossDomainPatternExists,

            boolean turningPointExists,

            int evidenceCount,

            boolean timelineUncertaintyExists

    ) {

        log.info(
                "{} timeline-year-contract "
                        + "generationId={}, "
                        + "model={}, "
                        + "category={}, "
                        + "sajuYear={}, "
                        + "contractExists={}, "
                        + "overallNarrativeStatus={}, "
                        + "bigFlowStatus={}, "
                        + "primaryActiveDomainExists={}, "
                        + "primaryPressureDomainExists={}, "
                        + "crossDomainPatternExists={}, "
                        + "turningPointExists={}, "
                        + "evidenceCount={}, "
                        + "timelineUncertaintyExists={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                sajuYear,
                contractExists,
                normalizeText(
                        overallNarrativeStatus
                ),
                normalizeText(
                        bigFlowStatus
                ),
                primaryActiveDomainExists,
                primaryPressureDomainExists,
                crossDomainPatternExists,
                turningPointExists,
                Math.max(
                        evidenceCount,
                        0
                ),
                timelineUncertaintyExists
        );
    }


    /**
     * OpenAI HTTP 응답 수신.
     *
     * Response Body 자체는 출력하지 않는다.
     * Body 길이만 기록한다.
     */
    public void responseReceived(

            String generationId,

            String model,

            ConsultationCategory category,

            int responseBodyLength,

            long elapsedMillis

    ) {

        log.info(
                "{} response-received generationId={}, model={}, category={}, bodyLength={}, elapsedMs={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                Math.max(
                        responseBodyLength,
                        0
                ),
                normalizeElapsedMillis(
                        elapsedMillis
                )
        );
    }


    /**
     * OpenAI Response의 status 확인.
     *
     * completed / incomplete / failed 등의
     * 상태 문자열만 기록한다.
     */
    public void responseStatus(

            String generationId,

            String model,

            ConsultationCategory category,

            String status

    ) {

        log.info(
                "{} response-status generationId={}, model={}, category={}, status={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                normalizeText(
                        status
                )
        );
    }


    /**
     * Structured Output의 JSON text를 추출한 상태.
     *
     * 실제 JSON 문자열은 로그에 남기지 않고
     * 길이만 기록한다.
     */
    public void structuredOutputExtracted(

            String generationId,

            String model,

            ConsultationCategory category,

            int outputLength

    ) {

        log.info(
                "{} structured-output generationId={}, model={}, category={}, outputLength={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                Math.max(
                        outputLength,
                        0
                )
        );
    }


    /**
     * Category Story 생성 성공.
     */
    public void categorySucceeded(

            String generationId,

            String model,

            ConsultationCategory category,

            int storyLength,

            long elapsedMillis

    ) {

        log.info(
                "{} category-success generationId={}, model={}, category={}, storyLength={}, elapsedMs={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                Math.max(
                        storyLength,
                        0
                ),
                normalizeElapsedMillis(
                        elapsedMillis
                )
        );
    }


    /**
     * Story 분량이 목표 범위를 벗어났을 때.
     *
     * 생성 자체는 성공했지만
     * 품질 진단을 위해 WARN으로 기록한다.
     */
    public void storyLengthOutOfRange(

            String generationId,

            String model,

            ConsultationCategory category,

            int storyLength,

            int minimumLength,

            int maximumLength

    ) {

        log.warn(
                "{} story-length-warning generationId={}, model={}, category={}, storyLength={}, targetMin={}, targetMax={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                Math.max(
                        storyLength,
                        0
                ),
                Math.max(
                        minimumLength,
                        0
                ),
                Math.max(
                        maximumLength,
                        0
                )
        );
    }


    /**
     * 특정 Category Story 생성 실패.
     *
     * Exception 객체 자체를 마지막 인수로 넘기므로
     * Stack Trace까지 자동 출력된다.
     *
     * 단,
     * API Key나 Prompt 전문은 Exception Message에
     * 직접 포함시키지 않는 것이 전제다.
     */
    public void categoryFailed(

            String generationId,

            String model,

            ConsultationCategory category,

            long elapsedMillis,

            Throwable exception

    ) {

        Objects.requireNonNull(
                exception,
                "진단 로그에는 Throwable이 필요합니다."
        );

        log.error(
                "{} category-failure generationId={}, model={}, category={}, elapsedMs={}, exceptionType={}, message={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeCategory(
                        category
                ),
                normalizeElapsedMillis(
                        elapsedMillis
                ),
                exception
                        .getClass()
                        .getName(),
                normalizeExceptionMessage(
                        exception
                ),
                exception
        );
    }


    /**
     * 전체 Story Generation 성공.
     */
    public void generationSucceeded(

            String generationId,

            String model,

            int generatedCount,

            long elapsedMillis

    ) {

        log.info(
                "{} generation-success generationId={}, model={}, generated={}, elapsedMs={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                Math.max(
                        generatedCount,
                        0
                ),
                normalizeElapsedMillis(
                        elapsedMillis
                )
        );
    }


    /**
     * Prompt Context는 있었지만
     * 실제 생성 대상 Category가 없는 경우.
     */
    public void generationSkippedNoAvailableCategory(

            String generationId,

            String model

    ) {

        log.info(
                "{} generation-skip generationId={}, model={}, reason=NO_AVAILABLE_CATEGORY",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                )
        );
    }


    /**
     * Category Resolution이 필요한 상태라
     * 외부 Story Generation을 수행하지 않는 경우.
     */
    public void generationSkippedCategoryResolutionRequired(

            String generationId,

            String model

    ) {

        log.info(
                "{} generation-skip generationId={}, model={}, reason=CATEGORY_RESOLUTION_REQUIRED",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                )
        );
    }


    /**
     * Prompt Context에 Section이 없는 경우.
     */
    public void generationSkippedNoSections(

            String generationId,

            String model

    ) {

        log.info(
                "{} generation-skip generationId={}, model={}, reason=NO_PROMPT_SECTIONS",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                )
        );
    }


    /**
     * API Key가 설정되지 않아
     * 외부 LLM 호출을 건너뛴 경우.
     *
     * API Key 값 자체는 절대 출력하지 않는다.
     */
    public void generationSkippedMissingApiKey(

            String generationId,

            String model

    ) {

        log.warn(
                "{} generation-skip generationId={}, model={}, reason=MISSING_API_KEY",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                )
        );
    }


    /**
     * 전체 외부 Story Generation 결과가 비어
     * 생성 결과를 반환할 수 없는 경우.
     */
    public void generationFallback(

            String generationId,

            String model,

            String reason

    ) {

        log.warn(
                "{} generation-fallback generationId={}, model={}, reason={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                normalizeReason(
                        reason
                )
        );
    }


    /**
     * 생성 결과가 실제 Response Story에 적용되는 경우.
     */
    public void generationApplied(

            String generationId,

            String model,

            int generatedCount

    ) {

        log.info(
                "{} generation-applied generationId={}, model={}, generated={}",
                LOG_PREFIX,
                normalizeGenerationId(
                        generationId
                ),
                normalizeText(
                        model
                ),
                Math.max(
                        generatedCount,
                        0
                )
        );
    }


    /*
     * =================================================
     * Utility
     * =================================================
     */

    private static String normalizeGenerationId(
            String generationId
    ) {

        if (
                generationId == null
                        ||
                        generationId.isBlank()
        ) {

            return "UNKNOWN";
        }

        return generationId.trim();
    }


    private static String normalizeText(
            String value
    ) {

        if (
                value == null
                        ||
                        value.isBlank()
        ) {

            return "UNKNOWN";
        }

        return sanitize(
                value
        );
    }


    private static String normalizeCategory(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return "UNKNOWN";
        }

        return category.name();
    }


    private static long normalizeElapsedMillis(
            long elapsedMillis
    ) {

        return Math.max(
                elapsedMillis,
                0L
        );
    }


    private static String normalizeExceptionMessage(
            Throwable exception
    ) {

        String message =
                exception.getMessage();

        if (
                message == null
                        ||
                        message.isBlank()
        ) {

            return "NO_MESSAGE";
        }

        return sanitize(
                message
        );
    }


    private static String normalizeReason(
            String reason
    ) {

        if (
                reason == null
                        ||
                        reason.isBlank()
        ) {

            return "UNKNOWN";
        }

        return sanitize(
                reason
        );
    }


    /**
     * 로그 한 줄이 깨지지 않도록
     * 개행문자를 제거한다.
     *
     * 지나치게 긴 Exception Message가
     * 콘솔 전체를 덮는 것도 방지한다.
     */
    private static String sanitize(
            String value
    ) {

        String sanitized =
                value
                        .replace(
                                '\r',
                                ' '
                        )
                        .replace(
                                '\n',
                                ' '
                        )
                        .trim();

        int maximumLength =
                800;

        if (
                sanitized.length()
                        <= maximumLength
        ) {

            return sanitized;
        }

        return sanitized.substring(
                0,
                maximumLength
        ) + "...";
    }
}