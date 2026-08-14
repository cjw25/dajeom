package com.dajeom.type;

/**
 * 투간 분석에서 사용하는 원국 위치.
 *
 * SOURCE로 사용할 경우:
 * 년지 / 월지 / 일지 / 시지
 *
 * TARGET으로 사용할 경우:
 * 년간 / 월간 / 일간 / 시간
 *
 * 동일 enum을 사용하되
 * Evidence에서 source/target 의미를 명확히 분리한다.
 */
public enum StemExposurePosition {

    YEAR(
            "년"
    ),

    MONTH(
            "월"
    ),

    DAY(
            "일"
    ),

    HOUR(
            "시"
    );

    private final String koreanName;

    StemExposurePosition(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public String branchName() {

        return koreanName + "지";
    }

    public String stemName() {

        return koreanName + "간";
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
}