package com.dajeom.type;

/**
 * 지장간의 위치와 역할.
 *
 * MAIN
 * 정기 / 본기
 * 해당 지지의 중심 기운.
 *
 * MIDDLE
 * 중기
 * 정기 다음으로 작용하는 내부 기운.
 *
 * RESIDUAL
 * 여기
 * 이전 계절이나 기운의 잔여 성분.
 *
 * 중요:
 * role 자체와 실제 세력 weight는 구분한다.
 *
 * 같은 MAIN이라도
 * 子의 癸와 午의 丁처럼
 * 실제 배분 비율은 지지별로 다를 수 있다.
 */
public enum HiddenStemRole {

    MAIN(
            "정기",
            "본기",
            3
    ),

    MIDDLE(
            "중기",
            "중기",
            2
    ),

    RESIDUAL(
            "여기",
            "여기",
            1
    );

    private final String koreanName;

    private final String traditionalName;

    private final int priority;

    HiddenStemRole(
            String koreanName,
            String traditionalName,
            int priority
    ) {

        this.koreanName =
                koreanName;

        this.traditionalName =
                traditionalName;

        this.priority =
                priority;
    }

    public String koreanName() {

        return koreanName;
    }

    public String traditionalName() {

        return traditionalName;
    }

    public int priority() {

        return priority;
    }

    public boolean isMain() {

        return this == MAIN;
    }

    public boolean isMiddle() {

        return this == MIDDLE;
    }

    public boolean isResidual() {

        return this == RESIDUAL;
    }
}