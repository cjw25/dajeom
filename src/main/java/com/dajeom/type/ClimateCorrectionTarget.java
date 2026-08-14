package com.dajeom.type;

/**
 * 정밀화 6차-3.
 *
 * 조후 오행 후보가 담당하는
 * 구체적인 환경 보정 목적.
 */
public enum ClimateCorrectionTarget {

    WARMING(
            "한기 완화"
    ),

    COOLING(
            "열기 완화"
    ),

    MOISTENING(
            "조기 완화"
    ),

    DRYING(
            "습기 완화"
    ),

    /**
     * 직접적인 한난조습 교정보다는
     * 다른 조후 후보가 작동할 수 있도록
     * 생조·연결하는 보조 후보.
     *
     * 최종 통관과는 다른 개념이다.
     *
     * 통관은 6-6/6-7에서 별도 처리한다.
     */
    BRIDGING(
            "조후 보조 연결"
    );

    private final String koreanName;

    ClimateCorrectionTarget(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean temperatureTarget() {

        return this == WARMING
                || this == COOLING;
    }

    public boolean moistureTarget() {

        return this == MOISTENING
                || this == DRYING;
    }

    public boolean bridgeTarget() {

        return this == BRIDGING;
    }
}