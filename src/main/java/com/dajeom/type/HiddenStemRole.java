package com.dajeom.type;

/**
 * 지장간의 역할.
 *
 * 여기(餘氣)
 * 중기(中氣)
 * 정기(正氣)
 */
public enum HiddenStemRole {

    RESIDUAL(
            "여기",
            "餘氣"
    ),

    MIDDLE(
            "중기",
            "中氣"
    ),

    MAIN(
            "정기",
            "正氣"
    );

    private final String koreanName;
    private final String chineseName;

    HiddenStemRole(
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