package com.dajeom.type;

/**
 * 정밀화 6차-7.
 *
 * 통관 오행 후보의 역할이 결정된
 * 주요 내부 근거.
 *
 * 사용자용 해석 문장 자체가 아니라
 * Explainability Evidence다.
 */
public enum PassageCandidateReasonType {

    LOW_PASSAGE_NEED(
            "약한 통관 필요"
    ),

    MEANINGFUL_PASSAGE_NEED(
            "의미 있는 통관 필요"
    ),

    HIGH_PASSAGE_NEED(
            "높은 통관 필요"
    ),

    CRITICAL_PASSAGE_NEED(
            "매우 높은 통관 필요"
    ),

    BRIDGE_ABSENT(
            "통관 오행 거의 없음"
    ),

    BRIDGE_WEAK(
            "통관 오행 부족"
    ),

    BRIDGE_ALREADY_FUNCTIONING(
            "통관 오행이 이미 작동"
    ),

    DAY_MASTER_DISADVANTAGED(
            "일간이 상극 관계에서 불리"
    ),

    REMEDY_PRIMARY_ALIGNMENT(
            "병약 1순위와 통관 방향 일치"
    ),

    REMEDY_SECONDARY_ALIGNMENT(
            "병약 보조 후보와 통관 방향 일치"
    ),

    CLIMATE_PRIMARY_ALIGNMENT(
            "조후 1순위와 통관 방향 일치"
    ),

    CLIMATE_SECONDARY_ALIGNMENT(
            "조후 보조 후보와 통관 방향 일치"
    ),

    REMEDY_CONFLICT(
            "병약 방향과 충돌"
    ),

    CLIMATE_CONFLICT(
            "조후 방향과 충돌"
    ),

    ELEMENT_SATURATED(
            "통관 오행이 이미 과다"
    );

    private final String koreanName;

    PassageCandidateReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean conflictReason() {

        return this == REMEDY_CONFLICT
                || this == CLIMATE_CONFLICT
                || this == ELEMENT_SATURATED;
    }

    public boolean alignmentReason() {

        return this == REMEDY_PRIMARY_ALIGNMENT
                || this == REMEDY_SECONDARY_ALIGNMENT
                || this == CLIMATE_PRIMARY_ALIGNMENT
                || this == CLIMATE_SECONDARY_ALIGNMENT;
    }
}