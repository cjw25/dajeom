package com.dajeom.type;

/**
 * 정밀화 14차-6.
 *
 * 세운 Context Evidence 상태.
 *
 * =================================================
 *
 * SUPPORTED
 *
 * 해당 작용을 직접 지지하는 근거.
 *
 * =================================================
 *
 * QUALIFIED
 *
 * 근거는 있으나 조건부 또는
 * 제한적인 해석이 필요한 상태.
 *
 * =================================================
 *
 * WEAKENED
 *
 * 기존 구조의 작동을 약화시키거나
 * 부담을 추가하는 근거.
 *
 * =================================================
 *
 * CONFLICTED
 *
 * 다른 강한 구조와 충돌하거나
 * 단순 해석이 불가능한 상태.
 *
 * =================================================
 *
 * 숫자 가중치를 부여하지 않는다.
 */
public enum AnnualLuckContextStatus {

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

    AnnualLuckContextStatus(
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