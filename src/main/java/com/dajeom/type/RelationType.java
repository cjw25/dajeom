package com.dajeom.type;

/**
 * 사주 원국 내부에서 발생하는 천간·지지 관계다.
 */
public enum RelationType {

    STEM_COMBINATION(
            "천간합",
            "天干合"
    ),

    STEM_CONTROL(
            "천간극",
            "天干剋"
    ),

    SIX_HARMONY(
            "육합",
            "六合"
    ),

    THREE_HARMONY(
            "삼합",
            "三合"
    ),

    HALF_HARMONY(
            "반합",
            "半合"
    ),

    CLASH(
            "충",
            "沖"
    ),

    PUNISHMENT(
            "형",
            "刑"
    ),

    BREAK(
            "파",
            "破"
    ),

    HARM(
            "해",
            "害"
    );

    private final String koreanName;
    private final String chineseName;

    RelationType(
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