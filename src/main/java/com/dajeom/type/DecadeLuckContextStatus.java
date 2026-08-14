package com.dajeom.type;

/**
 * 정밀화 13차-6.
 *
 * 대운 Context의 근거 상태.
 *
 * =================================================
 *
 * SUPPORTED
 *
 * 해당 구조를 직접 지지하는
 * 명확한 근거.
 *
 * =================================================
 *
 * QUALIFIED
 *
 * 작용 근거는 있으나
 * 조건부이거나 제한적으로 해석해야 하는 상태.
 *
 * =================================================
 *
 * WEAKENED
 *
 * 해당 구조의 발현을 약화시키거나
 * 부담을 추가하는 근거.
 *
 * =================================================
 *
 * CONFLICTED
 *
 * 다른 강한 구조와 직접 충돌하거나
 * 해석 방향을 단순화할 수 없는 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 enum에는 숫자 점수를 두지 않는다.
 *
 * SUPPORTED = +100
 * CONFLICTED = -100
 *
 * 같은 계산을 하지 않는다.
 */
public enum DecadeLuckContextStatus {

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

    DecadeLuckContextStatus(
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