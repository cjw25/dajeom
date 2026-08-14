package com.dajeom.type;

/**
 * 월령 기준 오행의 계절 상태.
 *
 * 전통적인 旺相休囚死 구조를
 * 다점 내부 계산 모델로 표현한다.
 *
 * PROSPEROUS
 * 旺(왕)
 * 월령과 같은 오행.
 *
 * ASSISTED
 * 相(상)
 * 월령이 생하는 오행.
 *
 * RESTING
 * 休(휴)
 * 월령을 생하는 오행.
 *
 * CONFINED
 * 囚(수)
 * 월령을 극하는 오행.
 *
 * DECLINED
 * 死(사)
 * 월령에게 극을 받는 오행.
 *
 * seasonalRate는 다점 엔진 내부의
 * 상대 보정률이다.
 *
 * 확률이나 전통 명리의 절대적인
 * 정량 수치를 의미하지 않는다.
 *
 * 실제 최종 세력은
 *
 * 표면 세력
 * + 지장간
 * + 월령
 * + 통근
 * + 투간
 * + 생극
 *
 * 을 종합해서 결정한다.
 */
public enum SeasonalPowerState {

    PROSPEROUS(
            "왕",
            "旺",
            120,
            5
    ),

    ASSISTED(
            "상",
            "相",
            110,
            4
    ),

    RESTING(
            "휴",
            "休",
            100,
            3
    ),

    CONFINED(
            "수",
            "囚",
            90,
            2
    ),

    DECLINED(
            "사",
            "死",
            80,
            1
    );

    private final String koreanName;

    private final String chineseName;

    /**
     * 100을 기준으로 한
     * 다점 내부 상대 계절 보정률.
     */
    private final int seasonalRate;

    /**
     * 계절 상태 순위.
     *
     * 최종 점수가 아니라
     * 상태 비교용이다.
     */
    private final int rank;

    SeasonalPowerState(
            String koreanName,
            String chineseName,
            int seasonalRate,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.chineseName =
                chineseName;

        this.seasonalRate =
                seasonalRate;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public String chineseName() {

        return chineseName;
    }

    public int seasonalRate() {

        return seasonalRate;
    }

    public int rank() {

        return rank;
    }

    public boolean prosperous() {

        return this == PROSPEROUS;
    }

    public boolean assisted() {

        return this == ASSISTED;
    }

    public boolean resting() {

        return this == RESTING;
    }

    public boolean confined() {

        return this == CONFINED;
    }

    public boolean declined() {

        return this == DECLINED;
    }

    /**
     * 계절상 평균 이상으로
     * 힘을 확보하는 상태인지.
     */
    public boolean seasonallyFavorable() {

        return this == PROSPEROUS
                || this == ASSISTED;
    }

    /**
     * 계절상 억제되는 상태인지.
     */
    public boolean seasonallyRestricted() {

        return this == CONFINED
                || this == DECLINED;
    }
}