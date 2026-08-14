package com.dajeom.type;

/**
 * 정밀화 10차-3.
 *
 * 식신·상관 복합구조 유형.
 *
 * =================================================
 *
 * 하나의 명식에 여러 Pattern이
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 식상 발달
 * +
 * 상관 중심
 * +
 * 신약 식상 과설
 *
 * 을 동시에 보존할 수 있다.
 *
 * =================================================
 *
 * 이번 단계에서는:
 *
 * 상관견관
 * 식신제살
 * 상관패인
 * 효신탈식
 *
 * 같은 특수 연쇄 Pattern을
 * 확정하지 않는다.
 *
 * 해당 구조는 10-8에서 처리한다.
 */
public enum OutputCompositePatternType {

    OUTPUT_DEVELOPED(
            "식상 발달"
    ),

    EATING_GOD_CENTERED(
            "식신 중심"
    ),

    HURTING_OFFICER_CENTERED(
            "상관 중심"
    ),

    MIXED_OUTPUT(
            "식신·상관 혼재"
    ),

    WEAK_DAY_MASTER_EXCESSIVE_DRAIN(
            "신약 식상 과설"
    ),

    RESOURCE_CONSTRAINED_OUTPUT(
            "인성 제약 식상"
    );

    private final String koreanName;

    OutputCompositePatternType(
            String koreanName
    ) {

        this.koreanName = koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean developed() {

        return this == OUTPUT_DEVELOPED;
    }

    public boolean eatingGodCentered() {

        return this == EATING_GOD_CENTERED;
    }

    public boolean hurtingOfficerCentered() {

        return this == HURTING_OFFICER_CENTERED;
    }

    public boolean mixed() {

        return this == MIXED_OUTPUT;
    }

    public boolean weakDayMasterExcessiveDrain() {

        return this == WEAK_DAY_MASTER_EXCESSIVE_DRAIN;
    }

    public boolean resourceConstrained() {

        return this == RESOURCE_CONSTRAINED_OUTPUT;
    }
}