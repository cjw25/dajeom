package com.dajeom.type;

/**
 * 일간의 최종 신강·신약 단계.
 *
 * EXTREME_STRONG
 * 극강
 *
 * STRONG
 * 신강
 *
 * BALANCED_STRONG
 * 중화신강
 *
 * BALANCED
 * 중화
 *
 * BALANCED_WEAK
 * 중화신약
 *
 * WEAK
 * 신약
 *
 * EXTREME_WEAK
 * 극약
 *
 * 주의:
 *
 * EXTREME_STRONG 또는 EXTREME_WEAK이라고 해서
 * 종격이 확정되는 것은 아니다.
 *
 * 종강/종약 등의 격국 판단은
 * 정밀화 5차에서 별도로 수행한다.
 */
public enum DayMasterStrengthLevel {

    EXTREME_STRONG(
            "극강",
            7
    ),

    STRONG(
            "신강",
            6
    ),

    BALANCED_STRONG(
            "중화신강",
            5
    ),

    BALANCED(
            "중화",
            4
    ),

    BALANCED_WEAK(
            "중화신약",
            3
    ),

    WEAK(
            "신약",
            2
    ),

    EXTREME_WEAK(
            "극약",
            1
    );

    private final String koreanName;

    private final int rank;

    DayMasterStrengthLevel(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean extremeStrong() {

        return this == EXTREME_STRONG;
    }

    public boolean strong() {

        return this == STRONG;
    }

    public boolean balancedStrong() {

        return this == BALANCED_STRONG;
    }

    public boolean balanced() {

        return this == BALANCED;
    }

    public boolean balancedWeak() {

        return this == BALANCED_WEAK;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean extremeWeak() {

        return this == EXTREME_WEAK;
    }

    /**
     * 신강 계열.
     */
    public boolean strongSide() {

        return this == EXTREME_STRONG
                || this == STRONG
                || this == BALANCED_STRONG;
    }

    /**
     * 신약 계열.
     */
    public boolean weakSide() {

        return this == EXTREME_WEAK
                || this == WEAK
                || this == BALANCED_WEAK;
    }

    /**
     * 중화권에 들어가는 상태.
     */
    public boolean balancedZone() {

        return this == BALANCED_STRONG
                || this == BALANCED
                || this == BALANCED_WEAK;
    }

    public boolean extreme() {

        return extremeStrong()
                || extremeWeak();
    }
}