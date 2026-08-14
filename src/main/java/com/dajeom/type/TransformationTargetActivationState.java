package com.dajeom.type;

/**
 * 정밀화 9차-9.
 *
 * 合化·三合·方合·六合에 의해
 * 목표 오행 기능이 어느 정도 활성화되는지를 표현한다.
 *
 * =================================================
 *
 * INACTIVE
 *
 * 목표 오행으로의 실제 변화가
 * 현재 구조에서는 성립하지 않는다.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 목표 오행 방향은 존재하지만
 * 실제 적용에는 조건이 남아 있다.
 *
 * =================================================
 *
 * PARTIAL
 *
 * 목표 오행 작용이 의미 있게
 * 부분적으로 활성화된다.
 *
 * =================================================
 *
 * STRONG
 *
 * 목표 오행 작용이 현재 구조의
 * 주요 변화 방향으로 강하게 활성화된다.
 *
 * =================================================
 *
 * 이 enum 역시 실제 오행 Power를
 * 즉시 수정하는 값이 아니다.
 */
public enum TransformationTargetActivationState {

    INACTIVE(
            "목표 오행 비활성",
            1
    ),

    CONDITIONAL(
            "목표 오행 조건부 활성",
            2
    ),

    PARTIAL(
            "목표 오행 부분 활성",
            3
    ),

    STRONG(
            "목표 오행 강한 활성",
            4
    );

    private final String koreanName;

    private final int rank;

    TransformationTargetActivationState(
            String koreanName,
            int rank
    ) {

        this.koreanName = koreanName;
        this.rank = rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean partial() {

        return this == PARTIAL;
    }

    public boolean strong() {

        return this == STRONG;
    }

    public boolean active() {

        return partial()
                || strong();
    }

    public boolean candidateRemains() {

        return !inactive();
    }
}