package com.dajeom.type;

/**
 * 정밀화 9차-7.
 *
 * 지지 결합 방해 검토의 대상 종류.
 *
 * =================================================
 *
 * SIX_COMBINATION
 * 9-4 六合 합화 후보
 *
 * THREE_COMBINATION
 * 9-5 三合·半合·부분조합 국 후보
 *
 * DIRECTIONAL_COMBINATION
 * 9-6 方合·부분方合 국 후보
 *
 * =================================================
 *
 * 9-7에서는 세 결합 계열을
 * 동일한 방해 분석 틀에서 검토한다.
 */
public enum EarthlyBranchCombinationCandidateKind {

    SIX_COMBINATION(
            "육합"
    ),

    THREE_COMBINATION(
            "삼합계"
    ),

    DIRECTIONAL_COMBINATION(
            "방합계"
    );

    private final String koreanName;

    EarthlyBranchCombinationCandidateKind(
            String koreanName
    ) {

        this.koreanName = koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean sixCombination() {

        return this == SIX_COMBINATION;
    }

    public boolean threeCombination() {

        return this == THREE_COMBINATION;
    }

    public boolean directionalCombination() {

        return this == DIRECTIONAL_COMBINATION;
    }
}