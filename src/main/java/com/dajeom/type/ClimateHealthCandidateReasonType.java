package com.dajeom.type;

/**
 * 정밀화 6차-8.
 *
 * 조후·병약·통관 통합 후보가
 * 현재 역할을 받은 주요 근거.
 *
 * 사용자에게 바로 출력하는 문구가 아니라
 * 엔진 내부 Explainability Evidence다.
 */
public enum ClimateHealthCandidateReasonType {

    CLIMATE_PRIMARY(
            "조후 1순위"
    ),

    CLIMATE_SECONDARY(
            "조후 보조 후보"
    ),

    CLIMATE_CONDITIONAL(
            "조후 조건부 후보"
    ),

    REMEDY_PRIMARY(
            "병약 1순위"
    ),

    REMEDY_SECONDARY(
            "병약 보조 후보"
    ),

    REMEDY_CONDITIONAL(
            "병약 조건부 후보"
    ),

    PASSAGE_PRIMARY(
            "통관 1순위"
    ),

    PASSAGE_SECONDARY(
            "통관 보조 후보"
    ),

    PASSAGE_CONDITIONAL(
            "통관 조건부 후보"
    ),

    DUAL_CONVERGENCE(
            "두 분석 방향 일치"
    ),

    TRIPLE_CONVERGENCE(
            "조후·병약·통관 모두 일치"
    ),

    SINGLE_HIGH_PRIORITY_SOURCE(
            "단일 고우선 근거"
    ),

    MULTIPLE_PATHOLOGIES_ADDRESSED(
            "복수 병 구조 동시 완화"
    ),

    DAY_MASTER_PASSAGE_SUPPORT(
            "일간 불리 상극 통관"
    ),

    CLIMATE_CONFLICT(
            "조후 충돌"
    ),

    REMEDY_CONFLICT(
            "병약 충돌"
    ),

    PASSAGE_CONFLICT(
            "통관 교차충돌"
    ),

    ELEMENT_SATURATED(
            "현재 오행 세력 과다"
    );

    private final String koreanName;

    ClimateHealthCandidateReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean convergenceReason() {

        return this == DUAL_CONVERGENCE
                || this == TRIPLE_CONVERGENCE;
    }

    public boolean conflictReason() {

        return this == CLIMATE_CONFLICT
                || this == REMEDY_CONFLICT
                || this == PASSAGE_CONFLICT
                || this == ELEMENT_SATURATED;
    }
}