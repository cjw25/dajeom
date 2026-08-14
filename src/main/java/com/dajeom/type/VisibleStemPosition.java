package com.dajeom.type;

/**
 * 원국에서 겉으로 드러난 천간의 위치.
 *
 * 년간
 * 월간
 * 일간
 * 시간
 *
 * 3차-1에서는 위치를 구분해 보존하지만
 * 위치만으로 임의의 세력 차이를 만들지는 않는다.
 *
 * 월령의 강한 계절 영향은 월간이 아니라
 * 월지/월령에서 발생하므로
 * MonthCommandAnalysis 단계에서 별도로 반영한다.
 */
public enum VisibleStemPosition {

    YEAR(
            "년간"
    ),

    MONTH(
            "월간"
    ),

    DAY(
            "일간"
    ),

    HOUR(
            "시간"
    );

    private final String koreanName;

    VisibleStemPosition(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean isYear() {

        return this == YEAR;
    }

    public boolean isMonth() {

        return this == MONTH;
    }

    public boolean isDay() {

        return this == DAY;
    }

    public boolean isHour() {

        return this == HOUR;
    }

    /**
     * 일간 여부.
     *
     * 일간은 신강·신약 판단의 기준점이기 때문에
     * 별도로 식별할 수 있어야 한다.
     */
    public boolean isDayMasterPosition() {

        return this == DAY;
    }
}