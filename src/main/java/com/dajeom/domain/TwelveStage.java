package com.dajeom.domain;

/**
 * 12운성.
 *
 * 순서:
 *
 * 장생 → 목욕 → 관대 → 건록 → 제왕 → 쇠
 * → 병 → 사 → 묘 → 절 → 태 → 양
 */
public enum TwelveStage {

    CHANG_SHENG(
            "長生",
            "장생"
    ),

    MU_YU(
            "沐浴",
            "목욕"
    ),

    GUAN_DAI(
            "冠帶",
            "관대"
    ),

    LIN_GUAN(
            "臨官",
            "건록"
    ),

    DI_WANG(
            "帝旺",
            "제왕"
    ),

    SHUAI(
            "衰",
            "쇠"
    ),

    BING(
            "病",
            "병"
    ),

    SI(
            "死",
            "사"
    ),

    MU(
            "墓",
            "묘"
    ),

    JUE(
            "絶",
            "절"
    ),

    TAI(
            "胎",
            "태"
    ),

    YANG(
            "養",
            "양"
    );

    private static final TwelveStage[] VALUES =
            values();

    private final String hanja;
    private final String korean;

    TwelveStage(
            String hanja,
            String korean
    ) {
        this.hanja = hanja;
        this.korean = korean;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }

    public static TwelveStage ofIndex(
            int index
    ) {

        return VALUES[
                Math.floorMod(
                        index,
                        VALUES.length
                )
                ];
    }

    @Override
    public String toString() {
        return korean;
    }
}