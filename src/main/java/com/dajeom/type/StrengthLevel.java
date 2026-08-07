package com.dajeom.type;

/**
 * 일간의 강약 단계를 나타낸다.
 *
 * 단순한 신강·신약 이분법이 아니라
 * 7단계로 구분한다.
 */
public enum StrengthLevel {

    EXTREMELY_WEAK(
            "극신약",
            "極身弱"
    ),

    WEAK(
            "신약",
            "身弱"
    ),

    SLIGHTLY_WEAK(
            "약한 편",
            "偏弱"
    ),

    BALANCED(
            "중화",
            "中和"
    ),

    SLIGHTLY_STRONG(
            "강한 편",
            "偏強"
    ),

    STRONG(
            "신강",
            "身強"
    ),

    EXTREMELY_STRONG(
            "극신강",
            "極身強"
    );

    private final String koreanName;
    private final String chineseName;

    StrengthLevel(
            String koreanName,
            String chineseName
    ) {
        this.koreanName = koreanName;
        this.chineseName = chineseName;
    }

    /**
     * 0~100 범위의 강약 점수를 단계로 변환한다.
     */
    public static StrengthLevel fromScore(
            double score
    ) {
        if (score < 20.0) {
            return EXTREMELY_WEAK;
        }

        if (score < 40.0) {
            return WEAK;
        }

        if (score < 46.0) {
            return SLIGHTLY_WEAK;
        }

        if (score <= 55.0) {
            return BALANCED;
        }

        if (score <= 65.0) {
            return SLIGHTLY_STRONG;
        }

        if (score <= 80.0) {
            return STRONG;
        }

        return EXTREMELY_STRONG;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseName() {
        return chineseName;
    }
}