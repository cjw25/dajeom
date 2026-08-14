package com.dajeom.type;

/**
 * 일간을 기준으로 바라본
 * 오행의 십성 그룹.
 *
 * 이 타입은 정확한 개별 십성:
 *
 * 비견 / 겁재
 * 식신 / 상관
 * 편재 / 정재
 * 편관 / 정관
 * 편인 / 정인
 *
 * 을 구분하는 타입이 아니다.
 *
 * 신강·신약 판단에 필요한
 * 오행 단위의 큰 십성 그룹을 표현한다.
 *
 * COMPANION
 * 비겁
 *
 * RESOURCE
 * 인성
 *
 * OUTPUT
 * 식상
 *
 * WEALTH
 * 재성
 *
 * OFFICER
 * 관살
 */
public enum DayMasterElementRole {

    COMPANION(
            "비겁",
            "일간과 같은 오행",
            true,
            false,
            false
    ),

    RESOURCE(
            "인성",
            "일간을 생하는 오행",
            true,
            false,
            false
    ),

    OUTPUT(
            "식상",
            "일간이 생하는 오행",
            false,
            true,
            false
    ),

    WEALTH(
            "재성",
            "일간이 극하는 오행",
            false,
            true,
            false
    ),

    OFFICER(
            "관살",
            "일간을 극하는 오행",
            false,
            false,
            true
    );

    private final String koreanName;

    private final String description;

    /**
     * 일간을 생조하는 방향인지.
     *
     * 비겁 / 인성.
     */
    private final boolean supportive;

    /**
     * 일간이 힘을 사용하거나
     * 설기되는 방향인지.
     *
     * 식상 / 재성.
     */
    private final boolean consuming;

    /**
     * 일간을 직접 극하는 방향인지.
     *
     * 관살.
     */
    private final boolean controlling;

    DayMasterElementRole(

            String koreanName,

            String description,

            boolean supportive,

            boolean consuming,

            boolean controlling

    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.supportive =
                supportive;

        this.consuming =
                consuming;

        this.controlling =
                controlling;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public boolean supportive() {

        return supportive;
    }

    public boolean consuming() {

        return consuming;
    }

    public boolean controlling() {

        return controlling;
    }

    public boolean companion() {

        return this == COMPANION;
    }

    public boolean resource() {

        return this == RESOURCE;
    }

    public boolean output() {

        return this == OUTPUT;
    }

    public boolean wealth() {

        return this == WEALTH;
    }

    public boolean officer() {

        return this == OFFICER;
    }
}