package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차.
 *
 * 하나의 ConsultationCategory에 대한
 * LLM Grounding Section.
 *
 * =================================================
 *
 * 아직 최종 상담문이 아니다.
 *
 * LLM에게 제공할:
 *
 * - 상담 분야
 * - 원국 Evidence
 * - Timeline Scenario
 * - unavailable 상태
 *
 * 만 구조화한다.
 *
 * =================================================
 *
 * unavailable:
 *
 * Evidence가 나쁘다는 뜻이 아니다.
 *
 * 현재 deterministic 계산 범위만으로
 * 해당 View를 완성할 수 없다는 뜻이다.
 */
public record ConsultationGroundingSection(

        ConsultationCategory category,

        List<ConsultationGroundingEvidence> natalEvidences,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios,

        Optional<String> unavailableReason

) {

    public ConsultationGroundingSection {

        Objects.requireNonNull(
                category,
                "Grounding Section의 ConsultationCategory는 필수입니다."
        );

        natalEvidences =
                natalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        natalEvidences
                );

        for (
                ConsultationGroundingEvidence evidence
                : natalEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "Grounding Natal Evidence는 null일 수 없습니다."
            );
        }

        timelineScenarios =
                timelineScenarios == null
                        ? List.of()
                        : List.copyOf(
                        timelineScenarios
                );

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            Objects.requireNonNull(
                    scenario,
                    "Grounding Timeline Scenario는 null일 수 없습니다."
            );
        }

        unavailableReason =
                normalizeUnavailableReason(
                        unavailableReason
                );

        /*
         * =================================================
         * unavailable View 검증
         * =================================================
         *
         * 현재 deterministic 계산 범위에서
         * 만들 수 없는 Section에는
         * 가짜 Evidence를 같이 넣지 않는다.
         */

        if (
                unavailableReason.isPresent()
                        && (
                        !natalEvidences.isEmpty()
                                || !timelineScenarios.isEmpty()
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
     * Timeline
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

    /**
     * 가능한 Timeline Scenario 중 하나라도
     * 불확실성을 포함하면 true.
     *
     * 여러 Scenario 중 하나를 임의로 선택하지 않는다.
     */
    public boolean timelineUncertaintyExists() {

        return timelineScenarios
                .stream()
                .anyMatch(
                        ConsultationGroundingTimelineScenario
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * 전체 Evidence
     * =================================================
     *
     * 원국 Evidence
     * +
     * 각 가능한 Timeline Scenario의 Evidence.
     *
     * 여기서 중복 제거하거나
     * 상태를 하나로 합치지 않는다.
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
     * unavailableReason 정규화
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