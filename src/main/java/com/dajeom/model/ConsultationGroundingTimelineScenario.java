package com.dajeom.model;

import java.util.List;

/**
 * 정밀화 20차.
 *
 * 하나의 가능한 Timeline Segment를
 * LLM Grounding용 Scenario로 변환한 결과.
 *
 * =================================================
 *
 * 출생시간 미상이나
 * 대운 교체 시점 불확실성 때문에
 * 동일 조회 시점에 여러 Segment가 가능하면:
 *
 * Scenario 1
 * Scenario 2
 * ...
 *
 * 로 각각 유지한다.
 *
 * =================================================
 *
 * 하나의 대표 Scenario를 고르지 않는다.
 *
 * POSSIBLE을 DEFINITE로 바꾸지 않는다.
 */
public record ConsultationGroundingTimelineScenario(

        int scenarioOrder,

        int sajuYear,

        int monthIndex,

        boolean fullyDefinite,

        boolean timelineUncertaintyExists,

        boolean activeDecade,

        boolean beforeFirstDecadeLuck,

        boolean afterGeneratedDecadeRange,

        boolean decadeUnavailable,

        List<ConsultationGroundingEvidence> evidences

) {

    public ConsultationGroundingTimelineScenario {

        if (
                scenarioOrder < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Timeline Scenario 순서는 1 이상이어야 합니다."
            );
        }

        if (
                monthIndex < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Timeline monthIndex는 1 이상이어야 합니다."
            );
        }

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        for (
                ConsultationGroundingEvidence evidence
                : evidences
        ) {

            if (
                    evidence == null
            ) {

                throw new IllegalArgumentException(
                        "Grounding Timeline Evidence는 null일 수 없습니다."
                );
            }
        }
    }

    public boolean hasEvidence() {

        return !evidences
                .isEmpty();
    }

    public int evidenceCount() {

        return evidences
                .size();
    }

    /**
     * 이 Scenario를 확정 Scenario라고
     * 사용자에게 설명해도 되는지.
     */
    public boolean definiteScenario() {

        return fullyDefinite
                && !timelineUncertaintyExists;
    }

    public boolean possibleScenario() {

        return !definiteScenario();
    }
}