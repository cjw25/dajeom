package com.dajeom.type;

/**
 * 일간이 월령과 맺는 계절적 상태.
 *
 * DIRECT_COMMAND_GAIN
 * 일간과 월령 중심 오행이 동일.
 * 엄격한 의미의 직접 득령.
 *
 * RESOURCE_SUPPORT
 * 월령이 일간을 생함.
 * 직접 득령은 아니지만 계절 생조를 받음.
 *
 * OUTPUT_DRAIN
 * 일간이 월령을 생함.
 * 식상 방향으로 설기.
 *
 * WEALTH_EXERTION
 * 일간이 월령을 극함.
 * 재성 방향으로 힘을 사용함.
 *
 * OFFICER_CONTROL
 * 월령이 일간을 극함.
 * 관살 방향의 계절 압력을 받음.
 */
public enum DayMasterSeasonalStatus {

    DIRECT_COMMAND_GAIN(
            "득령",
            "월령과 일간이 같은 오행으로 직접 계절의 중심 기운을 얻습니다.",
            true,
            true,
            false,
            false
    ),

    RESOURCE_SUPPORT(
            "월령 생조",
            "월령이 일간을 생하여 계절적인 공급을 받습니다.",
            false,
            true,
            false,
            false
    ),

    OUTPUT_DRAIN(
            "월령 설기",
            "일간이 월령을 생하여 계절 방향으로 기운을 내보냅니다.",
            false,
            false,
            true,
            false
    ),

    WEALTH_EXERTION(
            "월령 소모",
            "일간이 월령을 극하는 재성 방향으로 힘을 사용합니다.",
            false,
            false,
            true,
            false
    ),

    OFFICER_CONTROL(
            "월령 극제",
            "월령이 일간을 극하여 계절적인 압력을 가합니다.",
            false,
            false,
            false,
            true
    );

    private final String koreanName;

    private final String description;

    /**
     * 엄격한 의미의 직접 득령인가.
     */
    private final boolean directCommandGain;

    /**
     * 계절적으로 일간을 돕는 방향인가.
     *
     * 직접 득령 + 인성 생조.
     */
    private final boolean supportive;

    /**
     * 일간이 힘을 밖으로 사용하는 방향인가.
     */
    private final boolean consuming;

    /**
     * 일간이 월령으로부터 직접 극을 받는가.
     */
    private final boolean controlling;

    DayMasterSeasonalStatus(
            String koreanName,
            String description,
            boolean directCommandGain,
            boolean supportive,
            boolean consuming,
            boolean controlling
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.directCommandGain =
                directCommandGain;

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

    public boolean directCommandGain() {

        return directCommandGain;
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

    public boolean resourceSupport() {

        return this == RESOURCE_SUPPORT;
    }

    public boolean outputDrain() {

        return this == OUTPUT_DRAIN;
    }

    public boolean wealthExertion() {

        return this == WEALTH_EXERTION;
    }

    public boolean officerControl() {

        return this == OFFICER_CONTROL;
    }
}