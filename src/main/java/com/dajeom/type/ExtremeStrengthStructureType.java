package com.dajeom.type;

/**
 * 정밀화 4차-7.
 *
 * 신강·신약 일반 판정을 하기 전에
 * 확인하는 극단 구조 후보.
 *
 * 주의:
 *
 * FOLLOW_STRONG_CANDIDATE
 * FOLLOW_WEAK_CANDIDATE
 *
 * 는 종격 확정이 아니다.
 *
 * 실제 격국 확정은
 * 정밀화 5차 격국 분석에서
 * 다시 검토한다.
 */
public enum ExtremeStrengthStructureType {

    NORMAL(
            "일반 구조",
            false
    ),

    EXTREME_STRONG_CANDIDATE(
            "극강 후보",
            true
    ),

    EXTREME_WEAK_CANDIDATE(
            "극약 후보",
            true
    ),

    FOLLOW_STRONG_CANDIDATE(
            "종강 후보",
            true
    ),

    FOLLOW_WEAK_CANDIDATE(
            "종약 계열 후보",
            true
    );

    private final String koreanName;

    private final boolean extreme;

    ExtremeStrengthStructureType(
            String koreanName,
            boolean extreme
    ) {

        this.koreanName =
                koreanName;

        this.extreme =
                extreme;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean extreme() {

        return extreme;
    }

    public boolean normal() {

        return this == NORMAL;
    }

    public boolean extremeStrong() {

        return this
                == EXTREME_STRONG_CANDIDATE;
    }

    public boolean extremeWeak() {

        return this
                == EXTREME_WEAK_CANDIDATE;
    }

    public boolean followStrong() {

        return this
                == FOLLOW_STRONG_CANDIDATE;
    }

    public boolean followWeak() {

        return this
                == FOLLOW_WEAK_CANDIDATE;
    }
}