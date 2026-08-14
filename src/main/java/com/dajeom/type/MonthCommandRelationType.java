package com.dajeom.type;

/**
 * 월령의 중심 오행과 일간 오행 사이의 관계.
 *
 * 판단 기준은 일간 관점이다.
 *
 * COMPANION
 * 월령과 일간이 같은 오행.
 *
 * RESOURCE
 * 월령이 일간을 생한다.
 *
 * OUTPUT
 * 일간이 월령을 생한다.
 *
 * WEALTH
 * 일간이 월령을 극한다.
 *
 * OFFICER
 * 월령이 일간을 극한다.
 *
 * 이 단계에서는 관계만 확정한다.
 *
 * 실제 세력 점수는 이후
 * ElementStrength / Strength 단계에서 계산한다.
 */
public enum MonthCommandRelationType {

    COMPANION(
            "동기",
            "월령과 일간이 같은 오행",
            true,
            false,
            false
    ),

    RESOURCE(
            "생조",
            "월령이 일간을 생하는 관계",
            true,
            false,
            false
    ),

    OUTPUT(
            "설기",
            "일간이 월령을 생하여 기운이 빠지는 관계",
            false,
            true,
            false
    ),

    WEALTH(
            "소모",
            "일간이 월령을 극하여 힘을 사용하는 관계",
            false,
            true,
            false
    ),

    OFFICER(
            "극제",
            "월령이 일간을 극하는 관계",
            false,
            false,
            true
    );

    private final String koreanName;

    private final String description;

    private final boolean supportive;

    private final boolean draining;

    private final boolean controlling;

    MonthCommandRelationType(
            String koreanName,
            String description,
            boolean supportive,
            boolean draining,
            boolean controlling
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.supportive =
                supportive;

        this.draining =
                draining;

        this.controlling =
                controlling;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    /**
     * 일간을 계절적으로 돕는 관계.
     *
     * 동기 또는 생조.
     */
    public boolean supportive() {

        return supportive;
    }

    /**
     * 일간이 힘을 사용하는 관계.
     *
     * 설기 또는 재성 방향.
     */
    public boolean draining() {

        return draining;
    }

    /**
     * 월령이 일간을 직접 극하는 관계.
     */
    public boolean controlling() {

        return controlling;
    }

    public boolean sameElement() {

        return this == COMPANION;
    }

    public boolean resourceSupport() {

        return this == RESOURCE;
    }

    public boolean outputDrain() {

        return this == OUTPUT;
    }

    public boolean wealthDrain() {

        return this == WEALTH;
    }

    public boolean officerPressure() {

        return this == OFFICER;
    }
}