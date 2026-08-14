package com.dajeom.type;

/**
 * 통근의 근원이 되는 지지가
 * 원국의 어느 위치에 있는지를 나타낸다.
 *
 * 다점 내부 통근 가중치:
 *
 * 월지 > 일지 > 시지 > 년지
 *
 * 단,
 * 이 값은 월령 자체의 계절 세력을 의미하지 않는다.
 *
 * 월령의 득령/실령은 이후 오행 세력 단계에서
 * 별도로 계산한다.
 *
 * 여기서는 같은 지장간 통근이라도
 * 원국의 위치에 따른 상대적 작용 차이만 반영한다.
 */
public enum RootSourcePosition {

    YEAR(
            "년지",
            90
    ),

    MONTH(
            "월지",
            115
    ),

    DAY(
            "일지",
            110
    ),

    HOUR(
            "시지",
            100
    );

    private final String koreanName;

    /**
     * 위치 가중치.
     *
     * 100을 기준으로 한 상대값이며
     * 확률을 의미하지 않는다.
     */
    private final int weightRate;

    RootSourcePosition(
            String koreanName,
            int weightRate
    ) {

        this.koreanName =
                koreanName;

        this.weightRate =
                weightRate;
    }

    public String koreanName() {

        return koreanName;
    }

    public int weightRate() {

        return weightRate;
    }

    public boolean monthBranch() {

        return this == MONTH;
    }

    public boolean dayBranch() {

        return this == DAY;
    }
}