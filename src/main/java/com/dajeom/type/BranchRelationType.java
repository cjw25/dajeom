package com.dajeom.type;

/**
 * 지지 사이의 관계 종류.
 *
 * V1 분석 대상:
 *
 * - 육합
 * - 삼합
 * - 충
 * - 형
 * - 자형
 * - 해
 * - 파
 */
public enum BranchRelationType {

    SIX_COMBINATION(
            "육합",
            "六合"
    ),

    THREE_HARMONY(
            "삼합",
            "三合"
    ),

    CLASH(
            "충",
            "沖"
    ),

    PUNISHMENT(
            "형",
            "刑"
    ),

    SELF_PUNISHMENT(
            "자형",
            "自刑"
    ),

    HARM(
            "해",
            "害"
    ),

    BREAK(
            "파",
            "破"
    );

    private final String koreanName;
    private final String chineseName;

    BranchRelationType(
            String koreanName,
            String chineseName
    ) {
        this.koreanName = koreanName;
        this.chineseName = chineseName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseName() {
        return chineseName;
    }
}