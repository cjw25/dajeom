package com.dajeom.type;

/**
 * 하나의 Meaning Claim이 허용하는 현실 구체성 경계.
 *
 * =================================================
 * STRUCTURE
 * =================================================
 *
 * 명리 구조 자체 수준.
 *
 * 예:
 * - 특정 기능 관계가 성립한다.
 * - 특정 Pattern 구조가 성립한다.
 *
 * =================================================
 * TENDENCY
 * =================================================
 *
 * 성향 / 작동 경향 수준.
 *
 * 정확한 10십성의 사용자 Personality Meaning은
 * 기본적으로 이 경계를 사용한다.
 *
 * =================================================
 * DOMAIN
 * =================================================
 *
 * 직업 / 재물 / 관계 등
 * 특정 생활영역의 deterministic 판단 수준.
 *
 * =================================================
 * TIMING
 * =================================================
 *
 * 특정 연도 / 시기 흐름 수준.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 enum의 선언 순서는
 * 의미 강도나 점수 순서가 아니다.
 *
 * Origin마다 허용되는 Specificity는
 * ConsultationMeaningSpecificityContract에서
 * 명시적으로 결정한다.
 *
 * 구체 사건 EVENT는 현재 공식 Specificity에 존재하지 않는다.
 * 따라서 별도의 deterministic 사건 계산 없이
 * 결혼/이별/퇴사/합격/질병/사고 같은 사건으로
 * 자동 확장할 수 없다.
 */
public enum ConsultationMeaningSpecificity {

    STRUCTURE(false),

    TENDENCY(false),

    DOMAIN(false),

    TIMING(true);

    private final boolean temporal;

    ConsultationMeaningSpecificity(
            boolean temporal
    ) {
        this.temporal = temporal;
    }

    /**
     * 연도 / 시기 Meaning인지.
     */
    public boolean temporal() {
        return temporal;
    }

    /**
     * 현재 공식 모델에 사건 수준 Specificity가 없음을
     * 호출부에서 명시적으로 확인할 때 사용하는 helper.
     */
    public boolean eventLevel() {
        return false;
    }
}