package com.dajeom.type;

/**
 * 정밀화 15차-7.
 *
 * 월운 Context Evidence 상태.
 *
 * =================================================
 *
 * SUPPORTED
 *
 * 해당 작용을 직접 지지하는 근거.
 *
 * QUALIFIED
 *
 * 작용은 존재하지만 조건부 판단이 필요한 근거.
 *
 * WEAKENED
 *
 * 기존 작동을 약화시키거나 부담을 추가하는 근거.
 *
 * CONFLICTED
 *
 * 다른 구조와 직접 충돌하거나
 * 단순하게 한 방향으로 해석할 수 없는 근거.
 *
 * =================================================
 *
 * 숫자 점수를 의미하지 않는다.
 */
public enum MonthlyLuckContextStatus {

    SUPPORTED(
            "지지"
    ),

    QUALIFIED(
            "조건부"
    ),

    WEAKENED(
            "약화"
    ),

    CONFLICTED(
            "충돌"
    );

    private final String koreanName;

    MonthlyLuckContextStatus(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean supported() {

        return this == SUPPORTED;
    }

    public boolean qualified() {

        return this == QUALIFIED;
    }

    public boolean weakened() {

        return this == WEAKENED;
    }

    public boolean conflicted() {

        return this == CONFLICTED;
    }
}