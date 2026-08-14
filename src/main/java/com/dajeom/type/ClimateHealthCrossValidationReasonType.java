package com.dajeom.type;

/**
 * 정밀화 6차-9.
 *
 * 신강신약·격국 교차검증 근거.
 *
 * 사용자에게 바로 보여주는 문장이 아니라
 * Explainability용 내부 근거다.
 */
public enum ClimateHealthCrossValidationReasonType {

    WEAK_DAY_MASTER_SUPPORT(
            "신약 일간 보강 방향"
    ),

    WEAK_DAY_MASTER_PRESSURE(
            "신약 일간 추가 부담"
    ),

    STRONG_DAY_MASTER_RELEASE(
            "신강 일간 설기·소모·극제 방향"
    ),

    STRONG_DAY_MASTER_OVER_SUPPORT(
            "신강 일간 추가 생조"
    ),

    BALANCED_DAY_MASTER_NEUTRAL(
            "중화권 일간"
    ),

    FOLLOW_WEAK_DIRECTION_SUPPORTED(
            "종약 계열 방향과 일치"
    ),

    FOLLOW_WEAK_DIRECTION_CONTRADICTORY(
            "종약 계열 흐름을 되돌릴 가능성"
    ),

    FOLLOW_STRONG_DIRECTION_SUPPORTED(
            "종강 계열 방향과 일치"
    ),

    FOLLOW_STRONG_DIRECTION_CONTRADICTORY(
            "종강 계열 흐름을 깨뜨릴 가능성"
    ),

    PRIMARY_STRUCTURE_SUPPORT(
            "주격 성격 요소와 일치"
    ),

    PRIMARY_STRUCTURE_DAMAGE(
            "주격 파격 요소와 일치"
    ),

    PRIMARY_STRUCTURE_NEUTRAL(
            "주격과 직접 충돌 없음"
    ),

    SPECIAL_STRUCTURE_REVIEW(
            "특수격 추가 검토 필요"
    ),

    BASE_ROLE_PRESERVED(
            "6-8 역할 유지"
    ),

    PRIMARY_DOWNGRADED_TO_SECONDARY(
            "1순위에서 보조 후보로 하향"
    ),

    SELECTED_CANDIDATE_DEFERRED(
            "선택 후보 판정 보류"
    ),

    BASE_DEFERRED_PRESERVED(
            "기존 보류 상태 유지"
    ),

    BASE_REJECTED_PRESERVED(
            "기존 제외 상태 유지"
    );

    private final String koreanName;

    ClimateHealthCrossValidationReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean strengthReason() {

        return this == WEAK_DAY_MASTER_SUPPORT
                || this == WEAK_DAY_MASTER_PRESSURE
                || this == STRONG_DAY_MASTER_RELEASE
                || this == STRONG_DAY_MASTER_OVER_SUPPORT
                || this == BALANCED_DAY_MASTER_NEUTRAL
                || this == FOLLOW_WEAK_DIRECTION_SUPPORTED
                || this == FOLLOW_WEAK_DIRECTION_CONTRADICTORY
                || this == FOLLOW_STRONG_DIRECTION_SUPPORTED
                || this == FOLLOW_STRONG_DIRECTION_CONTRADICTORY;
    }

    public boolean structureReason() {

        return this == PRIMARY_STRUCTURE_SUPPORT
                || this == PRIMARY_STRUCTURE_DAMAGE
                || this == PRIMARY_STRUCTURE_NEUTRAL
                || this == SPECIAL_STRUCTURE_REVIEW;
    }

    public boolean roleChangeReason() {

        return this == BASE_ROLE_PRESERVED
                || this == PRIMARY_DOWNGRADED_TO_SECONDARY
                || this == SELECTED_CANDIDATE_DEFERRED
                || this == BASE_DEFERRED_PRESERVED
                || this == BASE_REJECTED_PRESERVED;
    }
}