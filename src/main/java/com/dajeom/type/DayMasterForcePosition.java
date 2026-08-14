package com.dajeom.type;

import java.util.Objects;

/**
 * 득세 분석에서 사용하는
 * 일간 주변의 위치.
 *
 * 일간 자체인 DAY_STEM은 제외한다.
 *
 * 천간:
 *
 * YEAR_STEM
 * MONTH_STEM
 * HOUR_STEM
 *
 * 지지:
 *
 * YEAR_BRANCH
 * MONTH_BRANCH
 * DAY_BRANCH
 * HOUR_BRANCH
 */
public enum DayMasterForcePosition {

    YEAR_STEM(
            "년간",
            true,
            false,
            false
    ),

    MONTH_STEM(
            "월간",
            true,
            false,
            true
    ),

    HOUR_STEM(
            "시간",
            true,
            false,
            false
    ),

    YEAR_BRANCH(
            "년지",
            false,
            true,
            false
    ),

    MONTH_BRANCH(
            "월지",
            false,
            true,
            true
    ),

    DAY_BRANCH(
            "일지",
            false,
            true,
            true
    ),

    HOUR_BRANCH(
            "시지",
            false,
            true,
            false
    );

    private final String koreanName;

    private final boolean stemPosition;

    private final boolean branchPosition;

    /**
     * 월간·월지·일지를
     * 일간 주변의 중심 위치로 표시한다.
     *
     * 이것 자체가 별도의 가중치는 아니다.
     */
    private final boolean centralPosition;

    DayMasterForcePosition(
            String koreanName,
            boolean stemPosition,
            boolean branchPosition,
            boolean centralPosition
    ) {

        this.koreanName =
                koreanName;

        this.stemPosition =
                stemPosition;

        this.branchPosition =
                branchPosition;

        this.centralPosition =
                centralPosition;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean stemPosition() {

        return stemPosition;
    }

    public boolean branchPosition() {

        return branchPosition;
    }

    public boolean centralPosition() {

        return centralPosition;
    }

    public static DayMasterForcePosition
    fromVisibleStemPosition(
            VisibleStemPosition position
    ) {

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    YEAR_STEM;

            case MONTH ->
                    MONTH_STEM;

            case HOUR ->
                    HOUR_STEM;

            case DAY ->
                    throw new IllegalArgumentException(
                            "일간 자체는 득세 주변 위치에 포함하지 않습니다."
                    );
        };
    }

    public static DayMasterForcePosition
    fromBranchPosition(
            StemExposurePosition position
    ) {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    YEAR_BRANCH;

            case MONTH ->
                    MONTH_BRANCH;

            case DAY ->
                    DAY_BRANCH;

            case HOUR ->
                    HOUR_BRANCH;
        };
    }
}