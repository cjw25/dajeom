package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range 확장.
 *
 * 하나의 ConsultationCategory에 대한
 * LLM Grounding Section.
 *
 * =================================================
 *
 * natalEvidences
 *
 * 원국 Grounding Evidence.
 *
 * =================================================
 *
 * timelineScenarios
 *
 * 현재 조회시점의 가능한 Timeline Segment.
 *
 * =================================================
 *
 * timelineYears
 *
 * PAST_LUCK / FUTURE_LUCK 전용
 * 실제 Timeline Range의 연도별 Grounding.
 *
 * =================================================
 *
 * unavailableReason
 *
 * deterministic 계산 범위에서
 * 아직 해당 Category를 만들 수 없는 경우.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 현재 Segment Scenario와
 * 과거/미래 Range Year를
 * 같은 개념으로 합치지 않는다.
 */
public record ConsultationGroundingSection(

        ConsultationCategory category,

        List<ConsultationGroundingEvidence>
        natalEvidences,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios,

        List<ConsultationGroundingTimelineYear>
        timelineYears,

        Optional<String> unavailableReason

) {

    /*
     * =================================================
     * 기존 20차 호출 호환 Constructor
     * =================================================
     */

    public ConsultationGroundingSection(

            ConsultationCategory category,

            List<ConsultationGroundingEvidence>
                    natalEvidences,

            List<ConsultationGroundingTimelineScenario>
                    timelineScenarios,

            Optional<String> unavailableReason

    ) {

        this(
                category,
                natalEvidences,
                timelineScenarios,
                List.of(),
                unavailableReason
        );
    }

    public ConsultationGroundingSection {

        Objects.requireNonNull(
                category,
                "Grounding Section의 ConsultationCategory는 필수입니다."
        );

        natalEvidences =
                normalizeNatalEvidences(
                        natalEvidences
                );

        timelineScenarios =
                normalizeTimelineScenarios(
                        timelineScenarios
                );

        timelineYears =
                normalizeTimelineYears(
                        timelineYears
                );

        unavailableReason =
                normalizeUnavailableReason(
                        unavailableReason
                );

        /*
         * =================================================
         * unavailable View 검증
         * =================================================
         */

        if (
                unavailableReason.isPresent()
                        && (
                        !natalEvidences.isEmpty()
                                || !timelineScenarios.isEmpty()
                                || !timelineYears.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Grounding Section에는 Evidence를 함께 넣을 수 없습니다: "
                            + category
            );
        }
    }

    /*
     * =================================================
     * 기본 정보
     * =================================================
     */

    public String title() {

        return category
                .getKoreanName();
    }

    /*
     * =================================================
     * Availability
     * =================================================
     */

    public boolean available() {

        return unavailableReason
                .isEmpty();
    }

    public boolean unavailable() {

        return !available();
    }

    /*
     * =================================================
     * Natal
     * =================================================
     */

    public boolean hasNatalEvidence() {

        return !natalEvidences
                .isEmpty();
    }

    public int natalEvidenceCount() {

        return natalEvidences
                .size();
    }

    /*
     * =================================================
     * Current Timeline Scenario
     * =================================================
     */

    public boolean hasTimelineScenarios() {

        return !timelineScenarios
                .isEmpty();
    }

    public int timelineScenarioCount() {

        return timelineScenarios
                .size();
    }

    /*
     * =================================================
     * Timeline Range Year
     * =================================================
     */

    public boolean hasTimelineYears() {

        return !timelineYears
                .isEmpty();
    }

    public int timelineYearCount() {

        return timelineYears
                .size();
    }

    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public boolean hasAnyTimeline() {

        return hasTimelineScenarios()
                || hasTimelineYears();
    }

    /**
     * 현재 Segment 또는
     * Range Year 중 하나라도
     * Timeline 불확실성을 가지면 true.
     */
    public boolean timelineUncertaintyExists() {

        boolean currentScenarioUncertainty =
                timelineScenarios
                        .stream()
                        .anyMatch(
                                ConsultationGroundingTimelineScenario
                                        ::timelineUncertaintyExists
                        );

        if (
                currentScenarioUncertainty
        ) {

            return true;
        }

        return timelineYears
                .stream()
                .anyMatch(
                        ConsultationGroundingTimelineYear
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * 전체 Evidence
     * =================================================
     *
     * 단순 조회용 연결이다.
     *
     * 여기서:
     *
     * - 합산
     * - 대표 polarity
     * - 점수화
     *
     * 하지 않는다.
     */

    public List<ConsultationGroundingEvidence>
    allEvidences() {

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>();

        result.addAll(
                natalEvidences
        );

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            result.addAll(
                    scenario.evidences()
            );
        }

        for (
                ConsultationGroundingTimelineYear timelineYear
                : timelineYears
        ) {

            result.addAll(
                    timelineYear.evidences()
            );
        }

        return List.copyOf(
                result
        );
    }

    public int evidenceCount() {

        return allEvidences()
                .size();
    }

    public boolean hasAnyEvidence() {

        return evidenceCount() > 0;
    }

    /*
     * =================================================
     * Normalize Natal
     * =================================================
     */

    private static List<ConsultationGroundingEvidence>
    normalizeNatalEvidences(

            List<ConsultationGroundingEvidence> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>();

        for (
                ConsultationGroundingEvidence evidence
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            evidence,
                            "Grounding Natal Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Normalize Current Timeline
     * =================================================
     */

    private static List<ConsultationGroundingTimelineScenario>
    normalizeTimelineScenarios(

            List<ConsultationGroundingTimelineScenario>
                    source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingTimelineScenario> result =
                new ArrayList<>();

        for (
                ConsultationGroundingTimelineScenario scenario
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            scenario,
                            "Grounding Timeline Scenario는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Normalize Timeline Year
     * =================================================
     */

    private static List<ConsultationGroundingTimelineYear>
    normalizeTimelineYears(

            List<ConsultationGroundingTimelineYear> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingTimelineYear> result =
                new ArrayList<>();

        for (
                ConsultationGroundingTimelineYear timelineYear
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            timelineYear,
                            "Grounding Timeline Year는 null일 수 없습니다."
                    )
            );
        }

        result.sort(
                java.util.Comparator
                        .comparingInt(
                                ConsultationGroundingTimelineYear
                                        ::yearOrder
                        )
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * unavailableReason
     * =================================================
     */

    private static Optional<String>
    normalizeUnavailableReason(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String value =
                source
                        .orElseThrow()
                        .trim();

        if (
                value.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Grounding unavailableReason은 빈 문자열일 수 없습니다."
            );
        }

        return Optional.of(
                value
        );
    }
}