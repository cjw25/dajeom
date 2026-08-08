package com.dajeom.type;

/**
 * 천간 사이의 관계 종류.
 *
 * V1에서는 천간 오합을 우선 분석한다.
 *
 * 실제 합화 성립 여부는
 * 단순히 두 천간이 만났다고 확정하지 않는다.
 * 이후 별도의 구조/조건 분석에서 판단한다.
 */
public enum StemRelationType {

    COMBINATION(
            "천간합",
            "天干合"
    );

    private final String koreanName;
    private final String chineseName;

    StemRelationType(
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