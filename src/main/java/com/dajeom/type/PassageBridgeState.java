package com.dajeom.type;

/**
 * 정밀화 6차-6.
 *
 * 상극 두 오행 사이를 연결하는
 * 중간 생조 오행이 현재 원국에서
 * 어느 정도 확보되어 있는지를 나타낸다.
 *
 * 예:
 *
 * 木剋土
 *
 * 木
 * ↓ 生
 * 火
 * ↓ 生
 * 土
 *
 * 여기서 火가 bridge.
 */
public enum PassageBridgeState {

    ABSENT(
            "통관 오행 거의 없음",
            0
    ),

    WEAK(
            "통관 오행 부족",
            1
    ),

    AVAILABLE(
            "통관 오행 존재",
            2
    ),

    STRONG(
            "통관 오행 충분",
            3
    );

    private final String koreanName;

    private final int rank;

    PassageBridgeState(
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

    public boolean absent() {

        return this == ABSENT;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean available() {

        return this == AVAILABLE;
    }

    public boolean strong() {

        return this == STRONG;
    }

    public boolean insufficient() {

        return this == ABSENT
                || this == WEAK;
    }

    public boolean alreadyFunctions() {

        return this == AVAILABLE
                || this == STRONG;
    }
}