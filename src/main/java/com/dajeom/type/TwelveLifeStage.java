package com.dajeom.type;

/**
 * 정밀화 11차-1.
 *
 * 십간 12운성.
 *
 * =================================================
 *
 * 순서:
 *
 * 장생
 * 목욕
 * 관대
 * 임관
 * 제왕
 * 쇠
 * 병
 * 사
 * 묘
 * 절
 * 태
 * 양
 *
 * =================================================
 *
 * cycleIndex는 순환상의 위치만 의미한다.
 *
 * 절대로:
 *
 * - 길흉 점수
 * - 오행 세력 점수
 * - 신강신약 가중치
 *
 * 로 직접 사용하지 않는다.
 *
 * 12운성은 3차 오행 세력과 별개의
 * 구조적·상태적 Evidence로 유지한다.
 */
public enum TwelveLifeStage {

    CHANG_SHENG(
            "長生",
            "장생",
            0
    ),

    MU_YU(
            "沐浴",
            "목욕",
            1
    ),

    GUAN_DAI(
            "冠帶",
            "관대",
            2
    ),

    LIN_GUAN(
            "臨官",
            "임관",
            3
    ),

    DI_WANG(
            "帝旺",
            "제왕",
            4
    ),

    SHUAI(
            "衰",
            "쇠",
            5
    ),

    BING(
            "病",
            "병",
            6
    ),

    SI(
            "死",
            "사",
            7
    ),

    MU(
            "墓",
            "묘",
            8
    ),

    JUE(
            "絶",
            "절",
            9
    ),

    TAI(
            "胎",
            "태",
            10
    ),

    YANG(
            "養",
            "양",
            11
    );

    private final String chineseName;

    private final String koreanName;

    private final int cycleIndex;

    TwelveLifeStage(
            String chineseName,
            String koreanName,
            int cycleIndex
    ) {

        this.chineseName =
                chineseName;

        this.koreanName =
                koreanName;

        this.cycleIndex =
                cycleIndex;
    }

    public String chineseName() {

        return chineseName;
    }

    public String koreanName() {

        return koreanName;
    }

    /**
     * 0 ~ 11의 순환 위치.
     *
     * 길흉 또는 강약 순위가 아니다.
     */
    public int cycleIndex() {

        return cycleIndex;
    }

    public boolean changSheng() {

        return this == CHANG_SHENG;
    }

    public boolean muYu() {

        return this == MU_YU;
    }

    public boolean guanDai() {

        return this == GUAN_DAI;
    }

    public boolean linGuan() {

        return this == LIN_GUAN;
    }

    public boolean diWang() {

        return this == DI_WANG;
    }

    public boolean shuai() {

        return this == SHUAI;
    }

    public boolean bing() {

        return this == BING;
    }

    public boolean si() {

        return this == SI;
    }

    public boolean mu() {

        return this == MU;
    }

    public boolean jue() {

        return this == JUE;
    }

    public boolean tai() {

        return this == TAI;
    }

    public boolean yang() {

        return this == YANG;
    }
}