package com.dajeom.constant;

import com.dajeom.type.HiddenStemRole;

/**
 * 다점 오행 세력 계산 V1에 사용하는 가중치다.
 *
 * 이 값은 명리학의 절대 공식이 아니라
 * 다점의 검증 가능한 계산 정책이다.
 *
 * 운영 과정에서 표본 검증을 거쳐
 * 버전 단위로 조정할 수 있다.
 */
public final class ElementStrengthWeightTable {

    public static final String RULE_VERSION =
            "DAJEOM_ELEMENT_STRENGTH_V1";

    /**
     * 년간·월간·일간·시간에 적용되는 기본 점수.
     */
    public static final double VISIBLE_STEM_WEIGHT =
            10.0;

    /**
     * 월지는 계절의 중심이므로
     * 다른 지지보다 강한 배수를 적용한다.
     */
    public static final double MONTH_BRANCH_MULTIPLIER =
            2.0;

    /**
     * 년지·일지·시지에는 기본 배수를 적용한다.
     */
    public static final double NORMAL_BRANCH_MULTIPLIER =
            1.0;

    private ElementStrengthWeightTable() {
    }

    /**
     * 지장간 역할별 기본 세력 점수를 반환한다.
     */
    public static double hiddenStemWeight(
            HiddenStemRole role
    ) {
        return switch (role) {
            case MAIN -> 12.0;
            case MIDDLE -> 7.0;
            case RESIDUAL -> 4.0;
        };
    }
}