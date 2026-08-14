package com.dajeom.type;

/**
 * 정밀화 13차-1.
 *
 * 대운 진행 방향.
 *
 * =================================================
 *
 * FORWARD
 *
 * 순행.
 *
 * 월주를 기준으로 육십갑자의
 * 다음 간지 방향으로 진행한다.
 *
 * =================================================
 *
 * REVERSE
 *
 * 역행.
 *
 * 월주를 기준으로 육십갑자의
 * 이전 간지 방향으로 진행한다.
 *
 * =================================================
 *
 * 방향 자체는 길흉을 의미하지 않는다.
 */
public enum DecadeLuckDirection {

    FORWARD(
            "순행"
    ),

    REVERSE(
            "역행"
    );

    private final String koreanName;

    DecadeLuckDirection(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean forward() {

        return this == FORWARD;
    }

    public boolean reverse() {

        return this == REVERSE;
    }

    /**
     * 이후 13-3 대운 간지 Sequence에서
     * 월주로부터 움직일 육십갑자 Step.
     */
    public int cycleStep() {

        return forward()
                ? 1
                : -1;
    }
}